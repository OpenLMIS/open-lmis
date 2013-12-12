package org.openlmis.core.repository;

import org.openlmis.core.domain.BudgetFileInfo;
import org.openlmis.core.repository.mapper.BudgetFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
