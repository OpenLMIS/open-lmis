/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;


import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Vendor;
import org.openlmis.core.repository.VendorRepository;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class VendorServiceTest {

  @Mock
  VendorRepository vendorRepository;

  @InjectMocks
  VendorService service;

  @Test
  public void shouldGetVendor() throws Exception {
    Vendor expectedVendor = new Vendor();
    when(vendorRepository.getByName("vendor")).thenReturn(expectedVendor);

    Vendor vendor = service.getByName("vendor");

    assertThat(vendor, is(expectedVendor));
    verify(vendorRepository).getByName("vendor");
  }

  @Test
  public void shouldReturnFalseIfNameAndAuthTokenInvalid() throws Exception {
    Vendor vendor = new Vendor();
    String extSystem = "external system";
    vendor.setName(extSystem);
    vendor.setAuthToken("invalid token");

    when(vendorRepository.getToken(extSystem)).thenReturn("valid token");

    boolean authenticated = service.authenticate(vendor);

    assertFalse(authenticated);
    verify(vendorRepository).getToken(vendor.getName());
  }

  @Test
  public void shouldReturnTrueIfNameAndAuthTokenValid() throws Exception {
    Vendor vendor = new Vendor();
    String extSystem = "external system";
    vendor.setName(extSystem);
    vendor.setAuthToken("valid token");

    when(vendorRepository.getToken(extSystem)).thenReturn("valid token");

    boolean authenticated = service.authenticate(vendor);

    assertTrue(authenticated);
    verify(vendorRepository).getToken(vendor.getName());
  }

  @Test
  public void shouldGetVendorByUserId() throws Exception {
    Vendor expectedVendor = new Vendor();
    when(vendorRepository.getByUserId(1L)).thenReturn(expectedVendor);

    Vendor actualVendor = service.getByUserId(1L);

    assertThat(actualVendor, is((expectedVendor)));
    verify(vendorRepository).getByUserId(1L);
  }

  @Test
  public void shouldReturnFalseIfVendorIsInvalid() {
    Vendor vendor = mock(Vendor.class);
    when(vendor.isValid()).thenReturn(false);
    assertThat(service.authenticate(vendor), is(Boolean.FALSE));
  }
}
