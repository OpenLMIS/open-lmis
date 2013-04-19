/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.authentication.web;

import org.openlmis.authentication.UserToken;
import org.openlmis.authentication.service.UserAuthenticationService;
import org.openlmis.core.domain.User;
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
        User user = new User();
        user.setUserName(userName);
        user.setPassword(password);
        UserToken userToken =  userAuthenticationService.authorizeUser(user);

        if (userToken.isAuthenticated()) {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userToken.getUserId(), null, null);
            Map userDetails = new HashMap();
            userDetails.put(UserAuthenticationSuccessHandler.USER, userToken.getUserName());
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
