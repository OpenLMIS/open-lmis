/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.authentication.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openlmis.authentication.UserToken;
import org.openlmis.core.domain.User;
import org.openlmis.core.hash.Encoder;
import org.openlmis.core.repository.mapper.UserMapper;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Encoder.class)
public class UserAuthenticationServiceTest {
  private UserAuthenticationService userAuthenticationService;

  @Mock
  @SuppressWarnings("unused")
  private UserMapper mockUserMapper;

  @Before
  public void setup() {
    initMocks(this);
    userAuthenticationService = new UserAuthenticationService(mockUserMapper);
  }

  @Test
  public void shouldAuthenticateAValidUser() {

    mockStatic(Encoder.class);
    when(Encoder.hash("defaultPassword")).thenReturn("hashedPassword");
    User user = new User();
    user.setUserName("defaultUserName");
    user.setPassword("defaultPassword");

    when(mockUserMapper.selectUserByUserNameAndPassword("defaultUserName", "hashedPassword")).thenReturn(user);

    UserToken userToken = userAuthenticationService.authorizeUser(user);
    verify(mockUserMapper).selectUserByUserNameAndPassword("defaultUserName", "hashedPassword");

    assertThat(userToken.isAuthenticated(), is(true));
  }

  @Test
  public void shouldNotAuthenticateAnInvalidUser() {
    mockStatic(Encoder.class);
    when(Encoder.hash("defaultPassword")).thenReturn("hashedPassword");
    User user = new User();
    user.setUserName("defaultUserName");
    user.setPassword("defaultPassword");

    when(mockUserMapper.selectUserByUserNameAndPassword("defaultUserName", "hashedPassword")).thenReturn(null);

    UserToken userToken = userAuthenticationService.authorizeUser(user);

    verify(mockUserMapper).selectUserByUserNameAndPassword("defaultUserName", "hashedPassword");
    assertThat(userToken.isAuthenticated(), is(false));
  }
}
