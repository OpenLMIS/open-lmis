/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.domain;


import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.db.categories.UnitTests;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
public class VendorTest {

  private Vendor vendor;

  @Before
  public void setUp() throws Exception {
    vendor = new Vendor();
    vendor.setId(1L);
    vendor.setAuthToken(UUID.randomUUID().toString());
    vendor.setActive(true);
    vendor.setName("test vendor");
  }

  @Test
  public void shouldValidateVendorAndReturnFalseIfIdNull() throws Exception {
    vendor.setName(null);

    assertThat(vendor.isValid(), is(false));
  }

  @Test
  public void shouldValidateVendorAndReturnFalseIfAuthTokenNull() throws Exception {
    vendor.setAuthToken(null);

    assertThat(vendor.isValid(), is(false));
  }

  @Test
  public void shouldValidateVendorAndReturnTrueIfValid() throws Exception {
    assertThat(vendor.isValid(), is(true));
  }
}
