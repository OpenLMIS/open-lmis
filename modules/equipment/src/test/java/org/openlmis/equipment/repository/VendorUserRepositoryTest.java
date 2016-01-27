/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.equipment.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.VendorUser;
import org.openlmis.equipment.repository.mapper.VendorUserMapper;

import static org.mockito.Mockito.verify;



@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VendorUserRepositoryTest {

  @Mock
  VendorUserMapper mapper;

  @InjectMocks
  VendorUserRepository repository;

  @Test
  public void shouldGetAllUsersForVendor() throws Exception {
    repository.getAllUsersForVendor(1L);
    verify(mapper).getAllUsersForVendor(1L);
  }

  @Test
  public void shouldInsert() throws Exception {
    VendorUser vendorUser = new VendorUser();
    repository.insert(vendorUser);

    verify(mapper).insert(vendorUser);
  }

  @Test
  public void shouldUpdate() throws Exception {
    VendorUser vendorUser = new VendorUser();
    repository.update(vendorUser);

    verify(mapper).update(vendorUser);
  }

  @Test
  public void shouldRemoveVendorUserAssociation() throws Exception {
    repository.removeVendorUserAssociation(1L, 2L);
    verify(mapper).remove(1L, 2L);
  }

  @Test
  public void shouldGetAllUsersAvailableForVendor() throws Exception {
    repository.getAllUsersAvailableForVendor();
    verify(mapper).getAllUsersAvailableForVendor();
  }
}