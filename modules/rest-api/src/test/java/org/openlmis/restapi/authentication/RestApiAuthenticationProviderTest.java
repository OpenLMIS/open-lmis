/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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

    expectedException.expect(BadCredentialsException.class);
    expectedException.expectMessage("Could not authenticate Vendor");

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
