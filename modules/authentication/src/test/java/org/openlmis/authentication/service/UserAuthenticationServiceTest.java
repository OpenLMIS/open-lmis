/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.authentication.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.Mock;
import org.openlmis.authentication.domain.UserToken;
import org.openlmis.core.domain.User;
import org.openlmis.core.hash.Encoder;
import org.openlmis.core.service.UserService;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@Category(UnitTests.class)
@PrepareForTest(Encoder.class)
public class UserAuthenticationServiceTest {
  private UserAuthenticationService userAuthenticationService;

  @Mock
  @SuppressWarnings("unused")
  private UserService userService;

  @Before
  public void setup() {
    initMocks(this);
    userAuthenticationService = new UserAuthenticationService(userService);
  }

  @Test
  public void shouldAuthenticateAValidUser() {

    mockStatic(Encoder.class);
    when(Encoder.hash("defaultPassword")).thenReturn("hashedPassword");
    User user = new User();
    user.setUserName("defaultUserName");
    user.setPassword("defaultPassword");
    user.setRestrictLogin(false);

    when(userService.selectUserByUserNameAndPassword("defaultUserName", "hashedPassword")).thenReturn(user);

    UserToken userToken = userAuthenticationService.authenticateUser(user);
    verify(userService).selectUserByUserNameAndPassword("defaultUserName", "hashedPassword");

    assertThat(userToken.isAuthenticated(), is(true));
  }

  @Test
  public void shouldNotAuthenticateAnInvalidUser() {
    mockStatic(Encoder.class);
    when(Encoder.hash("defaultPassword")).thenReturn("hashedPassword");
    User user = new User();
    user.setUserName("defaultUserName");
    user.setPassword("defaultPassword");

    when(userService.selectUserByUserNameAndPassword("defaultUserName", "hashedPassword")).thenReturn(null);

    UserToken userToken = userAuthenticationService.authenticateUser(user);

    verify(userService).selectUserByUserNameAndPassword("defaultUserName", "hashedPassword");
    assertThat(userToken.isAuthenticated(), is(false));
  }

  @Test
  public void shouldNotAuthenticateRestrictedUser() {
    mockStatic(Encoder.class);
    when(Encoder.hash("defaultPassword")).thenReturn("hashedPassword");
    User user = new User();
    user.setUserName("defaultUserName");
    user.setPassword("defaultPassword");
    user.setRestrictLogin(true);

    when(userService.selectUserByUserNameAndPassword("defaultUserName", "hashedPassword")).thenReturn(user);

    UserToken userToken = userAuthenticationService.authenticateUser(user);

    verify(userService).selectUserByUserNameAndPassword("defaultUserName", "hashedPassword");
    assertThat(userToken.isAuthenticated(), is(false));
  }
}
