package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.RequisitionHeader;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.web.model.FacilityReferenceData;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.openlmis.core.domain.Right.AUTHORIZE_REQUISITION;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;
import static org.openlmis.web.response.OpenLmisResponse.error;

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

  @RequestMapping(value = "admin/facilities", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','MANAGE_FACILITY')")
  public List<Facility> getAll() {
    return facilityService.getAll();
  }

  @RequestMapping(value = "logistics/user/facilities", method = RequestMethod.GET, headers = "Accept=application/json")
  public List<Facility> getAllByUser(HttpServletRequest httpServletRequest) {
    return facilityService.getAllForUser(loggedInUser(httpServletRequest));
  }

  @RequestMapping(value = "logistics/facility/{facilityId}/requisition-header", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
  public RequisitionHeader getRequisitionHeader(@PathVariable(value = "facilityId") Integer facilityId) {
    return facilityService.getRequisitionHeader(facilityId);
  }

  @RequestMapping(value = "admin/facility/reference-data", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','MANAGE_FACILITY')")
  public Map getReferenceData() {
    FacilityReferenceData facilityReferenceData = new FacilityReferenceData();
    return facilityReferenceData.addFacilityTypes(facilityService.getAllTypes()).
        addFacilityOperators(facilityService.getAllOperators()).
        addGeographicZones(facilityService.getAllZones()).
        addPrograms(programService.getAll()).get();
  }

  @RequestMapping(value = "admin/facility", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','MANAGE_FACILITY')")
  public ResponseEntity<OpenLmisResponse> addOrUpdate(@RequestBody Facility facility, HttpServletRequest request) {
    String modifiedBy = (String) request.getSession().getAttribute(USER);
    facility.setModifiedBy(modifiedBy);
    boolean createFlag = facility.getId() == null;
    try {
      facilityService.save(facility);
    } catch (DataException exception) {
      final ResponseEntity<OpenLmisResponse> errorResponse = error(exception, HttpStatus.BAD_REQUEST);
      errorResponse.getBody().setData("facility", facility);
      return errorResponse;
    }
    ResponseEntity<OpenLmisResponse> successResponse;
    if (createFlag) {
      successResponse = OpenLmisResponse.success(facility.getName() + " created successfully");
    } else {
      successResponse = OpenLmisResponse.success(facility.getName() + " updated successfully");
    }
    successResponse.getBody().setData("facility", facility);
    return successResponse;
  }

  @RequestMapping(value = "admin/facility/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','MANAGE_FACILITY')")
  public ResponseEntity<ModelMap> getFacility(@PathVariable(value = "id") Integer id) {
    ModelMap modelMap = new ModelMap();
    modelMap.put("facility", facilityService.getFacility(id));
    return new ResponseEntity<>(modelMap, HttpStatus.OK);
  }

  @RequestMapping(value = "admin/facility/update/{operation}", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','MANAGE_FACILITY')")
  public ResponseEntity<OpenLmisResponse> updateDataReportableAndActive(@RequestBody Facility facility, @PathVariable(value = "operation") String operation,
                                                                        HttpServletRequest request) {
    String modifiedBy = (String) request.getSession().getAttribute(USER);
    facility.setModifiedBy(modifiedBy);
    String message;
    if ("delete".equalsIgnoreCase(operation)) {
      facility.setDataReportable(false);
      facility.setActive(false);
      message = "deleted";
    } else {
      facility.setDataReportable(true);
      message = "restored";
    }
    try {
      facilityService.updateDataReportableAndActiveFor(facility);
      facility = facilityService.getFacility(facility.getId());
    } catch (DataException exception) {
      ResponseEntity<OpenLmisResponse> errorResponse = error(exception, HttpStatus.BAD_REQUEST);
      errorResponse.getBody().setData("facility", facility);
      return errorResponse;
    }
    final ResponseEntity<OpenLmisResponse> successResponse = OpenLmisResponse.success("\"" + facility.getName() + "\" / \"" + facility.getCode() + "\" " + message + " successfully");
    successResponse.getBody().setData("facility", facility);
    return successResponse;
  }

  @RequestMapping(value = "/create/requisition/supervised/{programId}/facilities.json", method = RequestMethod.GET, headers = "Accept=application/json")
  public ResponseEntity<ModelMap> getUserSupervisedFacilitiesSupportingProgram(@PathVariable(value = "programId") Integer programId, HttpServletRequest request) {
    ModelMap modelMap = new ModelMap();
    Integer userId = (Integer) request.getSession().getAttribute(USER_ID);
    List<Facility> facilities = facilityService.getUserSupervisedFacilities(userId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION);
    modelMap.put("facilities", facilities);
    return new ResponseEntity<>(modelMap, HttpStatus.OK);
  }

}
