package org.openlmis.core.transformer.budget;

import org.openlmis.core.domain.BudgetLineItem;
import org.openlmis.core.dto.BudgetLineItemDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.transformer.LineItemTransformer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import static java.math.RoundingMode.FLOOR;

@Service
public class BudgetLineItemTransformer extends LineItemTransformer {

  public BudgetLineItem transform(BudgetLineItemDTO lineItemDTO, String datePattern, Integer rowNumber) {
    BudgetLineItem budgetLineItem = new BudgetLineItem();

    Date periodDate = getValidatedPeriodDate(lineItemDTO.getPeriodStartDate(), datePattern, rowNumber);

    BigDecimal allocatedBudget = getAllocatedBudget(rowNumber, lineItemDTO.getAllocatedBudget());

    budgetLineItem.setPeriodDate(periodDate);
    budgetLineItem.setAllocatedBudget(allocatedBudget);
    budgetLineItem.setNotes(lineItemDTO.getNotes());

    return budgetLineItem;
  }

  private BigDecimal getAllocatedBudget(Integer rowNumber, String allocatedBudget) {
    try {
      Double budget = Double.valueOf(allocatedBudget);
      if (budget < 0) {
        throw new DataException("budget.allocated.negative");
      }
      DecimalFormat decimalFormat = new DecimalFormat("#0.##");
      decimalFormat.setRoundingMode(FLOOR);
      allocatedBudget = decimalFormat.format(budget);
    } catch (Exception e) {
      throw new DataException("budget.allocated.invalid", allocatedBudget, rowNumber);
    }
    return new BigDecimal(allocatedBudget);
  }

  private Date getValidatedPeriodDate(String periodStartDate, String datePattern, Integer rowNumber) {
    Date periodDate = null;
    if (datePattern != null) {
      try {
        periodDate = parseDate(datePattern, periodStartDate);
      } catch (Exception e) {
        throw new DataException("budget.invalid.date.format", periodStartDate, rowNumber);
      }
    }
    return periodDate;
  }
}
