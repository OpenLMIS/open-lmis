package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionHeader;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.web.model.ReferenceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;

@Controller
@NoArgsConstructor
public class FacilityController extends BaseController {

    private FacilityService facilityService;
    private ProgramService programService;

    @Autowired
    public FacilityController(FacilityService facilityService, ProgramService programService) {
        this.facilityService = facilityService;
        this.programService = programService;
    }

    @RequestMapping(value = "logistics/facilities", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<Facility> getAll() {
        return facilityService.getAll();
    }

    @RequestMapping(value = "logistics/user/facilities", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<Facility> getAllByUser(HttpServletRequest httpServletRequest) {
        return facilityService.getAllForUser(loggedInUser(httpServletRequest));
    }

    @RequestMapping(value = "logistics/facility/{code}/requisition-header", method = RequestMethod.GET, headers = "Accept=application/json")
    public RequisitionHeader getRequisitionHeader(@PathVariable(value = "code") String code) {
        return facilityService.getRequisitionHeader(code);
    }

    @RequestMapping(value = "admin/facility/reference-data", method = RequestMethod.GET, headers = "Accept=application/json")
    public Map getReferenceData() {
        ReferenceData referenceData = new ReferenceData();
        return referenceData.addFacilityTypes(facilityService.getAllTypes()).
                addFacilityOperators(facilityService.getAllOperators()).
                addGeographicZones(facilityService.getAllZones()).
                addPrograms(programService.getAll()).get();
    }

    @RequestMapping(value = "admin/facility" , method = RequestMethod.POST , headers = "Accept=application/json")
    public ResponseEntity<ModelMap> addFacility(@RequestBody Facility facility, HttpServletRequest request) {
        ModelMap modelMap = new ModelMap();
        String modifiedBy = (String) request.getSession().getAttribute(USER);
        facility.setModifiedBy(modifiedBy);
        try{
            facilityService.save(facility);
        }catch (RuntimeException exception){
            modelMap.put("error",exception.getMessage());
            return new ResponseEntity<>(modelMap,HttpStatus.BAD_REQUEST);
        }
        modelMap.put("success", facility.getName()+" created successfully");
        return new ResponseEntity<>(modelMap,HttpStatus.OK);
    }


}
