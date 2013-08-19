/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Role;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.web.response.OpenLmisResponse.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@NoArgsConstructor
public class RoleRightsController extends BaseController {


  private RoleRightsService roleRightsService;

  public static final String ROLE = "role";
  public static final String ROLES = "roles";
  public static final String RIGHTS = "rights";

  @Autowired
  public RoleRightsController(RoleRightsService roleRightsService) {
    this.roleRightsService = roleRightsService;
  }

  @RequestMapping(value = "/rights", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_ROLE')")
  public ResponseEntity<OpenLmisResponse> getAllRights() {
    return OpenLmisResponse.response(RIGHTS, roleRightsService.getAllRights());
  }

  @RequestMapping(value = "/roles", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_ROLE')")
  public ResponseEntity<OpenLmisResponse> createRole(@RequestBody Role role, HttpServletRequest request) {
    role.setCreatedBy(loggedInUserId(request));
    role.setModifiedBy(loggedInUserId(request));
    try {
      roleRightsService.saveRole(role);
      return success(messageService.message("message.role.created.success", role.getName()));
    } catch (DataException e) {
      return error(e, HttpStatus.CONFLICT);
    }
  }

  @RequestMapping(value = "/roles", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_ROLE, MANAGE_USER')")
  public ResponseEntity<OpenLmisResponse> getAll() {
    OpenLmisResponse response = new OpenLmisResponse(ROLES, roleRightsService.getAllRoles());
    return new ResponseEntity<>(response, HttpStatus.OK);
  }


  @RequestMapping(value = "/roles/{id}", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_ROLE')")
  public ResponseEntity<OpenLmisResponse> get(@PathVariable("id") Long id) {
    Role role = roleRightsService.getRole(id);
    return response(ROLE, role);
  }

  @RequestMapping(value = "/roles/{id}", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_ROLE')")
  public ResponseEntity<OpenLmisResponse> updateRole(@PathVariable("id") Long id, @RequestBody Role role, HttpServletRequest request) {
    role.setModifiedBy(loggedInUserId(request));
    try {
      role.setId(id);
      roleRightsService.updateRole(role);
    } catch (DataException e) {
      return error(e, HttpStatus.CONFLICT);
    }
    return success(messageService.message("message.role.updated.success", role.getName()));
  }

  @RequestMapping(value = "facility/{facilityId}/program/{programId}/rights", method = GET)
  public ResponseEntity<OpenLmisResponse> getRightsForUserAndFacilityProgram(@PathVariable("facilityId") Long facilityId, @PathVariable("programId") Long programId, HttpServletRequest httpServletRequest) {
    return response(RIGHTS, roleRightsService.getRightsForUserAndFacilityProgram(loggedInUserId(httpServletRequest), new Facility(facilityId), new Program(programId)));
  }
}
