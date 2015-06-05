/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.equipment.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.Program;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.equipment.builder.EquipmentBuilder;
import org.openlmis.equipment.builder.EquipmentInventoryBuilder;
import org.openlmis.equipment.builder.EquipmentTypeBuilder;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.domain.EquipmentInventory;
import org.openlmis.equipment.domain.EquipmentInventoryStatus;
import org.openlmis.equipment.domain.EquipmentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static junit.framework.Assert.assertEquals;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-equipment.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class EquipmentInventoryStatusMapperIT {

  @Autowired
  EquipmentInventoryStatusMapper mapper;

  @Autowired
  EquipmentInventoryMapper inventoryMapper;

  @Autowired
  ProgramMapper programMapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Autowired
  EquipmentTypeMapper equipmentTypeMapper;

  @Autowired
  EquipmentMapper equipmentMapper;

  EquipmentInventory inventory;
  EquipmentInventoryStatus status;

  @Before
  public void initialize() throws Exception {
    long statusId = 1L;
    long notFunctionalStatusId = 2L;
    Date effectiveDateTime = new Date();

    Program program = make(a(ProgramBuilder.defaultProgram));
    programMapper.insert(program);

    Facility facility = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(facility);

    EquipmentType equipmentType = make(a(EquipmentTypeBuilder.defaultEquipmentType));
    equipmentTypeMapper.insert(equipmentType);

    Equipment equipment = make(a(EquipmentBuilder.defaultEquipment));
    equipment.setEquipmentType(equipmentType);
    equipment.setEquipmentTypeId(equipmentType.getId());
    equipmentMapper.insert(equipment);

    inventory = make(a(EquipmentInventoryBuilder.defaultEquipmentInventory));
    inventory.setProgramId(program.getId());
    inventory.setFacility(facility);
    inventory.setFacilityId(facility.getId());
    inventory.setEquipment(equipment);
    inventory.setEquipmentId(equipment.getId());
    inventoryMapper.insert(inventory);

    status = new EquipmentInventoryStatus();
    status.setInventoryId(inventory.getId());
    status.setStatusId(statusId);
    status.setNotFunctionalStatusId(notFunctionalStatusId);
    status.setEffectiveDateTime(effectiveDateTime);
  }

  @Test
  public void shouldInsertAndGetCurrentStatus() throws Exception {
    mapper.insert(status);
    EquipmentInventoryStatus result = mapper.getCurrentStatus(inventory.getId());
    assertEquals(result, status);
  }

  @Test
  public void shouldUpdateAndGetCurrentStatus() throws Exception {
    long newStatusId = 3L;
    mapper.insert(status);
    status.setStatusId(newStatusId);
    mapper.update(status);
    EquipmentInventoryStatus result = mapper.getCurrentStatus(inventory.getId());
    assertEquals(result, status);
  }
}
