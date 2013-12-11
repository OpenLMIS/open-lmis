package org.openlmis.core.transformer;

import org.junit.Test;
import org.openlmis.core.domain.BudgetLineItem;
import org.openlmis.core.dto.BudgetLineItemDTO;
import org.openlmis.core.transformer.budget.BudgetLineItemTransformer;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class BudgetLineItemTransformerTest {

  @Test
  public void shouldTransformBudgetLineItemDTOIntoBudgetLineItem() throws Exception {
    BudgetLineItemDTO budgetLineItemDTO = new BudgetLineItemDTO("F10", "HIV", "10/12/2013", "345.45", "My good notes");
    String datePattern = "dd/MM/yy";
    SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
    Date date = dateFormat.parse("10/12/2013");
    BudgetLineItemTransformer budgetLineItemTransformer = new BudgetLineItemTransformer();

    BudgetLineItem budgetLineItem = budgetLineItemTransformer.transform(budgetLineItemDTO, datePattern);

    assertThat(budgetLineItem.getFacilityCode(), is("F10"));
    assertThat(budgetLineItem.getProgramCode(), is("HIV"));
    assertThat(budgetLineItem.getPeriodDate(), is(date));
    assertThat(budgetLineItem.getAllocatedBudget(), is(BigDecimal.valueOf(345.45)));
    assertThat(budgetLineItem.getNotes(), is("My good notes"));
  }

  @Test
  public void shouldTransformBudgetLineItemDTOWithoutParsingDateWhenPatternNotAvailable() throws Exception {
    BudgetLineItemDTO budgetLineItemDTO = new BudgetLineItemDTO("F10", "HIV", null, "345.45", "My good notes");
    String datePattern = null;

    BudgetLineItemTransformer budgetLineItemTransformer = new BudgetLineItemTransformer();

    BudgetLineItem budgetLineItem = budgetLineItemTransformer.transform(budgetLineItemDTO, datePattern);

    assertThat(budgetLineItem.getFacilityCode(), is("F10"));
    assertThat(budgetLineItem.getProgramCode(), is("HIV"));
    assertThat(budgetLineItem.getPeriodDate(), is(nullValue()));
    assertThat(budgetLineItem.getAllocatedBudget(), is(BigDecimal.valueOf(345.45)));
    assertThat(budgetLineItem.getNotes(), is("My good notes"));

  }
}
