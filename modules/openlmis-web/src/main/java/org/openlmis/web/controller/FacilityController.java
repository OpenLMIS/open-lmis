package org.openlmis.web.controller;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionHeader;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.web.model.ReferenceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

@Controller
public class FacilityController {

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private ProgramService programService;

    @RequestMapping(value = "logistics/facilities", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<Facility> getAll() {
        return facilityService.getAll();
    }

    @RequestMapping(value = "logistics/facility/{code}/requisition-header", method = RequestMethod.GET, headers = "Accept=application/json")
    public RequisitionHeader getRequisitionHeader(@PathVariable(value = "code") String code) {
        return facilityService.getRequisitionHeader(code);
    }

    @RequestMapping(value = "admin/facility/reference-data",method = RequestMethod.GET , headers = "Accept=application/json")
    public Map getReferenceData() {
        ReferenceData referenceData = new ReferenceData();
        return referenceData.addFacilityTypes(facilityService.getAllTypes()).
                addFacilityOperators(facilityService.getAllOperators()).
                addGeographicZones(facilityService.getAllZones()).
                addPrograms(programService.getAll()).get();
    }

    @RequestMapping(value = "admin/facility" , method = RequestMethod.POST , headers = "Accept=application/json")
    public void addFacility(@RequestBody Facility facility) {
        facilityService.save(facility);
    }



}
