/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
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

