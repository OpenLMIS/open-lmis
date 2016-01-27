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

package org.openlmis.equipment.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.VendorUser;
import org.openlmis.equipment.repository.VendorUserRepository;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VendorUserServiceTest {

  @Mock
  VendorUserRepository repository;

  @InjectMocks
  VendorUserService service;

  @Test
  public void shouldGetAllUsersForVendor() throws Exception {
    service.getAllUsersForVendor(3L);
    verify(repository).getAllUsersForVendor(3L);
  }

  @Test
  public void shouldSave() throws Exception {
    VendorUser vendorUser = new VendorUser();
    service.save(vendorUser);
    verify(repository).insert(vendorUser);
    verify(repository, never()).update(vendorUser);
  }

  @Test
  public void shouldSaveUpdates() throws Exception {
    VendorUser vendorUser = new VendorUser();
    vendorUser.setId(20L);
    service.save(vendorUser);
    verify(repository, never()).insert(vendorUser);
    verify(repository).update(vendorUser);
  }

  @Test
  public void shouldRemoveVendorUserAssociation() throws Exception {
    service.removeVendorUserAssociation(2L, 30L);
    verify(repository).removeVendorUserAssociation(2L, 30L);
  }

  @Test
  public void shouldGetAllUsersAvailableForVendor() throws Exception {
    service.getAllUsersAvailableForVendor();
    verify(repository).getAllUsersAvailableForVendor();
  }
}