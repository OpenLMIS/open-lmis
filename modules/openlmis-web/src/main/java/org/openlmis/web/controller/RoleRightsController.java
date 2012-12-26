package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.Role;
import org.openlmis.core.service.RoleRightsService;
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
  public List<Right> getAllRights() {
    return roleRightsService.getAllRights();
  }

  @RequestMapping(value = "/role", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','MANAGE_ROLE')")
  public ResponseEntity<ModelMap> saveRole(@RequestBody Role role, HttpServletRequest request) {
    role.setModifiedBy(loggedInUserId(request));
    ModelMap modelMap = new ModelMap();
    try {
      roleRightsService.saveRole(role);
      modelMap.put("success", "'" + role.getName() + "' created successfully");
      return new ResponseEntity<>(modelMap, HttpStatus.OK);
    } catch (RuntimeException e) {
      modelMap.put("error", e.getMessage());
      return new ResponseEntity<>(modelMap, HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/roles", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','MANAGE_ROLE')")
  public OpenLmisResponse getAll() {
    List<Role> allRoles = roleRightsService.getAllRoles();
    return new OpenLmisResponse(allRoles, null, null);
  }


  @RequestMapping(value = "/role/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','MANAGE_ROLE')")
  public OpenLmisResponse get(@PathVariable("id") Integer id) {
    Role role = roleRightsService.getRole(id);
    return new OpenLmisResponse(role);
  }
}
