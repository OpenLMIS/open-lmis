package org.openlmis.web.authentication;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;


public class UserAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        if (ifRolesContains(authentication.getAuthorities(),"ADMIN")) {
            response.sendRedirect("/admin/home");
            return;
        }
        response.sendRedirect("/home");
    }

    private boolean ifRolesContains(Collection<? extends GrantedAuthority> authorities, final String role) {
        return CollectionUtils.exists(authorities, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                GrantedAuthority grantedAuthority = (GrantedAuthority) o;
                return grantedAuthority.getAuthority().equalsIgnoreCase(role);
            }
        });

    }



}
