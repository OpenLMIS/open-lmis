/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.core.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.EDIConfiguration;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.core.domain.EDIFileTemplate;
import org.openlmis.core.repository.BudgetFileTemplateRepository;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(BudgetFileTemplateService.class)
@Category(UnitTests.class)
public class BudgetFileTemplateServiceTest {

  @Mock
  BudgetFileTemplateRepository budgetFileTemplateRepository;

  @InjectMocks
  BudgetFileTemplateService budgetFileTemplateService;

  @Test
  public void shouldUpdateBudgetFileTemplate() {

    EDIConfiguration ediConf = new EDIConfiguration();
    EDIFileColumn ediFileColumn1 = new EDIFileColumn("column1", "Column 1", true, true, 1, null);
    EDIFileColumn ediFileColumn2 = new EDIFileColumn("column2", "Column 2", true, true, 2, null);
    List<EDIFileColumn> columns = asList(ediFileColumn1, ediFileColumn2);
    EDIFileTemplate ediFileTemplate = new EDIFileTemplate(ediConf, columns);

    budgetFileTemplateService.update(ediFileTemplate);

    verify(budgetFileTemplateRepository).updateBudgetConfiguration(ediConf);
    verify(budgetFileTemplateRepository).update(ediFileColumn1);
    verify(budgetFileTemplateRepository).update(ediFileColumn2);
  }

  @Test
  public void shouldGetBudgetFileTemplate() throws Exception {

    List<EDIFileColumn> budgetFileColumns = new ArrayList<>();
    EDIConfiguration budgetConf = new EDIConfiguration();
    EDIFileTemplate ediFileTemplate = new EDIFileTemplate();
    when(budgetFileTemplateRepository.getAllBudgetFileColumns()).thenReturn(budgetFileColumns);
    when(budgetFileTemplateRepository.getBudgetConfiguration()).thenReturn(budgetConf);
    whenNew(EDIFileTemplate.class).withArguments(budgetConf, budgetFileColumns).thenReturn(ediFileTemplate);

    EDIFileTemplate returnedEdiFileTemplate = budgetFileTemplateService.get();

    assertThat(returnedEdiFileTemplate, is(ediFileTemplate));
  }

}
