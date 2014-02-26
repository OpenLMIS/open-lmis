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
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.BudgetFileInfo;
import org.openlmis.core.repository.BudgetFileRepository;

import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class BudgetFileServiceTest {

  @Mock
  BudgetFileRepository repository;

  @InjectMocks
  BudgetFileService service;

  @Test
  public void shouldInsertBudgetFileInfoIfDoesNotExist() throws Exception {
    BudgetFileInfo budgetFileInfo = new BudgetFileInfo("Budget File", false);
    service.save(budgetFileInfo);

    verify(repository).insert(budgetFileInfo);
  }

  @Test
  public void shouldUpdateBudgetFileInfoIfExist() throws Exception {
    BudgetFileInfo budgetFileInfo = new BudgetFileInfo("Budget File", false);
    budgetFileInfo.setId(1L);
    service.save(budgetFileInfo);

    verify(repository).update(budgetFileInfo);
  }
}
