/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.restapi.authentication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Vendor;
import org.openlmis.core.service.VendorService;
import org.springframework.security.core.Authentication;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RestApiAuthenticationProviderTest {

  @Mock
  VendorService vendorService;

  @InjectMocks
  RestApiAuthenticationProvider restApiAuthenticationProvider;

  @Test
  public void shouldAuthenticateVendorWithNameAndAuthTokenAndReturnNullIfInvalid() throws Exception {
    Authentication authentication = mock(Authentication.class);
    String externalSystem = "externalSystem";
    when(authentication.getPrincipal()).thenReturn(externalSystem);
    when(authentication.getCredentials()).thenReturn("invalid token");

    Vendor vendor = new Vendor();
    vendor.setName(externalSystem);
    vendor.setAuthToken("invalid token");
    when(vendorService.authenticate(vendor)).thenReturn(false);

    Authentication authenticated = restApiAuthenticationProvider.authenticate(authentication);

    assertThat(authenticated, is(nullValue()));
    verify(vendorService).authenticate(vendor);
  }

  @Test
  public void shouldReturnNullIfVendorName() throws Exception {
    Authentication authentication = mock(Authentication.class);
    String nullSystem = null;
    when(authentication.getPrincipal()).thenReturn(nullSystem);

    Authentication authenticated = restApiAuthenticationProvider.authenticate(authentication);

    assertThat(authenticated, is(nullValue()));
    verify(vendorService, never()).getByName(nullSystem);
  }

  @Test
  public void shouldReturnNullIfAuthTokenNull() throws Exception {
    Authentication authentication = mock(Authentication.class);
    String nullSystem = null;
    when(authentication.getCredentials()).thenReturn(nullSystem);

    Authentication authenticated = restApiAuthenticationProvider.authenticate(authentication);

    assertThat(authenticated, is(nullValue()));
    verify(vendorService, never()).getByName(nullSystem);
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
}
