package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.User;
import org.openlmis.core.domain.UserRoleAssignment;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.hash.Encoder;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.core.service.UserService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;

public class UserControllerTest {

  public static final Integer userId = 1;

  private MockHttpSession session;

  private MockHttpServletRequest httpServletRequest;

  private MockHttpServletResponse httpServletResponse;


  private UserController userController;

  @Mock
  @SuppressWarnings("unused")
  private RoleRightsService roleRightService;

  @Mock
  @SuppressWarnings("unused")
  private UserService userService;

  @Before
  public void setUp() {
    initMocks(this);
    httpServletRequest = new MockHttpServletRequest();
    httpServletResponse = new MockHttpServletResponse();
    session = new MockHttpSession();
    httpServletRequest.setSession(session);

    userController = new UserController(roleRightService, userService);
  }

  @Test
  public void shouldReturnUserInfoOfLoggedInUser() {
    String username = "Foo";
    session.setAttribute(UserAuthenticationSuccessHandler.USER, username);
    HashMap<String, Object> params = userController.user(httpServletRequest, null);
    assertThat(params.get("name").toString(), is("Foo"));
    assertThat((Boolean) params.get("authenticated"), is(true));
  }


  @Test
  public void shouldNotReturnUserInfoWhenNotLoggedIn() {
    session.setAttribute(UserAuthenticationSuccessHandler.USER, null);
    HashMap<String, Object> params = userController.user(httpServletRequest, "true");
    assertThat(params.get("error").toString(), is("true"));
    assertThat((Boolean) params.get("authenticated"), is(false));
  }

  @Test
  public void shouldGetAllPrivilegesForTheLoggedInUser() throws Exception {
    String username = "Foo";
    session.setAttribute(UserAuthenticationSuccessHandler.USER, username);
    List<Right> listOfRights = new ArrayList<>();
    when(roleRightService.getRights(username)).thenReturn(listOfRights);
    HashMap<String, Object> params = userController.user(httpServletRequest, "true");
    verify(roleRightService).getRights(username);
    assertThat((List<Right>) params.get("rights"), is(listOfRights));
  }

  @Test
  public void shouldEmailPasswordTokenForUser() throws Exception {
    User user = new User();
    user.setUserName("Manan");
    user.setEmail("manan@thoughtworks.com");
    userController.sendPasswordTokenEmail(user, httpServletRequest);
    verify(userService).sendForgotPasswordEmail(eq(user), anyString());
  }

  @Test
  public void shouldReturnErrorIfSendingForgotPasswordEmailFails() throws Exception {
    User user = new User();
    doThrow(new DataException("some error")).when(userService).sendForgotPasswordEmail(eq(user), anyString());

    ResponseEntity<OpenLmisResponse> response = userController.sendPasswordTokenEmail(user, httpServletRequest);

    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("some error"));
  }

  @Test
  public void shouldSaveUser() throws Exception {
    User user = new User();
    user.setFirstName("Shan");
    user.setLastName("Sharma");

    List<UserRoleAssignment> userRoleAssignments = new ArrayList<>();
    UserRoleAssignment roleAssignment = new UserRoleAssignment(2, Arrays.asList(1, 2));
    userRoleAssignments.add(roleAssignment);
    user.setRoleAssignments(userRoleAssignments);

    httpServletRequest.getSession().setAttribute(USER_ID, userId);
    httpServletRequest.getSession().setAttribute(USER, USER);
    ResponseEntity<OpenLmisResponse> response = userController.create(user, httpServletRequest);

    verify(userService).create(eq(user), anyString());

    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody().getSuccessMsg(), is("User " + user.getFirstName() + " " + user.getLastName() + " has been successfully created, password link sent on registered Email address"));
    assertThat(user.getPassword(), is(Encoder.hash("openLmis123")));
    assertThat(user.getModifiedBy(), is(USER));
  }

  @Test
  public void shouldUpdateUser() throws Exception {
    User user = new User();
    user.setId(1);
    user.setFirstName("Shan");
    user.setLastName("Sharma");
    user.setPassword("password");
    httpServletRequest.getSession().setAttribute(USER_ID, userId);
    httpServletRequest.getSession().setAttribute(USER, USER);

    List<UserRoleAssignment> userRoleAssignments = new ArrayList<>();
    UserRoleAssignment roleAssignment = new UserRoleAssignment(2, Arrays.asList(1, 2));
    userRoleAssignments.add(roleAssignment);
    user.setRoleAssignments(userRoleAssignments);

    user.setRoleAssignments(userRoleAssignments);

    ResponseEntity<OpenLmisResponse> response = userController.update(user, 1, httpServletRequest);

    verify(userService).update(user);

    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody().getSuccessMsg(), is("User " + user.getFirstName() + " " + user.getLastName() + " has been successfully updated"));
    assertThat(user.getPassword(), is(Encoder.hash("password")));
    assertThat(user.getModifiedBy(), is(USER));
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
    when(userService.getById(1)).thenReturn(user);

    User returnedUser = userController.getById(1);

    assertThat(returnedUser, is(user));

  }

  @Test
  public void shouldReturnErrorResponseIfTokenIsNotValid() throws IOException, ServletException {
    String invalidToken = "invalidToken";

    userController.validatePasswordResetToken(invalidToken);

    verify(userService).getUserIdForPasswordResetToken(invalidToken);
  }

}
