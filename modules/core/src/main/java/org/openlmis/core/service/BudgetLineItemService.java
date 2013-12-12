package org.openlmis.core.service;

import org.openlmis.core.domain.BudgetLineItem;
import org.openlmis.core.repository.BudgetLineItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetLineItemService {

  @Autowired
  BudgetLineItemRepository repository;

  public void save(BudgetLineItem budgetLineItem) {
    repository.save(budgetLineItem);
  }
}
