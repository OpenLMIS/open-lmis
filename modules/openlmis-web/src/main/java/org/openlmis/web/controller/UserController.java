package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.core.service.UserService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;


@Controller
@NoArgsConstructor
public class UserController extends BaseController {

  private RoleRightsService roleRightService;
  private UserService userService;

  @Autowired
  public UserController(RoleRightsService roleRightService, UserService userService) {
    this.roleRightService = roleRightService;
    this.userService = userService;
  }

  @RequestMapping(value = "/user", method = RequestMethod.GET)
  public HashMap<String, Object> user(HttpServletRequest httpServletRequest, @RequestParam(required = false) String error) {
    String userName = (String) httpServletRequest.getSession().getAttribute(USER);
    HashMap<String, Object> params = new HashMap<>();
    if (userName != null) {
      params.put("name", userName);
      params.put("authenticated", TRUE);
      params.put("rights", roleRightService.getRights(userName));
    } else {
      params.put("authenticated", FALSE);
      params.put("error", error);
    }
    return params;
  }

  @RequestMapping(value = "/forgot-password", method = RequestMethod.POST, headers = "Accept=application/json")
  public ResponseEntity<OpenLmisResponse> sendPasswordTokenEmail(@RequestBody User user) {
    try {
      userService.sendForgotPasswordEmail(user);
      return OpenLmisResponse.success("Email sent");
    } catch (DataException e) {
      return OpenLmisResponse.error(e.getOpenLmisMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/admin/users", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','MANAGE_USERS')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody User user) {
    try {
      user.setPassword("openLmis123");

      userService.save(user);

      ResponseEntity<OpenLmisResponse> successResponse = OpenLmisResponse.success("User saved successfully");
      successResponse.getBody().setData("user", user);
      return successResponse;
    } catch (DataException e) {
      ResponseEntity<OpenLmisResponse> errorResponse = OpenLmisResponse.error(e.getOpenLmisMessage(), HttpStatus.BAD_REQUEST);
      errorResponse.getBody().setData("user", user);
      return errorResponse;
    }
  }

  @RequestMapping(value = "/admin/search-user",method=RequestMethod.GET)
  @PreAuthorize("hasPermission('',MANAGE_USERS)")
  public List<User> searchUser(@RequestParam String userSearchParam) {
    return userService.searchUser(userSearchParam);
  }
}
