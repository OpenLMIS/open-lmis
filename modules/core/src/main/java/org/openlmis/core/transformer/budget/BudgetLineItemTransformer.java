package org.openlmis.core.transformer.budget;

import org.openlmis.core.domain.BudgetLineItem;
import org.openlmis.core.dto.BudgetLineItemDTO;
import org.openlmis.core.exception.DataException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class BudgetLineItemTransformer {

  public BudgetLineItem transform(BudgetLineItemDTO lineItemDTO, String datePattern) {
    BudgetLineItem budgetLineItem = new BudgetLineItem();
    Date periodDate = null;
    if (datePattern != null) {
      SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
      try {
        periodDate = dateFormat.parse(lineItemDTO.getPeriodStartDate());
      } catch (ParseException e) {
        throw new DataException("incorrect.date.format");
      }
    }

    budgetLineItem.setFacilityCode(lineItemDTO.getFacilityCode());
    budgetLineItem.setProgramCode(lineItemDTO.getProgramCode());
    budgetLineItem.setPeriodDate(periodDate);
    budgetLineItem.setAllocatedBudget(BigDecimal.valueOf(Double.parseDouble(lineItemDTO.getAllocatedBudget())));
    budgetLineItem.setNotes(lineItemDTO.getNotes());

    return budgetLineItem;
  }
}
