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

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.domain.EquipmentInventory;
import org.openlmis.equipment.domain.EquipmentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;


@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-equipment.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class EquipmentInventoryMapperIT {

  @Autowired
  EquipmentInventoryMapper mapper;

  @Autowired
  EquipmentTypeMapper typeMapper;

  @Autowired
  EquipmentMapper equipmentMapper;

  @Autowired
  QueryExecutor queryExecutor;

  @Test
  public void shouldGetAllInventoryItemsForFacility() throws Exception{
    EquipmentInventory inventory = new EquipmentInventory();

    inventory.setProgramId(1L);
    inventory.setFacilityId(1L);
    inventory.setOperationalStatusId(1L);
    inventory.setEquipmentId(1L);
    inventory.setReplacementRecommended(false);
    inventory.setYearOfInstallation(2012);
    inventory.setSerialNumber("2323");
    inventory.setPurchasePrice(0F);
    inventory.setDateLastAssessed(DateTime.now().toDate());

//    mapper.insert(inventory);
//
//    List<EquipmentInventory> inventories =  mapper.getInventoryByFacilityAndProgram(1L, 1L);
//    assertEquals(inventories.size(), 1);
  }

  @Test
  public void shouldGetInventoryById() throws Exception {
    EquipmentInventory inventory = new EquipmentInventory();
    inventory.setProgramId(1L);
    inventory.setFacilityId(1L);
    inventory.setOperationalStatusId(1L);
    inventory.setEquipmentId(1L);
    inventory.setModel("123");
    inventory.setReplacementRecommended(false);
    inventory.setYearOfInstallation(2012);
    inventory.setSerialNumber("2323");
    inventory.setPurchasePrice(0F);
    inventory.setDateLastAssessed(DateTime.now().toDate());

//    mapper.insert(inventory);
//
//    EquipmentInventory result = mapper.getInventoryById(inventory.getId());
//
//    assertEquals(result.getModel(), inventory.getModel());
//    assertEquals(result.getOperationalStatusId(), inventory.getOperationalStatusId());
  }

  @Test
  public void shouldInsert()throws Exception {
    EquipmentType type = new EquipmentType();
    type.setCode("1");
    type.setCode("Type");
    typeMapper.insert(type);

    Equipment equipment = new Equipment();
    equipment.setCode("123");
    equipment.setName("Name");
    equipment.setEquipmentType(type);
    equipmentMapper.insert(equipment);

    EquipmentInventory inventory = new EquipmentInventory();
    inventory.setFacilityId(1L);
    inventory.setEquipmentId(equipment.getId());
    inventory.setOperationalStatusId(1L);
    inventory.setProgramId(1L);
    inventory.setModel("123");
    inventory.setReplacementRecommended(false);
    inventory.setYearOfInstallation(2012);
    inventory.setSerialNumber("2323");
    inventory.setPurchasePrice(0F);
    inventory.setDateLastAssessed(DateTime.now().toDate());

//    mapper.insert(inventory);
//
//    assertThat(inventory.getId(), CoreMatchers.is(notNullValue()));
//
//    ResultSet rs = queryExecutor.execute("Select * from facility_program_equipments where id = " + inventory.getId());
//    assertEquals(rs.next(), true);
//    assertEquals(rs.getString("model"), "123");
  }

  @Test
  public void shouldUpdate() throws Exception{
    EquipmentType type = new EquipmentType();
    type.setCode("1");
    type.setCode("Type");
    typeMapper.insert(type);

    Equipment equipment = new Equipment();
    equipment.setCode("123");
    equipment.setName("Name");
    equipment.setEquipmentType(type);

    equipmentMapper.insert(equipment);

    EquipmentInventory inventory = new EquipmentInventory();
    inventory.setFacilityId(1L);

    inventory.setEquipmentId(equipment.getId());
    inventory.setOperationalStatusId(1L);
    inventory.setProgramId(1L);
    inventory.setModel("123");
    inventory.setReplacementRecommended(false);
    inventory.setYearOfInstallation(2012);
    inventory.setSerialNumber("2323");
    inventory.setPurchasePrice(0F);
    inventory.setDateLastAssessed(DateTime.now().toDate());

//    mapper.insert(inventory);
//
//    inventory.setModel("3432");
//
//    mapper.update(inventory);
//
//    ResultSet rs = queryExecutor.execute("Select * from facility_program_equipments where id = " + inventory.getId());
//    assertEquals(rs.next(), true);
//    assertEquals(rs.getString("model"), "3432");
  }
}