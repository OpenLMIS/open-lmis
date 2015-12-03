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

/**
 * BudgetLineItemTransformer transforms BudgetLineItemDTO into BudgetLineItem.
 */
@Service
public class BudgetLineItemTransformer extends LineItemTransformer {

  public BudgetLineItem transform(BudgetLineItemDTO lineItemDTO, String dateFormat, Integer rowNumber) {

    BudgetLineItem budgetLineItem = new BudgetLineItem();
    Date periodDate = getValidatedPeriodDate(lineItemDTO.getPeriodStartDate(), dateFormat, rowNumber);

    BigDecimal allocatedBudget = getAllocatedBudget(rowNumber, lineItemDTO.getAllocatedBudget());

    budgetLineItem.setPeriodDate(periodDate);
    budgetLineItem.setAllocatedBudget(allocatedBudget);
    budgetLineItem.setNotes(lineItemDTO.getNotes());

    return budgetLineItem;
  }

  private BigDecimal getAllocatedBudget(Integer rowNumber, String allocatedBudget) {
    try {
      BigDecimal budget = new BigDecimal(allocatedBudget);
      if (budget.compareTo(new BigDecimal(0)) == -1) {
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

  private Date getValidatedPeriodDate(String periodStartDate, String dateFormat, Integer rowNumber) {
    Date periodDate = null;
    if (dateFormat != null) {
      try {
        periodDate = parseDate(dateFormat, periodStartDate);
      } catch (Exception e) {
        throw new DataException("budget.invalid.date.format", periodStartDate, rowNumber);
      }
    }
    return periodDate;
  }
}
