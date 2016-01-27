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
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Role;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.RightService;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.core.web.OpenLmisResponse.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * This controller handles endpoint related to get, create, update roles and rights.
 */
@Controller
@NoArgsConstructor
public class RoleRightsController extends BaseController {

  @Autowired
  private RoleRightsService roleRightsService;

  @Autowired
  private RightService rightService;

  public static final String ROLE = "role";
  public static final String ROLES_MAP = "roles_map";
  public static final String RIGHTS = "rights";
  public static final String RIGHT_TYPE = "right_type";

  @RequestMapping(value = "/rights", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_ROLE')")
  public ResponseEntity<OpenLmisResponse> getAllRights() {
    return OpenLmisResponse.response(RIGHTS, rightService.getAll());
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
    OpenLmisResponse response = new OpenLmisResponse(ROLES_MAP, roleRightsService.getAllRolesMap());
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @RequestMapping(value = "/roles/list", method = GET)
  public ResponseEntity<OpenLmisResponse> getAllReadonly() {
    OpenLmisResponse response = new OpenLmisResponse(ROLES_MAP, roleRightsService.getAllRolesMap());
    return new ResponseEntity<>(response, HttpStatus.OK);
  }


  @RequestMapping(value = "/roles-flat", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_ROLE, MANAGE_USER')")
  public ResponseEntity<OpenLmisResponse> getAllRolesFlat() {
    OpenLmisResponse response = new OpenLmisResponse(ROLES_MAP, roleRightsService.getAllRoles());
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @RequestMapping(value = "/roles/{id}", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_ROLE')")
  public ResponseEntity<OpenLmisResponse> get(@PathVariable("id") Long id) {
    ResponseEntity<OpenLmisResponse> response = response(ROLE, roleRightsService.getRole(id));
    response.getBody().addData(RIGHT_TYPE, roleRightsService.getRightTypeForRoleId(id));
    return response;
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


    @RequestMapping(value="/roles/getList",method= RequestMethod.GET, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_ROLE')")
    public ResponseEntity<OpenLmisResponse> getRoleList(HttpServletRequest request){
        return OpenLmisResponse.response("roles", roleRightsService.getAllRoles());
    }

}
