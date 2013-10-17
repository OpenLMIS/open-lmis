package org.openlmis.core.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.EDIConfiguration;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.core.repository.mapper.BudgetConfigurationMapper;
import org.openlmis.core.repository.mapper.BudgetFileColumnMapper;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class BudgetFileTemplateRepositoryTest {

  @Mock
  BudgetFileColumnMapper budgetFileColumnMapper;

  @Mock
  BudgetConfigurationMapper budgetConfigurationMapper;

  @InjectMocks
  BudgetFileTemplateRepository repository;

  @Test
  public void shouldGetAllBudgetFileColumns() {
    List<EDIFileColumn> expectedBudgetFileColumns = new ArrayList<>();
    when(budgetFileColumnMapper.getAll()).thenReturn(expectedBudgetFileColumns);
    List<EDIFileColumn> budgetFileColumns = repository.getAllBudgetFileColumns();
    assertThat(budgetFileColumns, is(expectedBudgetFileColumns));
    verify(budgetFileColumnMapper).getAll();
  }

  @Test
  public void shouldGetBudgetConfiguration() {
    EDIConfiguration expectedBudgetConfiguration = new EDIConfiguration();
    when(budgetConfigurationMapper.get()).thenReturn(expectedBudgetConfiguration);

    EDIConfiguration budgetConfiguration = repository.getBudgetConfiguration();

    assertThat(budgetConfiguration, is(expectedBudgetConfiguration));
    verify(budgetConfigurationMapper).get();
  }

  @Test
  public void shouldUpdateBudgetConfiguration() {
    EDIConfiguration budgetConfiguration = new EDIConfiguration();

    repository.updateBudgetConfiguration(budgetConfiguration);

    verify(budgetConfigurationMapper).update(budgetConfiguration);
  }


  @Test
  public void shouldInsertBudgetFileColumn() {
    EDIFileColumn budgetFileColumn = new EDIFileColumn();

    repository.update(budgetFileColumn);

    verify(budgetFileColumnMapper).update(budgetFileColumn);
  }
}

