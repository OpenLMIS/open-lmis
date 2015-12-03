/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.authentication;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.authentication.domain.UserToken;
import org.openlmis.authentication.service.UserAuthenticationService;
import org.openlmis.authentication.web.UserAuthenticationProvider;
import org.openlmis.core.domain.User;
import org.openlmis.db.categories.UnitTests;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
public class UserAuthenticationProviderTest {

  private UserAuthenticationService userService;
  private UserAuthenticationProvider userAuthenticationProvider;

  @Before
  public void setup() {
    userService = mock(UserAuthenticationService.class);
    userAuthenticationProvider = new UserAuthenticationProvider(userService);
  }

  @Test
  public void shouldAuthenticateValidUser() {

    String validUser = "validUser";
    String password = "password";
    User user = new User();
    user.setUserName(validUser);
    user.setPassword(password);
    when(userService.authenticateUser(user)).thenReturn(new UserToken(validUser, 1L, true));
    Authentication authentication = new TestingAuthenticationToken(validUser, password);

    Authentication authenticate = userAuthenticationProvider.authenticate(authentication);


    assertThat(authenticate, instanceOf(UsernamePasswordAuthenticationToken.class));
    assertThat((Long) authenticate.getPrincipal(), is(equalTo(1L)));
    assertThat(authenticate.getCredentials(), is(equalTo(null)));
    assertThat(authenticate.isAuthenticated(), is(true));
  }


  @Test
  public void shouldNotAuthenticateInvalidUser() {
    String invalidUser = "invalidUser";
    String password = "password";
    User user = new User();
    user.setUserName(invalidUser);
    user.setPassword(password);
    when(userService.authenticateUser(user)).thenReturn(new UserToken(invalidUser, null, false));
    Authentication authentication = new TestingAuthenticationToken(invalidUser, password);

    Authentication authenticate = userAuthenticationProvider.authenticate(authentication);

    assertThat(authenticate, is(equalTo(null)));
  }

}
