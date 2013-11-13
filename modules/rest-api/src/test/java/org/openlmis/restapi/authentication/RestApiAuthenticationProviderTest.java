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

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.User;
import org.openlmis.core.hash.Encoder;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.UserService;
import org.openlmis.db.categories.UnitTests;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RestApiAuthenticationProviderTest {

  @Rule
  public ExpectedException expectedException = none();

  @Mock
  MessageService messageService;

  @Mock
  UserService userService;

  @Mock
  Authentication authentication;

  @InjectMocks
  RestApiAuthenticationProvider restApiAuthenticationProvider;

  @Test
  public void shouldThrowExceptionForInvalidCredentialsDuringUserAuthentication() throws Exception {
    String userName = "userName";
    String password = "invalid token";
    when(authentication.getPrincipal()).thenReturn(userName);
    when(authentication.getCredentials()).thenReturn(password);

    when(userService.selectUserByUserNameAndPassword(userName, password)).thenReturn(null);
    when(messageService.message("error.authentication.failed")).thenReturn("message");

    expectedException.expect(BadCredentialsException.class);
    expectedException.expectMessage("message");

    restApiAuthenticationProvider.authenticate(authentication);
  }

  @Test
  public void shouldReturnAuthenticationIfUserValidAndAuthenticated() throws Exception {
    String userName = "userName";
    String password = "valid token";
    when(authentication.getPrincipal()).thenReturn(userName);
    when(authentication.getCredentials()).thenReturn(password);

    when(userService.selectUserByUserNameAndPassword(userName, Encoder.hash(password))).thenReturn(new User(55L, "userName"));

    Authentication authenticated = restApiAuthenticationProvider.authenticate(authentication);

    assertThat((Long) authenticated.getPrincipal(), is(55L));
    verify(userService).selectUserByUserNameAndPassword(userName, Encoder.hash(password));
  }

  @Test
  public void shouldReturnFalseIfAuthenticationNotOfTypeUsernamePasswordAuthenticationToken() throws Exception {
    boolean supported = restApiAuthenticationProvider.supports(Authentication.class);
    assertFalse(supported);
  }

  @Test
  public void shouldReturnTrueIfAuthenticationOfTypeUsernamePasswordAuthenticationToken() throws Exception {
    boolean supported = restApiAuthenticationProvider.supports(UsernamePasswordAuthenticationToken.class);
    assertTrue(supported);
  }
}
