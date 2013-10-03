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
import org.openlmis.core.domain.Vendor;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.VendorService;
import org.openlmis.db.categories.UnitTests;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.*;
@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RestApiAuthenticationProviderTest {

  @Rule
  public ExpectedException expectedException = none();

  @Mock
  VendorService vendorService;

  @Mock
  MessageService messageService;

  @InjectMocks
  RestApiAuthenticationProvider restApiAuthenticationProvider;

  @Test
  public void shouldThrowExceptionForInvalidCredentialsDuringVendorAuthentication() throws Exception {
    Authentication authentication = mock(Authentication.class);
    String externalSystem = "externalSystem";
    when(authentication.getPrincipal()).thenReturn(externalSystem);
    when(authentication.getCredentials()).thenReturn("invalid token");

    Vendor vendor = new Vendor();
    vendor.setName(externalSystem);
    vendor.setAuthToken("invalid token");
    when(vendorService.authenticate(vendor)).thenReturn(false);
    when(messageService.message("error.authentication.failed")).thenReturn("message");

    expectedException.expect(BadCredentialsException.class);
    expectedException.expectMessage("message");

    restApiAuthenticationProvider.authenticate(authentication);

    verify(vendorService).authenticate(vendor);
  }

  @Test
  public void shouldReturnAuthenticationIfVendorValidAndAuthenticated() throws Exception {
    Authentication authentication = mock(Authentication.class);
    String externalSystem = "externalSystem";
    when(authentication.getPrincipal()).thenReturn(externalSystem);
    when(authentication.getCredentials()).thenReturn("valid token");

    Vendor vendor = new Vendor();
    vendor.setName(externalSystem);
    vendor.setAuthToken("valid token");
    when(vendorService.authenticate(vendor)).thenReturn(true);

    Authentication authenticated = restApiAuthenticationProvider.authenticate(authentication);

    assertThat(authenticated.getPrincipal(), is(authentication.getPrincipal()));
    verify(vendorService).authenticate(vendor);
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
