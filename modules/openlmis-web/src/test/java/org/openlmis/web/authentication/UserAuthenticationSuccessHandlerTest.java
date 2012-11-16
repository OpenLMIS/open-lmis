package org.openlmis.web.authentication;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.IS_ADMIN;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;

public class UserAuthenticationSuccessHandlerTest {

    public static final String CONTEXT_PATH = "contextPath";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    @Mock
    MockHttpServletRequest request;

    MockHttpServletResponse response;

    @Mock
    private HttpSession session;

    String SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";

    UserAuthenticationSuccessHandler userAuthenticationSuccessHandler;

    @Before
    public void setup() {
        initMocks(this);
        userAuthenticationSuccessHandler = new UserAuthenticationSuccessHandler();

        when(request.getSession()).thenReturn(session);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);

        response = new MockHttpServletResponse();
    }

    @Test
    public void shouldRedirectUserToHome() throws IOException, ServletException {
        String defaultTargetUrl = "/";

        Authentication authentication = new TestingAuthenticationToken(USERNAME, "password", "USER");
        userAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        assertEquals(CONTEXT_PATH + defaultTargetUrl, response.getRedirectedUrl());
    }

    @Test
    public void shouldSaveUsernameInSession() throws IOException, ServletException {
        Authentication authentication = new TestingAuthenticationToken(USERNAME, "password", "USER");
        userAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(session).setAttribute(USER, USERNAME);
    }

    @Test

    public void shouldSaveUserIfAdminInSession() throws IOException, ServletException {
        Authentication authentication = new TestingAuthenticationToken(USERNAME, PASSWORD, "ADMIN");
        userAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(session).setAttribute(USER, USERNAME);
        verify(session).setAttribute(IS_ADMIN, true);
    }

}
