package org.openlmis.web.controller.cubesreports;

import lombok.NoArgsConstructor;
import org.openlmis.web.controller.cubesreports.validation.CubesAccessInfo;
import org.openlmis.web.controller.cubesreports.validation.CubesReportValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

import static java.net.URLDecoder.decode;
import static org.springframework.http.HttpEntity.EMPTY;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@NoArgsConstructor
public class CubesReportProxyController {

    private static final String CUBES_ADDRESS = "http://localhost:5555";
    private static final String CUBES_REQUEST_PREFIX = "\\/cubesreports";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CubesReportValidationService cubesReportValidationService;

    @RequestMapping(method = GET, value = "/cubesreports/**")
    public ResponseEntity redirect(HttpServletRequest request) throws UnsupportedEncodingException {
        String queryUri = request.getRequestURI().replaceFirst(CUBES_REQUEST_PREFIX, "");
        String queryString = request.getQueryString() == null ? "" : "?" + decode(request.getQueryString(), "UTF-8");

        CubesAccessInfo cubesAccessInfo = cubesReportValidationService.validate(queryUri, queryString);
        if (cubesAccessInfo.isValid()) {
            String cubesRequestUrl = CUBES_ADDRESS + queryUri + cubesAccessInfo.getCubesQueryString();
            return restTemplate.exchange(cubesRequestUrl, HttpMethod.GET, EMPTY, String.class);
        } else {
            return new ResponseEntity(FORBIDDEN);
        }
    }
}
