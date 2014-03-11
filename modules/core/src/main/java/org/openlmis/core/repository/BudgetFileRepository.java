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

import org.openlmis.core.domain.BudgetFileInfo;
import org.openlmis.core.repository.mapper.BudgetFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * BudgetFileRepository is repository class for BudgetFile related database operations.
 */
@Repository
public class BudgetFileRepository {

  @Autowired
  BudgetFileMapper mapper;

  public void insert(BudgetFileInfo budgetFileInfo) {
    mapper.insert(budgetFileInfo);
  }

  public void update(BudgetFileInfo budgetFileInfo) {
    mapper.update(budgetFileInfo);
  }
}
