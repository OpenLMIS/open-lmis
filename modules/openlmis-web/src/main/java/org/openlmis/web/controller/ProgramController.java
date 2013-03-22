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
import java.util.List;
import java.util.Set;

import static org.openlmis.core.domain.Right.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@NoArgsConstructor
public class ProgramController extends BaseController {

  private ProgramService programService;
  public static final String PROGRAMS = "programs";


  @Autowired
  public ProgramController(ProgramService programService) {
    this.programService = programService;
  }

    @RequestMapping(value = "/active/programs", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CONFIGURE_RNR')")
  public List<Program> getAllActivePrograms() {
    return programService.getAllActive();
  }

  @RequestMapping(value = "/facilities/{facilityId}/programs", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION, MANAGE_USERS')")
  public List<Program> getProgramsForFacility(@PathVariable(value = "facilityId") Integer facilityId) {
    return programService.getByFacility(facilityId);
  }

  @RequestMapping(value = "/facility/{facilityId}/user/programs", method = GET, headers = ACCEPT_JSON)
  public List<Program> getProgramsSupportedByFacilityForUserWithRights(@PathVariable(value = "facilityId") Integer facilityId, @RequestParam("rights") Set<Right> rights, HttpServletRequest request) {
    return programService.getProgramsSupportedByFacilityForUserWithRights(facilityId, loggedInUserId(request), rights.toArray(new Right[rights.size()]));
  }


  @RequestMapping(value = "/create/requisition/supervised/programs", method = GET, headers = ACCEPT_JSON)
  public List<Program> getUserSupervisedActiveProgramsForCreateAndAuthorizeRequisition(HttpServletRequest request) {
    return programService.getUserSupervisedActiveProgramsWithRights(loggedInUserId(request), CREATE_REQUISITION, AUTHORIZE_REQUISITION);
  }

  @RequestMapping(value = "/programs", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_USERS')")
  public ResponseEntity<OpenLmisResponse> getAllPrograms() {
    return OpenLmisResponse.response(PROGRAMS, programService.getAll());
  }

}
