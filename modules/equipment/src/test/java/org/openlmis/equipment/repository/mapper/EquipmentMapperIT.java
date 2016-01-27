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

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Pagination;
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
  public void shouldGetByType() throws Exception {

    Pagination page=new Pagination(1,1);

    EquipmentType type = new EquipmentType();
    type.setCode("1");
    type.setName("Type");
    type.setColdChain(false);
    typeMapper.insert(type);

    EquipmentEnergyType energyType=new EquipmentEnergyType();
    energyType.setName("TestEnergy");
    energyTypeMapper.insert(energyType);

    Equipment equipment = new Equipment();
    equipment.setName("Name");
    equipment.setEquipmentType(type);
    equipment.setManufacturer("Manufacturer");
    equipment.setModel("Model");
    equipment.setEnergyTypeId(energyType.getId());
    mapper.insert(equipment);

    List<Equipment> results =  mapper.getByType(type.getId(), page);
    MatcherAssert.assertThat(results.size(), greaterThan(0));
  }

  @Test
  public void shouldGetCountByType() throws Exception {

    EquipmentType type = new EquipmentType();
    type.setCode("1");
    type.setName("Type");
    type.setColdChain(false);
    typeMapper.insert(type);

    EquipmentEnergyType energyType=new EquipmentEnergyType();
    energyType.setName("TestEnergy");
    energyTypeMapper.insert(energyType);

    Equipment equipment = new Equipment();
    equipment.setName("Name");
    equipment.setEquipmentType(type);
    equipment.setManufacturer("Manufacturer");
    equipment.setModel("Model");
    equipment.setEnergyTypeId(energyType.getId());
    mapper.insert(equipment);

    Integer count =  mapper.getCountByType(type.getId());
    assertEquals(count,Integer.valueOf(1));
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
    equipment.setName("Name");
    equipment.setEquipmentType(type);
    equipment.setManufacturer("Manufacturer");
    equipment.setModel("Model");
    equipment.setEnergyTypeId(energyType.getId());
    mapper.insert(equipment);

    Equipment result = mapper.getById(equipment.getId());
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
    equipment.setName("Name");
    equipment.setEquipmentType(type);
    equipment.setManufacturer("Manufacturer");
    equipment.setModel("Model");
    equipment.setEnergyTypeId(energyType.getId());
    mapper.insert(equipment);
    assertThat(equipment.getId(), CoreMatchers.is(notNullValue()));

    ResultSet rs = queryExecutor.execute("Select * from equipments where id = " + equipment.getId());
    assertEquals(rs.next(), true);
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

// Tests not necessary because name is not unique in db (should it be unique?)
//  @Test
//  public void shouldThrowExceptionIfDuplicateNameIsInserted() throws Exception{
//    EquipmentType type = new EquipmentType();
//    type.setCode("1");
//    type.setName("Type");
//
//    typeMapper.insert(type);
//
//    EquipmentEnergyType energyType=new EquipmentEnergyType();
//    energyType.setName("TestEnergy");
//    energyTypeMapper.insert(energyType);
//
//    Equipment equipment = new Equipment();
//    equipment.setName("Name");
//    equipment.setEquipmentType(type);
//    equipment.setManufacturer("Manufacturer");
//    equipment.setModel("Model");
//    equipment.setEnergyTypeId(energyType.getId());
//    mapper.insert(equipment);
//
//    mapper.insert(equipment);
//    expectedEx.expect(Exception.class);
//  }
//
//  @Test
//  public void shouldThrowExceptionIfDuplicateNameHappensBecauseOfAnUpdate() throws Exception{
//    EquipmentType type = new EquipmentType();
//    type.setCode("1");
//    type.setName("Type");
//
//    typeMapper.insert(type);
//
//    EquipmentEnergyType energyType=new EquipmentEnergyType();
//    energyType.setName("TestEnergy");
//    energyTypeMapper.insert(energyType);
//
//    Equipment equipment = new Equipment();
//    equipment.setName("Name");
//    equipment.setEquipmentType(type);
//    equipment.setManufacturer("Manufacturer");
//    equipment.setModel("Model");
//    equipment.setEnergyTypeId(energyType.getId());
//    mapper.insert(equipment);
//
//    equipment.setName("Name2");
//    mapper.insert(equipment);
//
//    expectedEx.expect(Exception.class);
//
//    equipment.setName("Name2");
//    mapper.update(equipment);
//  }
}