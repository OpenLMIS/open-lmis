package org.openlmis.web.authentication;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserAuthenticationSuccessHandlerTest {

    @Test
    public void shouldRedirectAdminToAdminHome() throws IOException, ServletException {
        UserAuthenticationSuccessHandler userAuthenticationSuccessHandler = new UserAuthenticationSuccessHandler();
        HttpServletRequest request = new MockHttpServletRequest() ;
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication authentication = new TestingAuthenticationToken("admin","null", "ADMIN");
        userAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        assertThat(response.getRedirectedUrl(), is(equalTo("/admin/home")));
    }

    @Test
    public void shouldRedirectUserToCoreWeb() throws IOException, ServletException {
        UserAuthenticationSuccessHandler userAuthenticationSuccessHandler = new UserAuthenticationSuccessHandler();
        HttpServletRequest request = new MockHttpServletRequest() ;
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication authentication = new TestingAuthenticationToken("user","null", "USER");
        userAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        assertThat(response.getRedirectedUrl(), is(equalTo("/home")));
    }
}
