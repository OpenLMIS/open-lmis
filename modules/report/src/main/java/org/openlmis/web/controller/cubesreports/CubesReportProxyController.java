package org.openlmis.web.controller.cubesreports;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@NoArgsConstructor
public class CubesReportProxyController {

    @Autowired
    private CubesReportProxy cubesReportProxy;

    @RequestMapping(method = GET, value = "/cubesreports/**")
    public ResponseEntity redirect(HttpServletRequest request) throws UnsupportedEncodingException {

        return cubesReportProxy.redirect(request);
    }
}
