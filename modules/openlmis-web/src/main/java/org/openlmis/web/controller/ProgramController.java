/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.openlmis.core.domain.RightName.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * This controller handles endpoint related to listing products for a different criterias, like products related to a facility,
 * program for which requisition can be created, pull based programs, push based programs, details of a program, all programs.
 */

@Controller
@NoArgsConstructor
public class ProgramController extends BaseController {

  public static final String PROGRAM = "program";
  public static final String PROGRAMS = "programs";

  @Autowired
  private ProgramService programService;

  @RequestMapping(value = "/facilities/{facilityId}/programs", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION, MANAGE_USER')")
  public List<Program> getProgramsForFacility(@PathVariable(value = "facilityId") Long facilityId) {
    return programService.getByFacility(facilityId);
  }

  @RequestMapping(value = "/facility/{facilityId}/view/requisition/programs", method = GET, headers = ACCEPT_JSON)
  public List<Program> getProgramsToViewRequisitions(@PathVariable(value = "facilityId") Long facilityId,
                                                     HttpServletRequest request) {
    List<Program> programs = programService.getProgramsForUserByFacilityAndRights(facilityId, loggedInUserId(request), VIEW_REQUISITION);
    List<Program> pullPrograms = new ArrayList<>();
    for (Program program : programs) {
      if (!program.getPush())
        pullPrograms.add(program);
    }
    return pullPrograms;
  }

  @RequestMapping(value = "/create/requisition/programs", method = GET, headers = ACCEPT_JSON)
  public List<Program> getProgramsForCreateOrAuthorizeRequisition(@RequestParam(value = "facilityId", required = false) Long facilityId,
                                                                  HttpServletRequest request) {
    String[] rights = {CREATE_REQUISITION, AUTHORIZE_REQUISITION};
    if (facilityId == null) {
      return programService.getProgramForSupervisedFacilities(loggedInUserId(request), rights);
    } else {
      return programService.getProgramsSupportedByUserHomeFacilityWithRights(facilityId, loggedInUserId(request), rights);
    }
  }

  @RequestMapping(value = "/programs/pull", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_USER, CONFIGURE_RNR, MANAGE_SUPPLY_LINE, MANAGE_FACILITY_APPROVED_PRODUCT, MANAGE_REQUISITION_GROUP')")
  public ResponseEntity<OpenLmisResponse> getAllPullPrograms() {
    return OpenLmisResponse.response(PROGRAMS, programService.getAllPullPrograms());
  }

  @RequestMapping(value = "/programs/push", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
  public ResponseEntity<OpenLmisResponse> getAllPushPrograms() {
    return OpenLmisResponse.response(PROGRAMS, programService.getAllPushPrograms());
  }

  @RequestMapping(value = "/programs/{id}", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CONFIGURE_RNR, MANAGE_REGIMEN_TEMPLATE')")
  public ResponseEntity<OpenLmisResponse> get(@PathVariable Long id) {
    return OpenLmisResponse.response(PROGRAM, programService.getById(id));
  }

  @RequestMapping(value = "/programs", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'MANAGE_REGIMEN_TEMPLATE, MANAGE_USER, MANAGE_PRODUCT')")
  public ResponseEntity<OpenLmisResponse> getAllPrograms() {
    return OpenLmisResponse.response(PROGRAMS, programService.getAll());
  }

  @RequestMapping(value = "/programs/save", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'MANAGE_REGIMEN_TEMPLATE, MANAGE_USER, MANAGE_PRODUCT')")
  public ResponseEntity<OpenLmisResponse> saveUpdates(@RequestBody Program program) {
    return OpenLmisResponse.response(PROGRAMS, programService.update(program));
  }

  @RequestMapping(value = "/facilities/{facilityId}/programsList", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT_ALLOWED_FOR_FACILITY')")
  public ResponseEntity<OpenLmisResponse> getProgramsForFacilityCompleteList(@PathVariable(value = "facilityId") Long facilityId) {
      return OpenLmisResponse.response(PROGRAMS,programService.getByFacility(facilityId));
  }

  @RequestMapping(value = "/facility/{facilityId}/view/vaccine-order-requisition/programs", method = GET, headers = ACCEPT_JSON)
  public List<Program> getProgramsToViewVaccineOrderRequisitions(@PathVariable(value = "facilityId") Long facilityId,
                                                     HttpServletRequest request) {
    List<Program> programs = programService.getProgramsForUserByFacilityAndRights(facilityId, loggedInUserId(request), VIEW_VACCINE_ORDER_REQUISITION);
    List<Program> pullPrograms = new ArrayList<>();
    for (Program program : programs) {
      if (!program.getPush())
        pullPrograms.add(program);
    }
    return pullPrograms;
  }

}
