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
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.MaintenanceRequest;
import org.openlmis.equipment.repository.MaintenanceRequestRepository;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(MaintenanceRequestService.class)
public class MaintenanceRequestServiceTest {

  @Mock
  MaintenanceRequestRepository repository;

  @InjectMocks
  MaintenanceRequestService service;

  @Test
  public void shouldGetAll() throws Exception {
    service.getAll();
    verify(repository).getAll();
  }

  @Test
  public void shouldGetAllForFacility() throws Exception {
    service.getAllForFacility(1L);
    verify(repository).getAllForFacility(1L);
  }

  @Test
  public void shouldGetAllForVendor() throws Exception {
    service.getAllForVendor(3L);
    verify(repository).getAllForVendor(3L);
  }

  @Test
  public void shouldGetOutstandingForVendor() throws Exception {
    service.getOutstandingForVendor(3L);
    verify(repository).getOutstandingForVendor(3L);
  }

  @Test
  public void shouldGetOutstandingForUser() throws Exception {
    service.getOutstandingForUser(3L);
    verify(repository).getOutstandingForUser(3L);
  }

  @Test
  public void shouldGetById() throws Exception {
    service.getById(6L);
    verify(repository).getById(6L);
  }

  @Test
  public void shouldSaveAnUpdate() throws Exception {
    MaintenanceRequest request = new MaintenanceRequest();
    request.setId(3L);
    service.save(request);
    verify(repository, never()).insert(request);
    verify(repository).update(request);
  }

  @Test
  public void shouldSaveANewRecord() throws Exception {
    MaintenanceRequest request = new MaintenanceRequest();
//TODO: compete this section
//    service.save(request);
//    verify(repository).insert(request);
//    verify(repository, never()).update(request);
  }

  @Test
  public void shouldGetFullHistory() throws Exception {
    service.getFullHistory(3L);
    verify(repository).getFullHistory(3L);
  }
}