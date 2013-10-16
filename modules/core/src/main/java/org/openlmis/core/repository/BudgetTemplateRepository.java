package org.openlmis.core.repository;

import org.openlmis.core.domain.EDIConfiguration;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.core.repository.mapper.BudgetConfigurationMapper;
import org.openlmis.core.repository.mapper.BudgetFileColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BudgetTemplateRepository {

  @Autowired
  private BudgetFileColumnMapper budgetFileColumnMapper;

  @Autowired
  BudgetConfigurationMapper budgetConfigurationMapper;

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

