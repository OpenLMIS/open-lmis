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