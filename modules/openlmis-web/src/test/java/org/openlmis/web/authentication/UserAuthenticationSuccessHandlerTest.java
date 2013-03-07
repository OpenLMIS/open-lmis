package org.openlmis.web.authentication;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;

public class UserAuthenticationSuccessHandlerTest {

    public static final String CONTEXT_PATH = "contextPath";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final Integer userId = 1;

    @Mock
    MockHttpServletRequest request;

    MockHttpServletResponse response;

    @Mock
    private HttpSession session;

    String SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";

    UserAuthenticationSuccessHandler userAuthenticationSuccessHandler;
    private Map userDetails;

    @Before
    public void setup() {
        initMocks(this);
        userAuthenticationSuccessHandler = new UserAuthenticationSuccessHandler();

        when(request.getSession()).thenReturn(session);
        when(request.getSession(anyBoolean())).thenReturn(session);
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        userDetails = new HashMap();
        userDetails.put(USER, USERNAME);
        response = new MockHttpServletResponse();
    }

    @Test
    public void shouldRedirectUserToHome() throws IOException, ServletException {
        String defaultTargetUrl = "/";

        TestingAuthenticationToken authentication = new TestingAuthenticationToken(userId, "password", "USER");
        authentication.setDetails(userDetails);
        userAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        assertEquals(CONTEXT_PATH + defaultTargetUrl, response.getRedirectedUrl());
    }

    @Test
    public void shouldSaveUsernameInSession() throws IOException, ServletException {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(userId, "password", "USER");
        authentication.setDetails(userDetails);
        userAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(session).setAttribute(USER, USERNAME);
    }

    @Test
    public void shouldSaveUserIdInSession() throws IOException, ServletException {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(userId, "password", "USER");
        authentication.setDetails(userDetails);
        userAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        verify(session).setAttribute(USER_ID, userId);
    }



}
