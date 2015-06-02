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

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.equipment.domain.Equipment;
import org.openlmis.equipment.domain.EquipmentEnergyType;
import org.openlmis.equipment.domain.EquipmentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-equipment.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class EquipmentMapperIT {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Autowired
  EquipmentTypeMapper typeMapper;

  @Autowired
  EquipmentEnergyTypeMapper energyTypeMapper;

  @Autowired
  EquipmentMapper mapper;

  @Autowired
  QueryExecutor queryExecutor;

  @Test
  public void shouldGetAll() throws Exception {
    EquipmentType type = new EquipmentType();
    type.setCode("1");
    type.setName("Type");
    type.setColdChain(false);
    typeMapper.insert(type);

    EquipmentEnergyType energyType=new EquipmentEnergyType();
    energyType.setName("TestEnergy");
    energyTypeMapper.insert(energyType);

    Equipment equipment = new Equipment();
    equipment.setCode("123");
    equipment.setName("Name");
    equipment.setEquipmentType(type);
    equipment.setManufacturer("Manufacturer");
    equipment.setModel("Model");
    equipment.setEnergyTypeId(energyType.getId());
    mapper.insert(equipment);

    List<Equipment> results =  mapper.getAll();
    MatcherAssert.assertThat(results.size(), greaterThan(0));
  }

  @Test
  public void shouldGetById() throws Exception {
    EquipmentType type = new EquipmentType();
    type.setCode("1");
    type.setName("Type");
    typeMapper.insert(type);

    EquipmentEnergyType energyType=new EquipmentEnergyType();
    energyType.setName("TestEnergy");
    energyTypeMapper.insert(energyType);

    Equipment equipment = new Equipment();
    equipment.setCode("123");
    equipment.setName("Name");
    equipment.setEquipmentType(type);
    equipment.setManufacturer("Manufacturer");
    equipment.setModel("Model");
    equipment.setEnergyTypeId(energyType.getId());
    mapper.insert(equipment);

    Equipment result = mapper.getById(equipment.getId());
    assertEquals(result.getCode(), equipment.getCode());
    assertEquals(result.getName(), equipment.getName());
    assertEquals(result.getModel(), equipment.getModel());
    assertEquals(result.getEnergyTypeId(), equipment.getEnergyTypeId());
  }

  @Test
  public void shouldInsert() throws Exception {
    EquipmentType type = new EquipmentType();
    type.setCode("1");
    type.setName("Type");
    typeMapper.insert(type);

    EquipmentEnergyType energyType=new EquipmentEnergyType();
    energyType.setName("TestEnergy");
    energyTypeMapper.insert(energyType);

    Equipment equipment = new Equipment();
    equipment.setCode("123");
    equipment.setName("Name");
    equipment.setEquipmentType(type);
    equipment.setManufacturer("Manufacturer");
    equipment.setModel("Model");
    equipment.setEnergyTypeId(energyType.getId());
    mapper.insert(equipment);
    assertThat(equipment.getId(), CoreMatchers.is(notNullValue()));

    ResultSet rs = queryExecutor.execute("Select * from equipments where id = " + equipment.getId());
    assertEquals(rs.next(), true);
    assertEquals(rs.getString("code"), "123");
    assertEquals(rs.getString("name"),"Name");
    assertEquals(rs.getString("model"),"Model");
    assertEquals(rs.getString("manufacturer"),"Manufacturer");

  }

  @Test
  public void shouldUpdate() throws Exception {
    EquipmentType type = new EquipmentType();
    type.setCode("1");
    type.setName("Type");
    typeMapper.insert(type);

    EquipmentEnergyType energyType=new EquipmentEnergyType();
    energyType.setName("TestEnergy");
    energyTypeMapper.insert(energyType);

    Equipment equipment = new Equipment();
    equipment.setCode("123");
    equipment.setName("Name");
    equipment.setEquipmentType(type);
    equipment.setManufacturer("Manufacturer");
    equipment.setModel("Model");
    equipment.setEnergyTypeId(energyType.getId());
    mapper.insert(equipment);

    equipment.setName("Updated Name");
    mapper.update(equipment);

    Equipment result = mapper.getById(equipment.getId());
    assertEquals(result.getName(),equipment.getName());
  }

  @Test
  public void shouldThrowExceptionIfDuplicateCodeIsInserted() throws Exception{
    EquipmentType type = new EquipmentType();
    type.setCode("1");
    type.setCode("Type");

    typeMapper.insert(type);

    EquipmentEnergyType energyType=new EquipmentEnergyType();
    energyType.setName("TestEnergy");
    energyTypeMapper.insert(energyType);

    Equipment equipment = new Equipment();
    equipment.setCode("123");
    equipment.setName("Name");
    equipment.setEquipmentType(type);
    equipment.setManufacturer("Manufacturer");
    equipment.setModel("Model");
    equipment.setEnergyTypeId(energyType.getId());
    mapper.insert(equipment);


    expectedEx.expect(Exception.class);
    mapper.insert(equipment);
  }

  @Test
  public void shouldThrowExceptionIfDuplicateCodeHappensBecauseOfAnUpdate() throws Exception{
    EquipmentType type = new EquipmentType();
    type.setCode("1");
    type.setCode("Type");

    typeMapper.insert(type);

    EquipmentEnergyType energyType=new EquipmentEnergyType();
    energyType.setName("TestEnergy");
    energyTypeMapper.insert(energyType);

    Equipment equipment = new Equipment();
    equipment.setCode("123");
    equipment.setName("Name");
    equipment.setEquipmentType(type);
    equipment.setManufacturer("Manufacturer");
    equipment.setModel("Model");
    equipment.setEnergyTypeId(energyType.getId());
    mapper.insert(equipment);

    equipment.setCode("1234");
    mapper.insert(equipment);

    expectedEx.expect(Exception.class);

    equipment.setCode("123");
    mapper.update(equipment);
  }
}