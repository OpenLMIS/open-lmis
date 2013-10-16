package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class BudgetFileColumnMapperIT {

  @Autowired
  BudgetFileColumnMapper budgetFileColumnMapper;

  @Test
  public void shouldGetAllBudgetFileColumns() {
    List<EDIFileColumn> budgetFileColumns = budgetFileColumnMapper.getAll();
    assertThat(budgetFileColumns.size(), is(5));
  }


  @Test
  public void shouldUpdateBudgetFileColumn() {
    List<EDIFileColumn> budgetFileColumns = budgetFileColumnMapper.getAll();
    EDIFileColumn budgetFileColumn = budgetFileColumns.get(0);
    budgetFileColumn.setName("Updated ");
  }

}
