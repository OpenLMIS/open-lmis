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
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.email.service.EmailService;
import org.openlmis.equipment.builder.EquipmentInventoryBuilder;
import org.openlmis.equipment.domain.EquipmentInventory;
import org.openlmis.equipment.domain.MaintenanceLog;
import org.openlmis.equipment.domain.MaintenanceRequest;
import org.openlmis.equipment.domain.ServiceContract;
import org.openlmis.equipment.repository.EquipmentInventoryRepository;
import org.openlmis.equipment.repository.MaintenanceLogRepository;
import org.openlmis.equipment.repository.ServiceContractRepository;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(MaintenanceLogService.class)
public class MaintenanceLogServiceTest {

  @Mock
  EmailService emailService;

  @Mock
  ConfigurationSettingService settingService;

  @Mock
  MaintenanceLogRepository repository;

  @Mock
  ServiceContractRepository serviceContractRepository;

  @Mock
  EquipmentInventoryRepository equipmentInventoryRepository;

  @InjectMocks
  MaintenanceLogService service;

  @Test
  public void shouldGetAll() throws Exception {
    service.getAll();
    verify(repository).getAll();
  }

  @Test
  public void shouldGetAllForFacility() throws Exception {
    service.getAllForFacility(3L);
    verify(repository).getAllForFacility(3L);
  }

  @Test
  public void shouldGetAllForVendor() throws Exception {
    service.getAllForVendor(3L);
    verify(repository).getAllForVendor(3L);

  }

  @Test
  public void shouldGetById() throws Exception {
    service.getById(4L);
    verify(repository).getById(4L);
  }

  @Test
  public void shouldInserOnNewSave() throws Exception {
    MaintenanceLog log = new MaintenanceLog();
    log.setId(null);
    service.save(log);
    verify(repository).insert(log);
    verify(repository, never()).update(log);
  }


  @Test
  public void shouldUpdateOnExistingSave() throws Exception {
    MaintenanceLog log = new MaintenanceLog();
    log.setId(3L);
    service.save(log);
    verify(repository, never()).insert(log);
    verify(repository).update(log);
  }

  @Test
  public void shouldSaveMaintenanceRequestForFacilityWithoutServiceContract() throws Exception {
    EquipmentInventory inventory = make(a(EquipmentInventoryBuilder.defaultEquipmentInventory));
    inventory.setEquipmentId(3L);
    when(equipmentInventoryRepository.getInventoryById(1L)).thenReturn(inventory);
    when(serviceContractRepository.getAllForEquipment(3L)).thenReturn(null);

    MaintenanceRequest request = new MaintenanceRequest();

    request.setInventoryId(1L);
    service.save(request);
    verify(repository).insert(any(MaintenanceLog.class));

  }

  @Test
  public void shouldSaveMaintenanceRequestForFacilityWithContract() throws Exception {
    EquipmentInventory inventory = make(a(EquipmentInventoryBuilder.defaultEquipmentInventory));
    inventory.setEquipmentId(3L);

    when(equipmentInventoryRepository.getInventoryById(1L)).thenReturn(inventory);
    ServiceContract contract = new ServiceContract();

    when(serviceContractRepository.getAllForEquipment(3L)).thenReturn(asList(contract));
    MaintenanceRequest request = new MaintenanceRequest();

    request.setInventoryId(1L);
    service.save(request);
    verify(repository).insert(any(MaintenanceLog.class));

  }
}