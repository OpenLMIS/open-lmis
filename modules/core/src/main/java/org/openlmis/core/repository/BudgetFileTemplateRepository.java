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

import org.openlmis.core.domain.EDIConfiguration;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.core.repository.mapper.BudgetConfigurationMapper;
import org.openlmis.core.repository.mapper.BudgetFileColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * BudgetFileTemplateRepository is Repository class for BudgetFileTemplate (BudgetFileColumn and BudgetConfiguration)
 * related database operations.
 */

@Repository
public class BudgetFileTemplateRepository {

  @Autowired
  private BudgetFileColumnMapper budgetFileColumnMapper;

  @Autowired
  private BudgetConfigurationMapper budgetConfigurationMapper;

  public List<EDIFileColumn> getAllBudgetFileColumns() {
    return budgetFileColumnMapper.getAll();
  }

  public EDIConfiguration getBudgetConfiguration() {
    return budgetConfigurationMapper.get();
  }

  public void updateBudgetConfiguration(EDIConfiguration shipmentConfiguration) {
    budgetConfigurationMapper.update(shipmentConfiguration);
  }

  public void update(EDIFileColumn ediFileColumn) {
    budgetFileColumnMapper.update(ediFileColumn);
  }
}

