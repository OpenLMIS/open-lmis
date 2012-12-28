package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Role;
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
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.web.response.OpenLmisResponse.*;

@Controller
@NoArgsConstructor
public class RoleRightsController extends BaseController {


  private RoleRightsService roleRightsService;

  @Autowired
  public RoleRightsController(RoleRightsService roleRightsService) {
    this.roleRightsService = roleRightsService;
  }

  @RequestMapping(value = "/rights", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','MANAGE_ROLE')")
  public ResponseEntity<OpenLmisResponse> getAllRights() {
    return OpenLmisResponse.response(RIGHTS, roleRightsService.getAllRights());
  }

  @RequestMapping(value = "/roles", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','MANAGE_ROLE')")
  public ResponseEntity<OpenLmisResponse> createRole(@RequestBody Role role, HttpServletRequest request) {
    role.setModifiedBy(loggedInUserId(request));
    try {
      roleRightsService.saveRole(role);
      return success("'" + role.getName() + "' created successfully");
    } catch (RuntimeException e) {
      return error(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/roles", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','MANAGE_ROLE')")
  public ResponseEntity<OpenLmisResponse> getAll() {
    OpenLmisResponse response = new OpenLmisResponse(ROLES, roleRightsService.getAllRoles());
    return new ResponseEntity<>(response, HttpStatus.OK);
  }


  @RequestMapping(value = "/roles/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','MANAGE_ROLE')")
  public ResponseEntity<OpenLmisResponse> get(@PathVariable("id") Integer id) {
    Role role = roleRightsService.getRole(id);
    return response(ROLE, role);
  }

  @RequestMapping(value = "/roles/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','MANAGE_ROLE')")
  public ResponseEntity<OpenLmisResponse> updateRole(@PathVariable("id") Integer id, @RequestBody Role role) {
    role.setId(id);
    roleRightsService.updateRole(role);
    return new ResponseEntity<>(new OpenLmisResponse(SUCCESS, "Role updated successfully"), HttpStatus.OK);
  }
}
