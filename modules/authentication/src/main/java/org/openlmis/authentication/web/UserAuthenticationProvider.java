package org.openlmis.authentication.web;

import org.openlmis.authentication.service.UserAuthenticationService;
import org.openlmis.authentication.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collection;

public class UserAuthenticationProvider implements AuthenticationProvider {

    private UserAuthenticationService userAuthenticationService;

    @Autowired
    public UserAuthenticationProvider(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String userName = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        UserToken userToken = userAuthenticationService.authorizeUser(userName, password);

        if (!userToken.isAuthenticated()) return null;

        Collection<? extends GrantedAuthority> authorities = Arrays.asList(getGrantedAuthority(userToken.getRole()));

        return new UsernamePasswordAuthenticationToken(userName, password, authorities);
    }

    private GrantedAuthority getGrantedAuthority(final String role) {
        return new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return role;
            }
        };
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
