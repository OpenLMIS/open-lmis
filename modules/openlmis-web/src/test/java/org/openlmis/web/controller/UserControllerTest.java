/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.hash.Encoder;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.core.service.UserService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.openlmis.web.controller.UserController.TOKEN_VALID;

@Category(UnitTests.class)
public class UserControllerTest {

  public static final Long userId = 1L;

  private MockHttpSession session;

  private MockHttpServletRequest httpServletRequest;

  @Mock
  private RoleRightsService roleRightService;

  @Mock
  private UserService userService;

  @Mock
  private MessageService messageService;

  private UserController userController;

  private String baseUrl = "http://localhost:9091/";

  @Before
  public void setUp() {
    initMocks(this);
    httpServletRequest = new MockHttpServletRequest();
    session = new MockHttpSession();
    httpServletRequest.setSession(session);
    userController = new UserController(roleRightService, userService, baseUrl);
    userController.setMessageService(messageService);
  }

  @Test
  public void shouldReturnUserInfoOfLoggedInUser() {
    String username = "Foo";
    session.setAttribute(UserAuthenticationSuccessHandler.USER, username);
    ResponseEntity<OpenLmisResponse> response = userController.user(httpServletRequest);
    assertThat(response.getBody().getData().get("name").toString(), is("Foo"));
    assertThat((Boolean) response.getBody().getData().get("authenticated"), is(true));
  }

  @Test
  public void shouldNotReturnUserInfoWhenNotLoggedIn() {
    when(messageService.message("user.login.error")).thenReturn("The username or password you entered is incorrect. Please try again.");
    session.setAttribute(UserAuthenticationSuccessHandler.USER, null);


    ResponseEntity<OpenLmisResponse> response = userController.user(httpServletRequest);
    assertThat(response.getBody().getErrorMsg(), is("The username or password you entered is incorrect. Please try again."));
  }

  @Test
  public void shouldGetAllPrivilegesForTheLoggedInUser() throws Exception {
    String username = "Foo";
    session.setAttribute(UserAuthenticationSuccessHandler.USER, username);
    Set<Right> rights = new HashSet<>();
    when(roleRightService.getRights(username)).thenReturn(rights);
    ResponseEntity<OpenLmisResponse> response = userController.user(httpServletRequest);
    verify(roleRightService).getRights(username);
    assertThat((Set<Right>) response.getBody().getData().get("rights"), is(rights));
  }

  @Test
  public void shouldEmailPasswordTokenForUser() throws Exception {
    User user = new User();
    user.setUserName("Manan");
    user.setEmail("manan@thoughtworks.com");
    userController.sendPasswordTokenEmail(user);
    verify(userService).sendForgotPasswordEmail(eq(user), eq("http://localhost:9091/public/pages/reset-password.html#/token/"));
  }

  @Test
  public void shouldReturnErrorIfSendingForgotPasswordEmailFails() throws Exception {
    User user = new User();
    HttpServletRequest request = mock(HttpServletRequest.class);
    doThrow(new DataException("some error")).when(userService).sendForgotPasswordEmail(eq(user), anyString());

    ResponseEntity<OpenLmisResponse> response = userController.sendPasswordTokenEmail(user);

    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("some error"));
  }

  @Test
  public void shouldSaveUser() throws Exception {
    User user = new User();
    httpServletRequest.getSession().setAttribute(USER_ID, userId);
    httpServletRequest.getSession().setAttribute(USER, USER);
    when(messageService.message("message.user.created.success.email.sent", null, null)).thenReturn("User 'null null' has been successfully created, password link has been sent on registered Email address");
    ResponseEntity<OpenLmisResponse> response = userController.create(user, httpServletRequest);

    verify(userService).create(eq(user), eq("http://localhost:9091/public/pages/reset-password.html#/token/"));

    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody().getSuccessMsg(), is("User '" + user.getFirstName() + " " + user.getLastName() + "' has been successfully created, password link has been sent on registered Email address"));
  }

  @Test
  public void shouldUpdateUser() throws Exception {
    User user = new User();
    user.setId(1L);
    user.setPassword("password");
    httpServletRequest.getSession().setAttribute(USER_ID, userId);
    httpServletRequest.getSession().setAttribute(USER, USER);
    when(messageService.message("message.user.updated.success", null, null)).thenReturn("User 'null null' has been successfully updated");

    ResponseEntity<OpenLmisResponse> response = userController.update(user, 1L, httpServletRequest);

    verify(userService).update(user);
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody().getSuccessMsg(), is("User '" + user.getFirstName() + " " + user.getLastName() + "' has been successfully updated"));
    assertThat(user.getPassword(), is(Encoder.hash("password")));
    assertThat(user.getModifiedBy(), is(userId));
  }

  @Test
  public void shouldReturnErrorIfSaveUserFails() throws Exception {
    User user = new User();
    doThrow(new DataException("Save user failed")).when(userService).create(eq(user), anyString());
    ResponseEntity<OpenLmisResponse> response = userController.create(user, httpServletRequest);

    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("Save user failed"));
  }

  @Test
  public void shouldReturnUserDetailsIfUserExists() throws Exception {
    String userSearchParam = "Admin";
    List<User> listOfUsers = Arrays.asList(new User());
    User userReturned = new User();

    when(userService.searchUser(userSearchParam)).thenReturn(listOfUsers);

    List<User> userList = userController.searchUser(userSearchParam);

    assertTrue(userList.contains(userReturned));
  }

  @Test
  public void shouldReturnUserIfIdExists() throws Exception {
    User user = new User();
    when(userService.getById(1L)).thenReturn(user);

    User returnedUser = userController.get(1L);

    assertThat(returnedUser, is(user));

  }

  @Test
  public void shouldReturnErrorResponseIfTokenIsNotValid() throws IOException, ServletException {
    String invalidToken = "invalidToken";
    String errorMessage = "some error";
    doThrow(new DataException(errorMessage)).when(userService).getUserIdByPasswordResetToken(invalidToken);
    ResponseEntity<OpenLmisResponse> response = userController.validatePasswordResetToken(invalidToken);

    verify(userService).getUserIdByPasswordResetToken(invalidToken);
    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is(errorMessage));
  }

  @Test
  public void shouldReturnSuccessResponseIfTokenIsValid() throws IOException, ServletException {
    String validToken = "validToken";
    ResponseEntity<OpenLmisResponse> response = userController.validatePasswordResetToken(validToken);

    verify(userService).getUserIdByPasswordResetToken(validToken);
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat((Boolean) response.getBody().getData().get(TOKEN_VALID), is(true));
  }
  
  @Test
  public void shouldUpdateUserPassword(){
    String password = "newPassword";
    when(messageService.message("password.reset")).thenReturn("password.reset");

    ResponseEntity<OpenLmisResponse> response = userController.updateUserPassword(userId, password);

    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody().getSuccessMsg(), is("password.reset"));
    verify(userService).updateUserPassword(userId, password);
  }

}
