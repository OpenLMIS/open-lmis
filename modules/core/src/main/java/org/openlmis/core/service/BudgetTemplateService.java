package org.openlmis.core.service;

import org.openlmis.core.domain.EDIConfiguration;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.core.domain.EDIFileTemplate;
import org.openlmis.core.repository.BudgetTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BudgetTemplateService {
  @Autowired
  BudgetTemplateRepository budgetTemplateRepository;

  @Transactional
  public void update(EDIFileTemplate<EDIFileColumn> ediFileTemplate) {
    budgetTemplateRepository.updateBudgetConfiguration(ediFileTemplate.getConfiguration());

    for (EDIFileColumn ediFileColumn : ediFileTemplate.getColumns()) {
      budgetTemplateRepository.update(ediFileColumn);
    }
  }

  public EDIFileTemplate get() {
    EDIConfiguration config = budgetTemplateRepository.getBudgetConfiguration();
    List<EDIFileColumn> columns = budgetTemplateRepository.getAllBudgetFileColumns();

    return new EDIFileTemplate(config, columns);
  }
}
