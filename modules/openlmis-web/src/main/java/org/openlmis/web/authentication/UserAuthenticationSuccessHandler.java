package org.openlmis.web.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class UserAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String USER = "USER";
    public static final String IS_ADMIN = "IS_ADMIN";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        request.getSession().setAttribute(USER, authentication.getPrincipal());
        request.getSession().setAttribute(IS_ADMIN, isAdmin(authentication));

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private boolean isAdmin(Authentication authentication) {
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if (grantedAuthority.getAuthority().equals("ADMIN")) {
                return true;
            }
        }
        return false;
    }

}
