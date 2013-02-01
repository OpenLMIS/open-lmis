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
import static org.openlmis.core.service.UserService.USER_REQUEST_URL;
import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;


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
      String requestUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/user/resetPassword/";
      Map<String, Object> args = new HashMap<>();
      args.put(USER_REQUEST_URL, requestUrl);
      userService.sendForgotPasswordEmail(user, args);
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
      String requestUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/";
      Map<String, Object> args = new HashMap<>();
      args.put(USER_REQUEST_URL, requestUrl);
      userService.save(user, args);
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
      userService.save(user, null);
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
    successResponse = success("User " + user.getFirstName() + " " + user.getLastName() + " has been successfully updated");
    successResponse.getBody().setData("user", user);
    return successResponse;
  }

  @RequestMapping(value = "/admin/search-user", method = GET)
  @PreAuthorize("hasPermission('','MANAGE_USERS')")
  public List<User> searchUser(@RequestParam String userSearchParam) {
    return userService.searchUser(userSearchParam);
  }

  @RequestMapping(value = "/admin/user/{id}", method = GET)
  @PreAuthorize("hasPermission('','MANAGE_USERS')")
  public User getById(@PathVariable(value = "id") Integer id) {
    return userService.getById(id);
  }

  @RequestMapping(value = "/user/resetPassword/" + "{token}", method = GET)
  public void resetPassword(@PathVariable(value = "token") String token, HttpServletRequest request, HttpServletResponse servletResponse) throws IOException, ServletException {
    try {
      userService.getUserIdForPasswordResetToken(token);
    } catch (DataException e) {
      request.getRequestDispatcher("/public/pages/access-denied.html").forward(request, servletResponse);
    }
    request.getRequestDispatcher("/public/pages/admin/user/reset-password.html").forward(request, servletResponse);
  }

  @RequestMapping(value = "/user/updatePassword", method = PUT)
  public void updateUserPassword(@RequestBody User user) {
    userService.updateUserPassword(user);
  }

}
