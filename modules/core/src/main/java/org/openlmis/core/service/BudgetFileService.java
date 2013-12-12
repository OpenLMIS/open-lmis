package org.openlmis.core.service;

import org.openlmis.core.domain.BudgetFileInfo;
import org.openlmis.core.repository.BudgetFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetFileService {

  @Autowired
  BudgetFileRepository repository;

  public void save(BudgetFileInfo budgetFileInfo) {
    repository.save(budgetFileInfo);
  }

}
