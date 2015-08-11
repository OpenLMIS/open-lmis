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