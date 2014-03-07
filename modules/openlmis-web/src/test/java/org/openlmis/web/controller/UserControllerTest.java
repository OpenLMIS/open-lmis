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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.core.service.UserService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.session.SessionRegistry;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
import static org.openlmis.web.controller.UserController.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
public class UserControllerTest {

  private Long userId;

  @Mock
  private MockHttpSession session;

  @Mock
  private MockHttpServletRequest httpServletRequest;

  @Mock
  private RoleRightsService roleRightService;

  @Mock
  private UserService userService;

  @Mock
  private MessageService messageService;

  @Mock
  private SessionRegistry sessionRegistry;

  @InjectMocks
  private UserController userController;

  @Mock
  String baseUrl = "http://localhost:9091";

  @Before
  public void setUp() {
    userId = 3L;
    httpServletRequest = new MockHttpServletRequest();
    session = new MockHttpSession();
    httpServletRequest.setSession(session);
  }

  @Test
  public void shouldReturnUserInfoOfLoggedInUser() {
    String username = "Foo";
    long userId = 1234L;
    session.setAttribute(UserAuthenticationSuccessHandler.USER, username);
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, userId);
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
    Long userId = 1234L;
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, userId);
    Set<Right> rights = new HashSet<>();
    when(roleRightService.getRights(userId)).thenReturn(rights);
    ResponseEntity<OpenLmisResponse> response = userController.user(httpServletRequest);
    verify(roleRightService).getRights(userId);
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
    doThrow(new DataException("some error")).when(userService).sendForgotPasswordEmail(eq(user), anyString());

    ResponseEntity<OpenLmisResponse> response = userController.sendPasswordTokenEmail(user);

    assertThat(response.getStatusCode(), is(BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("some error"));
  }

  @Test
  public void shouldSaveUser() throws Exception {
    User user = new User();
    httpServletRequest.getSession().setAttribute(USER_ID, userId);
    httpServletRequest.getSession().setAttribute(USER, USER);
    ResponseEntity<OpenLmisResponse> response = userController.create(user, httpServletRequest);

    verify(userService).create(eq(user), eq("http://localhost:9091/public/pages/reset-password.html#/token/"));

    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat((User) response.getBody().getData().get("user"), is(user));
    assertThat(response.getBody().getSuccessMsg(), is(USER_CREATED_SUCCESS_MSG));
  }

  @Test
  public void shouldUpdateUser() throws Exception {
    User user = make(a(defaultUser));
    user.setId(userId);
    httpServletRequest.getSession().setAttribute(USER_ID, userId);
    httpServletRequest.getSession().setAttribute(USER, USER);

    ResponseEntity<OpenLmisResponse> response = userController.update(user, userId, httpServletRequest);

    verify(userService).update(user);

    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(user.getModifiedBy(), is(userId));
  }

  @Test
  public void shouldReturnErrorIfSaveUserFails() throws Exception {
    User user = new User();
    doThrow(new DataException("Save user failed")).when(userService).create(eq(user), anyString());
    ResponseEntity<OpenLmisResponse> response = userController.create(user, httpServletRequest);

    assertThat(response.getStatusCode(), is(BAD_REQUEST));
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
    assertThat(response.getStatusCode(), is(BAD_REQUEST));
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
  public void shouldUpdateUserPassword() {
    String password = "newPassword";
    when(messageService.message("password.reset.success")).thenReturn("password.reset");

    ResponseEntity<OpenLmisResponse> response = userController.updateUserPassword(userId, password);

    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody().getSuccessMsg(), is("password.reset"));
    verify(userService).updateUserPassword(userId, password);
  }

  @Test
  public void shouldDisableUser() throws Exception {
    Long modifiedBy = 1L;
    httpServletRequest.getSession().setAttribute(USER_ID, modifiedBy);
    doNothing().when(userService).disable(userId, modifiedBy);

    ResponseEntity<OpenLmisResponse> response = userController.disable(userId, httpServletRequest);

    String successMsg = response.getBody().getSuccessMsg();

    assertThat(successMsg, is(MSG_USER_DISABLE_SUCCESS));
    verify(userService).disable(userId, modifiedBy);
  }
}
