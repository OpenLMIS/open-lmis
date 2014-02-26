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
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.BudgetLineItem;
import org.openlmis.core.repository.mapper.BudgetLineItemMapper;
import org.springframework.dao.DuplicateKeyException;

import java.math.BigDecimal;
import java.util.Date;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BudgetLineItemRepositoryTest {

  @Mock
  BudgetLineItemMapper mapper;

  @InjectMocks
  BudgetLineItemRepository repository;

  @Test
  public void shouldInsertBudgetLineItemIfDoesNotExistsAlready() throws Exception {

    BudgetLineItem budgetLineItem = new BudgetLineItem(1L, 2L, 1L, 1L, new Date(), BigDecimal.valueOf(345.45), "My good notes");

    repository.save(budgetLineItem);

    verify(mapper).insert(budgetLineItem);
  }

  @Test
  public void shouldUpdateBudgetLineItemIfAlreadyExists() throws Exception {
    Long periodId = 1l;
    BudgetLineItem budgetLineItem = new BudgetLineItem(1L, 2L, periodId, 1L, new Date(), BigDecimal.valueOf(345.45), "My good notes");

    doThrow(DuplicateKeyException.class).when(mapper).insert(budgetLineItem);

    repository.save(budgetLineItem);

    verify(mapper, never()).get(1L, 2L, periodId);
    verify(mapper).insert(budgetLineItem);
    verify(mapper).update(budgetLineItem);
  }
}
