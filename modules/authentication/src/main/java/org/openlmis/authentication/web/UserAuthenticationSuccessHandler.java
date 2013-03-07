package org.openlmis.authentication.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


public class UserAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String USER = "USER";
    public static final String USER_ID = "USER_ID";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        request.getSession().setAttribute(USER_ID, authentication.getPrincipal());
        Map userDetails = (Map) authentication.getDetails();
        request.getSession().setAttribute(USER, userDetails.get(USER));
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
