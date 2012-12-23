package org.openlmis.authentication.web;

import org.openlmis.authentication.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

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
        Boolean isAuthenticated =  userAuthenticationService.authorizeUser(userName, password);

        if (isAuthenticated) {
          return new UsernamePasswordAuthenticationToken(userName, password, null);
        } else {
          return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
