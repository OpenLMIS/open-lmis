package org.openlmis.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RequisitionController {

    @RequestMapping(value = "/logistics/rnr/{facilityCode}/{programCode}/init", method = RequestMethod.POST, headers = "Accept=application/json")
    public String initRnr(@PathVariable("facilityCode") String facilityCode, @PathVariable("programCode") String programCode) {
        return "done";
    }

}
