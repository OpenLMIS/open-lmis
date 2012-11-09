package org.openlmis.web.controller;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionHeader;
import org.openlmis.core.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class FacilityController {

    @Autowired
    private FacilityService facilityService;

    @RequestMapping(value = "facilities", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<Facility> getAll() {
        return facilityService.getAll();
    }

    @RequestMapping(value = "/facility/{code}/requisition-header", method = RequestMethod.GET, headers = "Accept=application/json")
    public RequisitionHeader getRequisitionHeader(@PathVariable(value = "code") String code) {
        return facilityService.getRequisitionHeader(code);
    }

}
