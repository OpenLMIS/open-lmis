package org.openlmis.web.controller;

import org.openlmis.rnr.service.RnrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

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
    public Map<String, String> initRnr(@PathVariable("facilityCode") String facilityCode, @PathVariable("programCode") String programCode) {
        HashMap<String, String> params = new HashMap<>();
        int rnrId = rnrService.initRnr(facilityCode, programCode);
        params.put("rnrId", String.valueOf(rnrId));
        return params;
    }

}
