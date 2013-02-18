package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.openlmis.core.domain.Right.AUTHORIZE_REQUISITION;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;
import static org.openlmis.core.domain.Right.VIEW_REQUISITION;
import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.*;

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

  @RequestMapping(value = "/facilities", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("hasPermission('','MANAGE_FACILITY')")
  public List<Facility> get(@RequestParam(value = "searchParam", required = false) String searchParam) {
    if (searchParam != null) {
      return facilityService.searchFacilitiesByCodeOrName(searchParam);
    } else {
      return facilityService.getAll();
    }
  }

  @RequestMapping(value = "logistics/user/facilities", method = GET)
  public List<Facility> getAllByUser(HttpServletRequest httpServletRequest) {
    return facilityService.getAllForUser(loggedInUserId(httpServletRequest));
  }

  @RequestMapping(value = "/facilities/reference-data", method = GET)
  @PreAuthorize("hasPermission('','MANAGE_FACILITY')")
  public Map getReferenceData() {
    FacilityReferenceData facilityReferenceData = new FacilityReferenceData();
    return facilityReferenceData.addFacilityTypes(facilityService.getAllTypes()).
        addFacilityOperators(facilityService.getAllOperators()).
        addGeographicZones(facilityService.getAllZones()).
        addPrograms(programService.getAll()).get();
  }

  @RequestMapping(value = "/facilities/{id}", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("hasPermission('','MANAGE_FACILITY')")
  public ResponseEntity<ModelMap> getFacility(@PathVariable(value = "id") Integer id) {
    ModelMap modelMap = new ModelMap();
    modelMap.put("facility", facilityService.getById(id));
    return new ResponseEntity<>(modelMap, HttpStatus.OK);
  }

  @RequestMapping(value = "/facility/update/{operation}", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("hasPermission('','MANAGE_FACILITY')")
  public ResponseEntity<OpenLmisResponse> updateDataReportableAndActive(@RequestBody Facility facility, @PathVariable(value = "operation") String operation,
                                                                        HttpServletRequest request) {
    facility.setModifiedBy(loggedInUser(request));
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
      facility = facilityService.getById(facility.getId());
    } catch (DataException exception) {
      ResponseEntity<OpenLmisResponse> errorResponse = error(exception, HttpStatus.BAD_REQUEST);
      errorResponse.getBody().setData("facility", facility);
      return errorResponse;
    }
    final ResponseEntity<OpenLmisResponse> successResponse = success("\"" + facility.getName() + "\" / \"" + facility.getCode() + "\" " + message + " successfully");
    successResponse.getBody().setData("facility", facility);
    return successResponse;
  }

  @RequestMapping(value = "/create/requisition/supervised/{programId}/facilities.json", method = GET)
  public ResponseEntity<ModelMap> getUserSupervisedFacilitiesSupportingProgram(@PathVariable(value = "programId") Integer programId, HttpServletRequest request) {
    ModelMap modelMap = new ModelMap();
    Integer userId = (Integer) request.getSession().getAttribute(USER_ID);
    List<Facility> facilities = facilityService.getUserSupervisedFacilities(userId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION);
    modelMap.put("facilities", facilities);
    return new ResponseEntity<>(modelMap, HttpStatus.OK);
  }

  @RequestMapping(value = "/facilities", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("hasPermission('','MANAGE_FACILITY')")
  public ResponseEntity insert(@RequestBody Facility facility, HttpServletRequest request) {
    facility.setModifiedBy(loggedInUser(request));
    ResponseEntity<OpenLmisResponse> response;
    try {
      facilityService.insert(facility);
    } catch (DataException exception) {
      return createErrorResponse(facility, exception);
    }
    response = success("Facility '" + facility.getName() + "' created successfully");
    response.getBody().setData("facility", facility);
    return response;
  }

  @RequestMapping(value = "/facilities/{id}", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("hasPermission('','MANAGE_FACILITY')")
  public ResponseEntity update(@RequestBody Facility facility, HttpServletRequest request) {
    String modifiedBy = (String) request.getSession().getAttribute(USER);
    facility.setModifiedBy(modifiedBy);
    ResponseEntity<OpenLmisResponse> response;
    try {
      facilityService.update(facility);
    } catch (DataException exception) {
      return createErrorResponse(facility, exception);
    }
    response = success("Facility '" + facility.getName() + "' updated successfully");
    response.getBody().setData("facility", facility);
    return response;
  }

  @RequestMapping(value = "/user/facilities/view", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> listForViewing(HttpServletRequest request) {
    return OpenLmisResponse.response("facilities", facilityService.getForUserAndRights(loggedInUserId(request), VIEW_REQUISITION));
  }

  private ResponseEntity<OpenLmisResponse> createErrorResponse(Facility facility, DataException exception) {
    ResponseEntity<OpenLmisResponse> response;
    response = error(exception, HttpStatus.BAD_REQUEST);
    response.getBody().setData("facility", facility);
    return response;
  }
}
