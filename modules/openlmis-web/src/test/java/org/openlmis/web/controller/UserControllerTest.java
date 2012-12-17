package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.Right;
import org.openlmis.core.service.RoleRightsService;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserControllerTest {

  private MockHttpSession session;

  @Mock
  private HttpServletRequest httpServletRequest;

  private UserController userController;

  @Mock
  private RoleRightsService roleRightService;

  @Before
  public void setUp() {
    initMocks(this);
    session = new MockHttpSession();
    when(httpServletRequest.getSession()).thenReturn(session);
    userController = new UserController(roleRightService);
  }

  @Test
  public void shouldReturnUserInfoOfLoggedInUser() {
    String username = "Foo";
    session.setAttribute(UserAuthenticationSuccessHandler.USER, username);
    session.setAttribute(UserAuthenticationSuccessHandler.IS_ADMIN, false);
    HashMap<String, Object> params = userController.user(httpServletRequest, null);
    assertThat(params.get("name").toString(), is("Foo"));
    assertThat(params.get("authenticated").toString(), is("true"));
  }



  @Test
  public void shouldNotReturnUserInfoWhenNotLoggedIn() {
    session.setAttribute(UserAuthenticationSuccessHandler.USER, null);
    HashMap<String, Object> params = userController.user(httpServletRequest, "true");
    assertThat(params.get("error").toString(), is("true"));
    assertThat(params.get("authenticated").toString(), is("false"));
  }

  @Test
  public void shouldGetAllPrivilegesForTheLoggedInUser() throws Exception {
    String username = "Foo";
    session.setAttribute(UserAuthenticationSuccessHandler.USER, username);
    List<Right> listOfRights = new ArrayList<>();
    when(roleRightService.getRights(username)).thenReturn(listOfRights);
    HashMap<String, Object> params = userController.user(httpServletRequest, "true");
    verify(roleRightService).getRights(username);
    assertThat((List<Right>)params.get("rights"), is(listOfRights));
  }
}
