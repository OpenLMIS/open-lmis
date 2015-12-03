/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.authentication.web;

import org.openlmis.authentication.domain.UserToken;
import org.openlmis.authentication.service.UserAuthenticationService;
import org.openlmis.core.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.HashMap;
import java.util.Map;

/**
 * This class acts as an authentication provider. It authenticates user credentials.
 */

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
    UserToken userToken = userAuthenticationService.authenticateUser(user);

    if (userToken.isAuthenticated()) {
      UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userToken.getUserId(), null, null);
      Map<String, String> userDetails = new HashMap<>();
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
