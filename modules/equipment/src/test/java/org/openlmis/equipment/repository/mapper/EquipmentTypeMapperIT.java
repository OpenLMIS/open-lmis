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

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.equipment.domain.EquipmentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-equipment.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class EquipmentTypeMapperIT {

  @Autowired
  EquipmentTypeMapper mapper;

  @Autowired
  QueryExecutor queryExecutor;


  @Test
  public void shouldGetEquipmentTypeById() throws Exception {
    EquipmentType type = new EquipmentType();
    type.setName("Test");
    type.setCode("Test");
    mapper.insert(type);

    EquipmentType result = mapper.getEquipmentTypeById(type.getId());
    assertEquals(result.getCode(), type.getCode());
    assertEquals(result.getName(), type.getName());
  }

  @Test
  public void shouldGetAll() throws Exception {
    EquipmentType type = new EquipmentType();
    type.setName("Test");
    type.setCode("Test");
    mapper.insert(type);

    ResultSet resultSet = queryExecutor.execute("SELECT * FROM equipment_types" );
    assertEquals(resultSet.next(), true);
  }

  @Test
  public void shouldInsert() throws Exception {
    EquipmentType type = new EquipmentType();
    type.setName("Test");
    type.setCode("Test");

    mapper.insert(type);

    assertThat(type.getId(), is(notNullValue()));

    ResultSet resultSet = queryExecutor.execute("SELECT * FROM equipment_types WHERE id = " + type.getId());
    assertTrue(resultSet.next());
    assertThat(resultSet.getString("name"), is("Test"));
  }

  @Test
  public void shouldUpdate() throws Exception {
    EquipmentType type = new EquipmentType();
    type.setName("Test-Update");
    type.setCode("Test-Update");

    mapper.insert(type);

    type.setCode("Test-2");
    type.setName("Test-2");

    mapper.update(type);

    ResultSet resultSet = queryExecutor.execute("SELECT * FROM equipment_types WHERE id = " + type.getId());
    assertTrue(resultSet.next());
    assertThat(resultSet.getString("name"), is("Test-2"));
  }
}