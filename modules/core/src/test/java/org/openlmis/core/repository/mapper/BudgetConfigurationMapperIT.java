package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.EDIConfiguration;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class BudgetConfigurationMapperIT {


  @Autowired
  BudgetConfigurationMapper budgetConfigurationMapper;

  @Test
  public void shouldGetBudgetConfiguration() {
    EDIConfiguration budgetConfiguration = budgetConfigurationMapper.get();
    assertThat(budgetConfiguration, is(notNullValue()));
    assertThat(budgetConfiguration.isHeaderInFile(), is(false));
  }

  @Test
  public void shouldUpdateBudgetConfiguration() {
    EDIConfiguration budgetConfiguration = new EDIConfiguration(true);
    budgetConfigurationMapper.update(budgetConfiguration);
    EDIConfiguration savedBudgetConfiguration = budgetConfigurationMapper.get();
    assertThat(savedBudgetConfiguration.isHeaderInFile(), is(true));
  }
}
