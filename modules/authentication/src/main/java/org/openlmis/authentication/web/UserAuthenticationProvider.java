package org.openlmis.authentication.web;

import org.openlmis.authentication.UserToken;
import org.openlmis.authentication.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.HashMap;
import java.util.Map;

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
        UserToken userToken =  userAuthenticationService.authorizeUser(userName, password);

        if (userToken.isAuthenticated()) {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userName, password, null);
            Map userDetails = new HashMap();
            userDetails.put(UserAuthenticationSuccessHandler.USER_ID, userToken.getUserId());
            usernamePasswordAuthenticationToken.setDetails(userDetails);
            return usernamePasswordAuthenticationToken;
        } else {
          return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
