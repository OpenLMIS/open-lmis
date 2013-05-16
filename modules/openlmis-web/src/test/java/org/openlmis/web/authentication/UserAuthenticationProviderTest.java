/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.authentication;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.authentication.UserToken;
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
    when(userService.authorizeUser(user)).thenReturn(new UserToken(validUser, 1L, true));
    Authentication authentication = new TestingAuthenticationToken(validUser, password);

    Authentication authenticate = userAuthenticationProvider.authenticate(authentication);


    assertThat(authenticate, instanceOf(UsernamePasswordAuthenticationToken.class));
    assertThat((Long)authenticate.getPrincipal(), is(equalTo(1L)));
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
    when(userService.authorizeUser(user)).thenReturn(new UserToken(invalidUser, null, false));
    Authentication authentication = new TestingAuthenticationToken(invalidUser, password);

    Authentication authenticate = userAuthenticationProvider.authenticate(authentication);

    assertThat(authenticate, is(equalTo(null)));
  }

}
