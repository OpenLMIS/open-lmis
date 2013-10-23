package org.openlmis.core.service;

import org.openlmis.core.domain.EDIConfiguration;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.core.domain.EDIFileTemplate;
import org.openlmis.core.repository.BudgetFileTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BudgetFileTemplateService {
  @Autowired
  BudgetFileTemplateRepository budgetFileTemplateRepository;

  @Transactional
  public void update(EDIFileTemplate ediFileTemplate) {
    budgetFileTemplateRepository.updateBudgetConfiguration(ediFileTemplate.getConfiguration());

    for (EDIFileColumn ediFileColumn : ediFileTemplate.getColumns()) {
      budgetFileTemplateRepository.update(ediFileColumn);
    }
  }

  public EDIFileTemplate get() {
    EDIConfiguration config = budgetFileTemplateRepository.getBudgetConfiguration();
    List<EDIFileColumn> columns = budgetFileTemplateRepository.getAllBudgetFileColumns();

    return new EDIFileTemplate(config, columns);
  }
}
