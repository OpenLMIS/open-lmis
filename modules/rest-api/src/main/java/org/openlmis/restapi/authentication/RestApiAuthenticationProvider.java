/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.restapi.authentication;

import org.openlmis.core.domain.User;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * This class extends org.springframework.security.authentication.AuthenticationProvider
 * and is responsible for handling authentication for REST API endpoints.
 */

public class RestApiAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private UserService userService;

  MessageService messageService = MessageService.getRequestInstance();

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    User user = new User();
    user.setUserName(authentication.getPrincipal().toString());
    user.setPassword(authentication.getCredentials().toString());

    User authenticatedUser = userService.selectUserByUserNameAndPassword(user.getUserName(), user.getPassword());

    if (authenticatedUser == null)
      throw new BadCredentialsException(messageService.message("error.authentication.failed"));

    Collection<? extends GrantedAuthority> authorities = null;

    return new UsernamePasswordAuthenticationToken(authenticatedUser.getId(), user.getPassword(),
      authorities);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
  }
}
