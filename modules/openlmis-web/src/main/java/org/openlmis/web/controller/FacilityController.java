/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
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
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.openlmis.core.domain.Facility.createFacilityToBeDeleted;
import static org.openlmis.core.domain.Facility.createFacilityToBeRestored;
import static org.openlmis.core.domain.Right.*;
import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@NoArgsConstructor
public class FacilityController extends BaseController {

  @Autowired
  private FacilityService facilityService;
  @Autowired
  private ProgramService programService;

  @RequestMapping(value = "/facilities", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public List<Facility> get(@RequestParam(value = "searchParam", required = false) String searchParam) {
    if (searchParam != null) {
      return facilityService.searchFacilitiesByCodeOrName(searchParam);
    } else {
      return facilityService.getAll();
    }
  }

  @RequestMapping(value = "logistics/user/facilities", method = GET)
  public List<Facility> getHomeFacility(HttpServletRequest httpServletRequest) {
    return Arrays.asList(facilityService.getHomeFacility(loggedInUserId(httpServletRequest)));
  }

  @RequestMapping(value = "/facilities/reference-data", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public Map getReferenceData() {
    FacilityReferenceData facilityReferenceData = new FacilityReferenceData();
    return facilityReferenceData.addFacilityTypes(facilityService.getAllTypes()).
      addFacilityOperators(facilityService.getAllOperators()).
      addGeographicZones(facilityService.getAllZones()).
      addPrograms(programService.getAll()).get();
  }

  @RequestMapping(value = "/facilities/{id}", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public ResponseEntity<ModelMap> getFacility(@PathVariable(value = "id") Long id) {
    ModelMap modelMap = new ModelMap();
    modelMap.put("facility", facilityService.getById(id));
    return new ResponseEntity<>(modelMap, HttpStatus.OK);
  }


  @RequestMapping(value = "/create/requisition/supervised/{programId}/facilities.json", method = GET)
  public ResponseEntity<ModelMap> getUserSupervisedFacilitiesSupportingProgram(@PathVariable(
    value = "programId") Long programId, HttpServletRequest request) {
    ModelMap modelMap = new ModelMap();
    Long userId = loggedInUserId(request);
    List<Facility> facilities = facilityService.getUserSupervisedFacilities(userId, programId, CREATE_REQUISITION,
      AUTHORIZE_REQUISITION);
    modelMap.put("facilities", facilities);
    return new ResponseEntity<>(modelMap, HttpStatus.OK);
  }

  @RequestMapping(value = "/facilities", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public ResponseEntity insert(@RequestBody Facility facility, HttpServletRequest request) {
    facility.setModifiedBy(loggedInUserId(request));
    ResponseEntity<OpenLmisResponse> response;
    try {
      facilityService.insert(facility);
    } catch (DataException exception) {
      return createErrorResponse(facility, exception);
    }
    response = success(new OpenLmisMessage("message.facility.created.success", facility.getName()));
    response.getBody().addData("facility", facility);
    return response;
  }

  @RequestMapping(value = "/facilities/{id}", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public ResponseEntity update(@RequestBody Facility facility, HttpServletRequest request) {
    facility.setModifiedBy(loggedInUserId(request));
    ResponseEntity<OpenLmisResponse> response;
    try {
      facilityService.update(facility);
    } catch (DataException exception) {
      return createErrorResponse(facility, exception);
    }
    response = success(new OpenLmisMessage("message.facility.updated.success", facility.getName()));
    response.getBody().addData("facility", facility);
    return response;
  }

  @RequestMapping(value = "/user/facilities/view", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> listForViewing(HttpServletRequest request) {
    return OpenLmisResponse.response("facilities",
      facilityService.getForUserAndRights(loggedInUserId(request), VIEW_REQUISITION));
  }

  @RequestMapping(value = "/facilities/{facilityId}", method = DELETE, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public ResponseEntity softDelete(HttpServletRequest httpServletRequest, @PathVariable Long facilityId) {
    ResponseEntity<OpenLmisResponse> response;
    Facility facilityToBeDeleted = createFacilityToBeDeleted(facilityId, loggedInUserId(httpServletRequest));
    Facility deletedFacility = facilityService.updateDataReportableAndActiveFor(facilityToBeDeleted);

    response = success(new OpenLmisMessage("delete.facility.success", deletedFacility.getName(),
      deletedFacility.getCode()));
    response.getBody().addData("facility", deletedFacility);
    return response;
  }


  @RequestMapping(value = "/facilities/{id}/restore", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public ResponseEntity<OpenLmisResponse> restore(HttpServletRequest request, @PathVariable("id") long facilityId, @RequestParam boolean active) {
    ResponseEntity<OpenLmisResponse> response;
    Facility facilityToBeDeleted = createFacilityToBeRestored(facilityId, loggedInUserId(request), active);
    Facility restoredFacility = facilityService.updateDataReportableAndActiveFor(facilityToBeDeleted);

    response = success(new OpenLmisMessage("restore.facility.success", restoredFacility.getName(),
      restoredFacility.getCode()));
    response.getBody().addData("facility", restoredFacility);
    return response;
  }

  private ResponseEntity<OpenLmisResponse> createErrorResponse(Facility facility, DataException exception) {
    ResponseEntity<OpenLmisResponse> response;
    response = error(exception, HttpStatus.BAD_REQUEST);
    response.getBody().addData("facility", facility);
    return response;
  }

}
