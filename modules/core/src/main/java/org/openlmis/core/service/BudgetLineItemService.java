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

import org.openlmis.core.domain.BudgetLineItem;
import org.openlmis.core.repository.BudgetLineItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Exposes the services for handling BudgetLineItem entity.
 */

@Service
public class BudgetLineItemService {

  @Autowired
  BudgetLineItemRepository repository;

  public void save(BudgetLineItem budgetLineItem) {
    repository.save(budgetLineItem);
  }

  public BudgetLineItem get(Long facilityId, Long programId, Long periodId) {
    return repository.get(facilityId, programId, periodId);
  }
}
