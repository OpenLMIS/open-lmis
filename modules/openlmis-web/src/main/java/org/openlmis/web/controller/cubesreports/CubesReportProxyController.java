package org.openlmis.web.controller.cubesreports;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@NoArgsConstructor
public class CubesReportProxyController {

    private static final String CUBES_ADDRESS = "http://localhost:5555";

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(method = GET, value = "/cubesreports/**")
    public ResponseEntity redirect(HttpServletRequest request) {
        String cubesRequestUrl = CUBES_ADDRESS + request.getRequestURI().replaceFirst("\\/cubesreports", "") + "?" + request.getQueryString();
        return restTemplate.exchange(cubesRequestUrl, HttpMethod.GET, HttpEntity.EMPTY, String.class);
    }
}
