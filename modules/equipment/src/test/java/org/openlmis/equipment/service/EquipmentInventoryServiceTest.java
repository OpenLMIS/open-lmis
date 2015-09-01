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

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.service.FacilityService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.domain.EquipmentInventory;
import org.openlmis.equipment.domain.EquipmentType;
import org.openlmis.equipment.repository.EquipmentInventoryRepository;
import org.openlmis.equipment.repository.EquipmentRepository;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.openlmis.core.domain.RightName.MANAGE_EQUIPMENT_INVENTORY;
import static org.powermock.api.mockito.PowerMockito.when;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class EquipmentInventoryServiceTest {

  private static Logger logger = Logger.getLogger(EquipmentInventoryServiceTest.class);

  @Mock
  private EquipmentInventoryRepository repository;

  @Mock
  private EquipmentRepository equipmentRepository;

  @Mock
  private FacilityService facilityService;

  @Mock
  private EquipmentService equipmentService;

  @InjectMocks
  private EquipmentInventoryService service;

  @Test
  public void shouldGetInventoryForFacility() throws Exception {
    List<EquipmentInventory> expectedEquipments = new ArrayList<>();
    expectedEquipments.add(new EquipmentInventory());
    when(repository.getFacilityInventory(1L, 1L)).thenReturn(expectedEquipments);

    List<EquipmentInventory> equipments = service.getInventoryForFacility(1L, 1L);
    verify(repository).getFacilityInventory(1L, 1L);

    assertEquals(equipments, expectedEquipments);
  }

  @Test
  public void shouldGetInventoryForUserFacility() throws Exception {
    // Set up variables
    long userId = 1L;
    long typeId = 0L;
    long programId = 1L;
    long equipmentTypeId = 1L;
    long facilityId = 1L;
    List<EquipmentInventory> expectedEquipments = new ArrayList<>();
    expectedEquipments.add(new EquipmentInventory());
    Facility facility = new Facility(facilityId);
    long[] facilityIds = {facilityId};
    Pagination page = new Pagination(1, 2);

    // Set up mock calls
    when(facilityService.getHomeFacility(userId)).thenReturn(facility);
    when(repository.getInventory(programId, equipmentTypeId, facilityIds, page)).thenReturn(expectedEquipments);

    // Do the call
    List<EquipmentInventory> equipments = service.getInventory(userId, typeId, programId, equipmentTypeId, page);

    // Test the results
    verify(facilityService).getHomeFacility(userId);
    verify(repository).getInventory(programId, equipmentTypeId, facilityIds, page);
    assertEquals(equipments, expectedEquipments);
  }

  @Test
  public void shouldGetInventoryForSupervisedFacilities() throws Exception {
    // Set up variables
    long userId = 1L;
    long typeId = 1L;
    long programId = 1L;
    long equipmentTypeId = 1L;
    long facilityId = 1L;
    List<EquipmentInventory> expectedEquipments = new ArrayList<>();
    expectedEquipments.add(new EquipmentInventory());
    Facility facility = new Facility(facilityId);
    List<Facility> facilities = new ArrayList<>();
    facilities.add(facility);
    long[] facilityIds = {facilityId};
    Pagination page = new Pagination(1, 2);

    // Set up mock calls
    when(facilityService.getUserSupervisedFacilities(userId, programId, MANAGE_EQUIPMENT_INVENTORY)).thenReturn(facilities);
    when(repository.getInventory(programId, equipmentTypeId, facilityIds, page)).thenReturn(expectedEquipments);

    // Do the call
    List<EquipmentInventory> equipments = service.getInventory(userId, typeId, programId, equipmentTypeId, page);

    // Test the results
    verify(facilityService).getUserSupervisedFacilities(userId, programId, MANAGE_EQUIPMENT_INVENTORY);
    verify(repository).getInventory(programId, equipmentTypeId, facilityIds, page);
    assertEquals(equipments, expectedEquipments);
  }

  @Test
  public void shouldGetInventoryCountForUserFacility() throws Exception {
    // Set up variables
    long userId = 1L;
    long typeId = 0L;
    long programId = 1L;
    long equipmentTypeId = 1L;
    long facilityId = 1L;
    Facility facility = new Facility(facilityId);
    long[] facilityIds = {facilityId};

    // Set up mock calls
    when(facilityService.getHomeFacility(userId)).thenReturn(facility);
    when(repository.getInventoryCount(programId, equipmentTypeId, facilityIds)).thenReturn(1);

    // Do the call
    int count = service.getInventoryCount(userId, typeId, programId, equipmentTypeId);

    // Test the results
    verify(facilityService).getHomeFacility(userId);
    verify(repository).getInventoryCount(programId, equipmentTypeId, facilityIds);
    assertEquals(count, 1);
  }

  @Test
  public void shouldGetInventoryCountForSupervisedFacilities() throws Exception {
    // Set up variables
    long userId = 1L;
    long typeId = 1L;
    long programId = 1L;
    long equipmentTypeId = 1L;
    long facilityId = 1L;
    Facility facility = new Facility(facilityId);
    List<Facility> facilities = new ArrayList<>();
    facilities.add(facility);
    long[] facilityIds = {facilityId};

    // Set up mock calls
    when(facilityService.getUserSupervisedFacilities(userId, programId, MANAGE_EQUIPMENT_INVENTORY)).thenReturn(facilities);
    when(repository.getInventoryCount(programId, equipmentTypeId, facilityIds)).thenReturn(1);

    // Do the call
    int count = service.getInventoryCount(userId, typeId, programId, equipmentTypeId);

    // Test the results
    verify(facilityService).getUserSupervisedFacilities(userId, programId, MANAGE_EQUIPMENT_INVENTORY);
    verify(repository).getInventoryCount(programId, equipmentTypeId, facilityIds);
    assertEquals(count, 1);
  }

  @Test
  public void shouldGetInventoryById() throws Exception {
    EquipmentInventory equipment = new EquipmentInventory();
    equipment.setSerialNumber("123");
    when(repository.getInventoryById(1L)).thenReturn(equipment);

    EquipmentInventory result = service.getInventoryById(1L);
    assertEquals(result.getSerialNumber(), equipment.getSerialNumber());
  }

  @Test
  public void shouldSaveNewCCEInventory() throws Exception {
    // Set up variables
    EquipmentType equipmentType = new EquipmentType();
    equipmentType.setColdChain(true);
    Equipment equipment = new Equipment();
    equipment.setEquipmentType(equipmentType);
    EquipmentInventory inventory = new EquipmentInventory();
    inventory.setSerialNumber("123");
    inventory.setEquipment(equipment);

    // Do the call
    service.save(inventory);

    // Test the results
    verify(repository).insert(inventory);
    verify(repository, never()).update(inventory);
  }

  @Test
  public void shouldSaveChangesInExistingCCEInventory() throws Exception {
    // Set up variables
    EquipmentType equipmentType = new EquipmentType();
    equipmentType.setColdChain(true);
    Equipment equipment = new Equipment();
    equipment.setEquipmentType(equipmentType);
    EquipmentInventory inventory = new EquipmentInventory();
    inventory.setId(1L);
    inventory.setSerialNumber("123");
    inventory.setEquipment(equipment);

    // Do the call
    service.save(inventory);

    // Test the results
    verify(repository, never()).insert(inventory);
    verify(repository).update(inventory);
  }

  @Test
  public void shouldSaveNewEquipmentInventory() throws Exception {
    // Set up variables
    EquipmentType equipmentType = new EquipmentType();
    equipmentType.setColdChain(false);
    equipmentType.setId(1L);
    Equipment equipment = new Equipment();
    equipment.setEquipmentType(equipmentType);
    equipment.setEquipmentTypeId(equipmentType.getId());
    equipment.setManufacturer("Manu");
    equipment.setModel("123");
    EquipmentInventory inventory = new EquipmentInventory();
    inventory.setSerialNumber("123");
    inventory.setEquipment(equipment);

    // Set up mock calls
    List<Equipment> equipmentsEmpty = new ArrayList<>();
    List<Equipment> equipmentsInserted = new ArrayList<>();
    equipmentsInserted.add(equipment);
    when(equipmentService.getAllByType(1L)).thenReturn(equipmentsEmpty, equipmentsInserted);

    // Do the call
    service.save(inventory);

    // Test the results
    verify(equipmentRepository).insert(equipment);
    verify(equipmentRepository, never()).update(equipment);
    verify(repository).insert(inventory);
    verify(repository, never()).update(inventory);
  }

  @Test
  public void shouldSaveChangesInExistingEquipmentInventory() throws Exception {
    // Set up variables
    EquipmentType equipmentType = new EquipmentType();
    equipmentType.setColdChain(false);
    equipmentType.setId(1L);
    Equipment equipment = new Equipment();
    equipment.setEquipmentType(equipmentType);
    equipment.setEquipmentTypeId(equipmentType.getId());
    equipment.setManufacturer("Manu");
    equipment.setModel("123");
    EquipmentInventory inventory = new EquipmentInventory();
    inventory.setId(1L);
    inventory.setSerialNumber("123");
    inventory.setEquipment(equipment);

    // Set up mock calls
    List<Equipment> equipments = new ArrayList<>();
    equipments.add(equipment);
    when(equipmentService.getAllByType(1L)).thenReturn(equipments);

    // Do the call
    service.save(inventory);

    // Test the results
    verify(equipmentRepository, never()).insert(equipment);
    verify(equipmentRepository).update(equipment);
    verify(repository, never()).insert(inventory);
    verify(repository).update(inventory);
  }
}