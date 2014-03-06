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

import org.openlmis.core.domain.BudgetLineItem;
import org.openlmis.core.repository.mapper.BudgetLineItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

/**
 * BudgetLineItemRepository is Repository class for BudgetLineItem related database operations.
 */

@Repository
public class BudgetLineItemRepository {

  @Autowired
  BudgetLineItemMapper mapper;

  public void save(BudgetLineItem budgetLineItem) {
    try {
      mapper.insert(budgetLineItem);
    } catch (DuplicateKeyException e) {
      mapper.update(budgetLineItem);
    }
  }

  public BudgetLineItem get(Long facilityId, Long programId, Long periodId) {
    return mapper.get(facilityId, programId, periodId);
  }
}
