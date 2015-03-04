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
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.equipment.builder.MaintenanceLogBuilder;
import org.openlmis.equipment.domain.MaintenanceLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;

import static org.junit.Assert.*;
@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-equipment.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class MaintenanceLogMapperIT {

  @Autowired
  private MaintenanceLogMapper mapper;

  @Before
  public void setup(){

  }

  @Test
  public void testGetById() throws Exception {
    MaintenanceLog log = make(a(MaintenanceLogBuilder.defaultMaintenanceLog));
    mapper.insert(log);

    MaintenanceLog log2 = mapper.getById(log.getId());
    assertEquals(log.getRecommendation(), log2.getRecommendation());
  }

  @Test
  public void testGetAll() throws Exception {
    List<MaintenanceLog> logs = mapper.getAll();
    assertEquals(0, logs.size());
    MaintenanceLog log = make(a(MaintenanceLogBuilder.defaultMaintenanceLog));
    mapper.insert(log);

    logs = mapper.getAll();
    assertEquals(1, logs.size());
  }

  @Test
  public void testGetAllForFacility() throws Exception {

  }

  @Test
  public void testGetAllForVendor() throws Exception {

  }

  @Test
  public void testInsert() throws Exception {

  }

  @Test
  public void testUpdate() throws Exception {

  }
}