/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.BudgetFileInfo;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class BudgetFileMapperIT {

  @Autowired
  BudgetFileMapper budgetFileMapper;

  @Autowired
  QueryExecutor queryExecutor;


  @Test
  public void shouldInsertBudgetFileInfo() throws SQLException {
    BudgetFileInfo budgetFileInfo = new BudgetFileInfo("BudgetFile", false);

    budgetFileMapper.insert(budgetFileInfo);

    assertThat(budgetFileInfo.getId(), is(notNullValue()));

    ResultSet resultSet = queryExecutor.execute("SELECT fileName FROM budget_file_info WHERE id = " + budgetFileInfo.getId());
    assertTrue(resultSet.next());
    assertThat(resultSet.getString("fileName"), is("BudgetFile"));
  }

  @Test
  public void shouldUpdateBudgetFileInfo() throws SQLException {
    BudgetFileInfo budgetFileInfo = new BudgetFileInfo("BudgetFile", false);

    budgetFileMapper.insert(budgetFileInfo);

    budgetFileInfo.setProcessingError(true);

    budgetFileMapper.update(budgetFileInfo);


    ResultSet resultSet = queryExecutor.execute("SELECT processingError FROM budget_file_info WHERE id = " + budgetFileInfo.getId());
    assertTrue(resultSet.next());
    assertThat(resultSet.getBoolean("processingError"), is(true));


  }
}
