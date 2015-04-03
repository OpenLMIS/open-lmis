/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.vaccine.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.vaccine.domain.reports.LogisticsColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;
@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-vaccine.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class VaccineColumnTemplateMapperIT {

  @Autowired
  private VaccineColumnTemplateMapper mapper;


  @Test
  public void testGetAll() throws Exception {
    List<LogisticsColumn> columns =  mapper.getAllMasterColumns();
    assertEquals(15, columns.size());
  }

  @Test
  public void testGetForProgram() throws Exception {
    List<LogisticsColumn> program  = mapper.getForProgram(1L);
    assertEquals(0, program.size());
  }

  @Test
  public void testInsertProgramColumn() throws Exception {
    LogisticsColumn column = new LogisticsColumn();
    column.setDescription("Test Description");
    column.setIndicator("Indicator");
    column.setProgramId(1L);
    column.setLabel("The label");
    column.setMasterColumnId(1L);
    column.setDisplayOrder(1);
    column.setVisible(true);
    mapper.insertProgramColumn(column);

    List<LogisticsColumn> columns = mapper.getForProgram(1L);
    assertEquals( columns.size(), 1 );
    assertEquals( column.getLabel(), columns.get(0).getLabel());


  }

  @Test
  public void testUpdateProgramColumn() throws Exception {
    //TODO:
  }
}