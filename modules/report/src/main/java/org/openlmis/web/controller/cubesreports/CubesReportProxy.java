package org.openlmis.web.controller.cubesreports;

import lombok.NoArgsConstructor;
import org.openlmis.web.controller.cubesreports.validation.CubesAccessInfo;
import org.openlmis.web.controller.cubesreports.validation.CubesReportValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

import static java.net.URLDecoder.decode;
import static org.springframework.http.HttpEntity.EMPTY;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Component
@NoArgsConstructor
public class CubesReportProxy {

    private static final String CUBES_ADDRESS = "http://localhost:5555";
    private static final String CUBES_REQUEST_PREFIX = "\\/cubesreports";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CubesReportValidationService cubesReportValidationService;

    public ResponseEntity redirect(HttpServletRequest request) throws UnsupportedEncodingException {
        String queryUri = request.getRequestURI().replaceFirst(CUBES_REQUEST_PREFIX, "");
        String queryString = request.getQueryString() == null ? "" : "?" + decode(request.getQueryString(), "UTF-8");
        return redirect(queryUri, queryString);
    }

    public ResponseEntity redirect(String queryUri, String queryString) {
        CubesAccessInfo cubesAccessInfo = cubesReportValidationService.validate(queryUri, queryString);
        if (cubesAccessInfo.isValid()) {
            String cubesRequestUrl = CUBES_ADDRESS + queryUri + cubesAccessInfo.getCubesQueryString();
            return restTemplate.exchange(cubesRequestUrl, HttpMethod.GET, EMPTY, String.class);
        } else {
            return new ResponseEntity(FORBIDDEN);
        }
    }

}
