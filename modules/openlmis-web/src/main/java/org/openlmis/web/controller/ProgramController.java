/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.service.ProgramService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.openlmis.core.domain.Right.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@NoArgsConstructor
public class ProgramController extends BaseController {

  public static final String PROGRAM = "program";
  private ProgramService programService;
  public static final String PROGRAMS = "programs";


  @Autowired
  public ProgramController(ProgramService programService) {
    this.programService = programService;
  }

  @RequestMapping(value = "/facilities/{facilityId}/programs", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION, MANAGE_USERS')")
  public List<Program> getProgramsForFacility(@PathVariable(value = "facilityId") Long facilityId) {
    return programService.getByFacility(facilityId);
  }

  @RequestMapping(value = "/facility/{facilityId}/view/requisition/programs", method = GET, headers = ACCEPT_JSON)
  public List<Program> getProgramsToViewRequisitions(@PathVariable(value = "facilityId") Long facilityId,
                                                     HttpServletRequest request) {
    List<Program> programs =  programService.getProgramsForUserByFacilityAndRights(facilityId, loggedInUserId(request), VIEW_REQUISITION);
    List<Program> pullPrograms = new ArrayList<>();
    for(Program program : programs) {
      if(!program.isPush())
        pullPrograms.add(program);
    }
    return pullPrograms;
  }

  @RequestMapping(value = "/create/requisition/programs")
  public List<Program> getProgramsForCreateOrAuthorizeRequisition(@RequestParam(value = "facilityId", required = false) Long facilityId,
                                                                  HttpServletRequest request) {
    Right[] rights = {CREATE_REQUISITION, AUTHORIZE_REQUISITION};
    if (facilityId == null) {
      return programService.getProgramForSupervisedFacilities(loggedInUserId(request), rights);
    } else {
      return programService.getProgramsSupportedByUserHomeFacilityWithRights(facilityId, loggedInUserId(request), rights);
    }
  }

  @RequestMapping(value = "/pull/programs", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_USERS, CONFIGURE_RNR')")
  public ResponseEntity<OpenLmisResponse> getAllPullPrograms() {
    return OpenLmisResponse.response(PROGRAMS, programService.getAllPullPrograms());
  }

  @RequestMapping(value = "/push/programs", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
  public ResponseEntity<OpenLmisResponse> getAllPushPrograms() {
    return OpenLmisResponse.response(PROGRAMS, programService.getAllPushPrograms());
  }

  @RequestMapping(value = "programs/{id}", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CONFIGURE_RNR')")
  public ResponseEntity<OpenLmisResponse> get(@PathVariable Long id) {
    return OpenLmisResponse.response(PROGRAM, programService.getById(id));
  }

  @RequestMapping(value = "/programs", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'MANAGE_REGIMEN_TEMPLATE')")
  public ResponseEntity<OpenLmisResponse> getAllPrograms() {
    return OpenLmisResponse.response(PROGRAMS, programService.getAll());
  }

}
