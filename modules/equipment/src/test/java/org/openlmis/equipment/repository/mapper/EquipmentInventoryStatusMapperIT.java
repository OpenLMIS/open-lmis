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

package org.openlmis.equipment.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.builder.ProgramBuilder;
import org.openlmis.core.domain.Facility;
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
