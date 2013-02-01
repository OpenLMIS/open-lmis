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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.*;


@Controller
@NoArgsConstructor
public class UserController extends BaseController {

  private RoleRightsService roleRightService;
  private UserService userService;
  public static final String USER_ID = "userId";

  @Autowired
  public UserController(RoleRightsService roleRightService, UserService userService) {
    this.roleRightService = roleRightService;
    this.userService = userService;
  }

  @RequestMapping(value = "/user", method = GET)
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

  @RequestMapping(value = "/forgot-password", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> sendPasswordTokenEmail(@RequestBody User user, HttpServletRequest request) {
    try {
      String resetPasswordLink = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/public/pages/reset-password.html#/token/";
      userService.sendForgotPasswordEmail(user, resetPasswordLink);
      return success("Email sent");
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/users", method = POST, headers = "Accept=application/json")
  @PreAuthorize("hasPermission('','MANAGE_USERS')")
  public ResponseEntity<OpenLmisResponse> create(@RequestBody User user, HttpServletRequest request) {
    ResponseEntity<OpenLmisResponse> successResponse;
    String modifiedBy = (String) request.getSession().getAttribute(USER);
    user.setModifiedBy(modifiedBy);
    try {
      user.setPassword("openLmis123");
      String resetPasswordBaseLink = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/";
      userService.create(user, resetPasswordBaseLink);
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
    successResponse = success("User " + user.getFirstName() + " " + user.getLastName() + " has been successfully created, password link sent on registered Email address");
    successResponse.getBody().setData("user", user);
    return successResponse;
  }

  @RequestMapping(value = "/users/{id}", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("hasPermission('','MANAGE_USERS')")
  public ResponseEntity<OpenLmisResponse> update(@RequestBody User user,
                                                 @PathVariable("id") Integer id,
                                                 HttpServletRequest request) {
    ResponseEntity<OpenLmisResponse> successResponse;
    user.setModifiedBy(loggedInUser(request));
    user.setId(id);
    try {
      userService.update(user);
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
    successResponse = success("User " + user.getFirstName() + " " + user.getLastName() + " has been successfully updated");
    successResponse.getBody().setData("user", user);
    return successResponse;
  }

  @RequestMapping(value = "/users", method = GET)
  @PreAuthorize("hasPermission('','MANAGE_USERS')")
  public List<User> searchUser(@RequestParam(required = true) String param) {
    return userService.searchUser(param);
  }

  @RequestMapping(value = "/admin/user/{id}", method = GET)
  @PreAuthorize("hasPermission('','MANAGE_USERS')")
  public User getById(@PathVariable(value = "id") Integer id) {
    return userService.getById(id);
  }

  @RequestMapping(value = "/user/validatePasswordResetToken/{token}", method = GET)
  public ResponseEntity<OpenLmisResponse> validatePasswordResetToken(@PathVariable(value = "token") String token) throws IOException, ServletException {
    Integer userId = null;
    try {
      userService.getUserIdForPasswordResetToken(token);
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
    return response(USER_ID, userId);
  }

  @RequestMapping(value = "/user/resetPassword" , method = PUT)
  public void resetPassword(@RequestBody User user) {
    userService.updateUserPassword(user);
  }

}
