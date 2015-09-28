package org.openlmis.web.controller;

import org.openlmis.core.service.StaticReferenceDataService;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.HttpStatus.OK;

@Controller
public class HealthCheckController {

    @Autowired
    private StaticReferenceDataService service;

    @RequestMapping(value = {"/health/ping"}, method = RequestMethod.GET)
    public ResponseEntity<?> ping() {
        return new ResponseEntity<String>(OK);
    }

    @RequestMapping(value = "/release", method = RequestMethod.GET)
    public ResponseEntity getRelease() {
        String appVersion = service.getPropertyValue("app.version");
        return OpenLmisResponse.response("version", appVersion, OK, "application/json");
    }
}
