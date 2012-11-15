package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.web.authentication.UserAuthenticationSuccessHandler;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserControllerTest {

    private MockHttpSession session;

    @Mock
    private HttpServletRequest httpServletRequest;

    private UserController userController;

    @Before
    public void setUp() {
        initMocks(this);
        session = new MockHttpSession();
        userController = new UserController();
    }

    @Test
    public void shouldReturnUserInfoWhenLoggedIn() {
        String username = "Foo";
        session.setAttribute(UserAuthenticationSuccessHandler.USER, username);
        when(httpServletRequest.getSession()).thenReturn(session);
        HashMap<String, String> params = userController.user(httpServletRequest, null);
        assertThat(params.size(), is(2));
        assertThat(params.get("name"), is("Foo"));
        assertThat(params.get("authenticated"), is("true"));
    }

    @Test
    public void shouldNotReturnUserInfoWhenNotLoggedIn() {
        session.setAttribute(UserAuthenticationSuccessHandler.USER, null);
        when(httpServletRequest.getSession()).thenReturn(session);
        HashMap<String, String> params = userController.user(httpServletRequest, "true");
        assertThat(params.size(), is(2));
        assertThat(params.get("error"), is("true"));
        assertThat(params.get("authenticated"), is("false"));
    }

}
