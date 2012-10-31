package org.openlmis.web.authentication;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserAuthenticationSuccessHandlerTest {

    UserAuthenticationSuccessHandler userAuthenticationSuccessHandler;
    MockHttpServletRequest request;
    MockHttpServletResponse response;
    String SAVED_REQUEST = "SPRING_SECURITY_SAVED_REQUEST";

    @Before
    public void setup() {
        userAuthenticationSuccessHandler = new UserAuthenticationSuccessHandler();
        response = new MockHttpServletResponse();
    }

    @Test
    public void shouldRedirectAdminToHomeWhenNoSavedRequestPresent() throws IOException, ServletException {
        request = new MockHttpServletRequest();
        Authentication authentication = getAdminAuthenticationObj();
        userAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        assertThat(response.getRedirectedUrl(), is(equalTo("/")));
    }

    @Test
    public void shouldRedirectUserToHomeNoSavedRequestPresent() throws IOException, ServletException {
        request = new MockHttpServletRequest();
        Authentication authentication = getUserAuthenticationObj();
        userAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        assertThat(response.getRedirectedUrl(), is(equalTo("/")));
    }

    @Test
    public void shouldRedirectToRequestedUrl() throws ServletException, IOException {

        String contextPathUrl = "http:localhost:8080";
        String requestRelativeUrl = "/testUrl";
        HttpSession session = mock(HttpSession.class);
        request = new MockHttpServletRequest(RequestMethod.GET.toString(), contextPathUrl + requestRelativeUrl);
        request.setSession(session);
        Authentication authentication = getAdminAuthenticationObj();

        DefaultSavedRequest defaultSavedRequest = mock(DefaultSavedRequest.class);

        when(defaultSavedRequest.getRedirectUrl()).thenReturn(new String("/testUrl"));
        when(session.getAttribute(SAVED_REQUEST)).thenReturn(defaultSavedRequest);

        userAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        assertThat(response.getRedirectedUrl(), is(equalTo(requestRelativeUrl)));
    }

    private Authentication getAdminAuthenticationObj() {
        return new TestingAuthenticationToken("user", "null", "ADMIN");
    }

    private Authentication getUserAuthenticationObj() {
        return new TestingAuthenticationToken("user", "null", "USER");
    }


}
