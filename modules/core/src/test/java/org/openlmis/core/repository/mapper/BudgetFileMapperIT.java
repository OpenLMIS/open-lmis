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
  public void shouldSaveBudgetFileInfo() throws Exception {
    BudgetFileInfo budgetFileInfo = new BudgetFileInfo("BudgetFile", false);

    budgetFileMapper.insert(budgetFileInfo);

    assertThat(budgetFileInfo.getId(), is(notNullValue()));

    ResultSet resultSet = queryExecutor.execute("SELECT fileName FROM budget_file_info WHERE id = " + budgetFileInfo.getId());
    assertTrue(resultSet.next());
    assertThat(resultSet.getString("fileName"), is("BudgetFile"));
  }
}
