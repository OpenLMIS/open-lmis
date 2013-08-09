/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.core.service.UserService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.io.IOException;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.web.response.OpenLmisResponse.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.*;


@Controller
@NoArgsConstructor
public class UserController extends BaseController {

  static final String MSG_USER_DISABLE_SUCCESS = "msg.user.disable.success";
  static final String USER_CREATED_SUCCESS_MSG = "message.user.created.success.email.sent";

  private RoleRightsService roleRightService;
  private UserService userService;

  public static final String USER_ID = "userId";
  public static final String TOKEN_VALID = "TOKEN_VALID";
  private static final String RESET_PASSWORD_PATH = "public/pages/reset-password.html#/token/";


  private String baseUrl;

  @Autowired
  public UserController(RoleRightsService roleRightService, UserService userService, @Value("${mail.base.url}") String baseUrl) {
    this.roleRightService = roleRightService;
    this.userService = userService;
    this.baseUrl = baseUrl;
  }

  @RequestMapping(value = "/user-context", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> user(HttpServletRequest httpServletRequest) {
    String userName = (String) httpServletRequest.getSession().getAttribute(USER);
    if (userName != null) {
      OpenLmisResponse openLmisResponse = new OpenLmisResponse("name", userName);
      openLmisResponse.addData("authenticated", TRUE);
      openLmisResponse.addData("rights", roleRightService.getRights(userName));
      return openLmisResponse.response(HttpStatus.OK);
    } else {
      return authenticationError();
    }
  }

  @RequestMapping(value = "/authentication-error", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> authenticationError() {
    return error(messageService.message("user.login.error"), HttpStatus.UNAUTHORIZED);
  }

  @RequestMapping(value = "/forgot-password", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> sendPasswordTokenEmail(@RequestBody User user) {
    try {
      String resetPasswordLink = baseUrl + RESET_PASSWORD_PATH;
      userService.sendForgotPasswordEmail(user, resetPasswordLink);
      return success(messageService.message("email.sent"));
    } catch (DataException e) {
      return error(e, BAD_REQUEST);
    }
  }

  @RequestMapping(value = "/users", method = POST, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_USER')")
  public ResponseEntity<OpenLmisResponse> create(@RequestBody User user, HttpServletRequest request) {
    user.setCreatedBy(loggedInUserId(request));
    user.setModifiedBy(loggedInUserId(request));
    try {
      String resetPasswordBaseLink = baseUrl + RESET_PASSWORD_PATH;
      userService.create(user, resetPasswordBaseLink);
    } catch (DataException e) {
      return error(e, BAD_REQUEST);
    }
    ResponseEntity<OpenLmisResponse> success = success(USER_CREATED_SUCCESS_MSG);
    success.getBody().addData("user", user);
    return success;
  }

  @RequestMapping(value = "/users/{id}", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_USER')")
  public ResponseEntity<OpenLmisResponse> update(@RequestBody User user,
                                                 @PathVariable("id") Long id,
                                                 HttpServletRequest request) {
    user.setModifiedBy(loggedInUserId(request));
    user.setId(id);
    try {
      userService.update(user);
    } catch (DataException e) {
      return error(e, BAD_REQUEST);
    }
    return new OpenLmisResponse().response(HttpStatus.OK);
  }

  @RequestMapping(value = "/users", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_USER')")
  public List<User> searchUser(@RequestParam(required = true) String param) {
    return userService.searchUser(param);
  }

  @RequestMapping(value = "/users/{id}", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_USER')")
  public User get(@PathVariable(value = "id") Long id) {
    return userService.getById(id);
  }

  @RequestMapping(value = "/users/{id}", method = DELETE, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> disable(@PathVariable("id") Long id,
                                                  HttpServletRequest request) {
    userService.disable(id, loggedInUserId(request));
    return success(MSG_USER_DISABLE_SUCCESS);
  }

  @RequestMapping(value = "/user/validatePasswordResetToken/{token}", method = GET)
  public ResponseEntity<OpenLmisResponse> validatePasswordResetToken(@PathVariable(value = "token") String token) throws IOException, ServletException {
    try {
      userService.getUserIdByPasswordResetToken(token);
    } catch (DataException e) {
      return error(e, BAD_REQUEST);
    }
    return response(TOKEN_VALID, true);
  }

  @RequestMapping(value = "/user/resetPassword/{token}", method = PUT)
  public ResponseEntity<OpenLmisResponse> resetPassword(@PathVariable(value = "token") String token, @RequestBody String password) {
    try {
      userService.updateUserPassword(token, password);
    } catch (DataException e) {
      return error(e, BAD_REQUEST);
    }
    return success(messageService.message("password.reset"));
  }

  @RequestMapping(value = "/admin/resetPassword/{userId}", method = PUT)
  public ResponseEntity<OpenLmisResponse> updateUserPassword(@PathVariable(value = "userId") Long userId, @RequestBody String password) {
    try {
      userService.updateUserPassword(userId, password);
    } catch (DataException e) {
      return error(e, BAD_REQUEST);
    }
    return success(messageService.message("password.reset.success"));
  }
}
