/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository;


import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Vendor;
import org.openlmis.core.repository.mapper.VendorMapper;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VendorRepositoryTest {


  @Mock
  VendorMapper vendorMapper;

  @InjectMocks
  VendorRepository vendorRepository;

  @Test
  public void shouldGetVendor() {

    Vendor expectedVendor = new Vendor();
    when(vendorMapper.getByName("vendor")).thenReturn(expectedVendor);

    Vendor vendor = vendorRepository.getByName("vendor");

    assertThat(vendor, is(expectedVendor));
    verify(vendorMapper).getByName("vendor");

  }

  @Test
  public void shouldGetAuthTokenForVendor() throws Exception {
    String vendor = "some vendor";
    when(vendorMapper.getToken(vendor)).thenReturn("some token");
    String token = vendorRepository.getToken(vendor);

    assertThat(token, is("some token"));
    verify(vendorMapper).getToken(vendor);
  }

  @Test
  public void shouldGetVendorForUserId() throws Exception {
    Vendor expectedVendor = new Vendor();
    when(vendorMapper.getByUserId(1L)).thenReturn(expectedVendor);

    Vendor vendor = vendorRepository.getByUserId(1L);

    assertThat(vendor, is(expectedVendor));
    verify(vendorMapper).getByUserId(1L);
  }
}
