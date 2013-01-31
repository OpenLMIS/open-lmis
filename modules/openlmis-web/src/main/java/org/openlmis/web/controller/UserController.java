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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.core.service.UserService.USER_REQUEST_URL;
import static org.openlmis.web.response.OpenLmisResponse.error;


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
  public ResponseEntity<OpenLmisResponse> sendPasswordTokenEmail(@RequestBody User user, HttpServletRequest request) {
    try {
      String requestUrl = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/";
      Map<String,Object> args = new HashMap<>();
      args.put(USER_REQUEST_URL, requestUrl);
      userService.sendForgotPasswordEmail(user, args);
      return OpenLmisResponse.success("Email sent");
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/admin/users", method = RequestMethod.POST, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','MANAGE_USERS')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody User user, HttpServletRequest request) {
    ResponseEntity<OpenLmisResponse> successResponse;
    String modifiedBy = (String) request.getSession().getAttribute(USER);
    user.setModifiedBy(modifiedBy);
    boolean createFlag = user.getId() == null;
    try {
      if(createFlag) {
        user.setPassword("openLmis123");
      }
      userService.save(user);
    } catch (DataException e) {
      ResponseEntity<OpenLmisResponse> errorResponse = error(e, HttpStatus.BAD_REQUEST);
      errorResponse.getBody().setData("user", user);
      return errorResponse;
    }
    if(createFlag) {
      successResponse = OpenLmisResponse.success("User " + user.getFirstName() + " " + user.getLastName() + " has been successfully created, password link sent on registered Email address");
    }
    else {
      successResponse = OpenLmisResponse.success("User " + user.getFirstName() + " " + user.getLastName() + " has been successfully updated");
    }
    successResponse.getBody().setData("user", user);
    return successResponse;
  }

  @RequestMapping(value = "/admin/search-user", method = RequestMethod.GET)
  @PreAuthorize("hasPermission('','MANAGE_USERS')")
  public List<User> searchUser(@RequestParam String userSearchParam) {
    return userService.searchUser(userSearchParam);
  }

  @RequestMapping(value = "/admin/user/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasPermission('','MANAGE_USERS')")
  public User getById(@PathVariable(value = "id") Integer id) {
    return userService.getById(id);
  }
}
