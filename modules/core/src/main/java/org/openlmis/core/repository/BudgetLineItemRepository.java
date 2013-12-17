package org.openlmis.core.repository;

import org.openlmis.core.domain.BudgetLineItem;
import org.openlmis.core.repository.mapper.BudgetLineItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BudgetLineItemRepository {

  @Autowired
  BudgetLineItemMapper mapper;


  public void save(BudgetLineItem budgetLineItem) {
    BudgetLineItem savedBudgetLineItem = mapper.getBy(budgetLineItem);
    if (savedBudgetLineItem != null) {
      budgetLineItem.setId(savedBudgetLineItem.getId());
      mapper.update(budgetLineItem);
    } else {
      mapper.insert(budgetLineItem);
    }
  }
}
