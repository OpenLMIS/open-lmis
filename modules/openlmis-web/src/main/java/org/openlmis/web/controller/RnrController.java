package org.openlmis.web.controller;

import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RnrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RnrController {

    private RnrService rnrService;

    public RnrController() {
    }

    @Autowired
    public RnrController(RnrService rnrService) {
        this.rnrService = rnrService;
    }

    @RequestMapping(value = "/logistics/rnr/{facilityCode}/{programCode}/init", method = RequestMethod.POST, headers = "Accept=application/json")
    public Rnr initRnr(@PathVariable("facilityCode") String facilityCode, @PathVariable("programCode") String programCode) {
        return rnrService.initRnr(facilityCode, programCode, "user");
    }

}
