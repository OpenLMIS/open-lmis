package org.openlmis.core.transformer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.BudgetLineItem;
import org.openlmis.core.dto.BudgetLineItemDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.transformer.budget.BudgetLineItemTransformer;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.String.format;
import static java.math.BigDecimal.valueOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BudgetLineItemTransformerTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @InjectMocks
  private BudgetLineItemTransformer budgetLineItemTransformer;


  @Test
  public void shouldTransformBudgetLineItemDTOIntoBudgetLineItem() throws Exception {
    BudgetLineItemDTO budgetLineItemDTO = new BudgetLineItemDTO("F10", "HIV", "10/12/2013", "345.45", "My good notes");
    String datePattern = "dd/MM/yyyy";
    SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
    Date date = dateFormat.parse("10/12/2013");

    BudgetLineItem budgetLineItem = budgetLineItemTransformer.transform(budgetLineItemDTO, datePattern, 1);

    assertThat(budgetLineItem.getFacilityCode(), is("F10"));
    assertThat(budgetLineItem.getProgramCode(), is("HIV"));
    assertThat(budgetLineItem.getPeriodDate(), is(date));
    assertThat(budgetLineItem.getAllocatedBudget(), is(valueOf(345.45)));
    assertThat(budgetLineItem.getNotes(), is("My good notes"));
  }

  @Test
  public void shouldTransformBudgetLineItemDTOWithoutParsingDateWhenPatternNotAvailable() throws Exception {
    BudgetLineItemDTO budgetLineItemDTO = new BudgetLineItemDTO("F10", "HIV", null, "345.45", "My good notes");

    BudgetLineItem budgetLineItem = budgetLineItemTransformer.transform(budgetLineItemDTO, null, 1);

    assertThat(budgetLineItem.getFacilityCode(), is("F10"));
    assertThat(budgetLineItem.getProgramCode(), is("HIV"));
    assertThat(budgetLineItem.getPeriodDate(), is(nullValue()));
    assertThat(budgetLineItem.getAllocatedBudget(), is(valueOf(345.45)));
    assertThat(budgetLineItem.getNotes(), is("My good notes"));

  }

  @Test
  public void shouldThrowErrorIfDateIsInInvalidFormat() {
    BudgetLineItemDTO budgetLineItemDTO = new BudgetLineItemDTO("F10", "HIV", "1234-33-44", "345.45", "My good notes");
    int rowNumber = 1;
    String datePattern = "MM/dd/yy";

    expectedException.expect(DataException.class);
    expectedException.expectMessage(format("code: budget.invalid.date.format, params: { %s; %d }", budgetLineItemDTO.getPeriodStartDate(), rowNumber));

    budgetLineItemTransformer.transform(budgetLineItemDTO, datePattern, rowNumber);
  }

  @Test
  public void shouldThrowErrorIfAllocatedBudgetIsNotValid() {
    BudgetLineItemDTO budgetLineItemDTO = new BudgetLineItemDTO("F10", "HIV", "12-12-2013", "345sdsa.45", "My good notes");
    int rowNumber = 1;
    String datePattern = "MM-dd-yyyy";

    expectedException.expect(DataException.class);
    expectedException.expectMessage(format("code: budget.allocated.budget.invalid, params: { %s; %d }", budgetLineItemDTO.getAllocatedBudget(), rowNumber));

    budgetLineItemTransformer.transform(budgetLineItemDTO, datePattern, rowNumber);
  }

  @Test
  public void shouldThrowErrorIfAllocatedBudgetIsNegative() {
    BudgetLineItemDTO budgetLineItemDTO = new BudgetLineItemDTO("F10", "HIV", "12-12-2013", "-345.45", "My good notes");
    int rowNumber = 1;
    String datePattern = "MM-dd-yyyy";

    expectedException.expect(DataException.class);
    expectedException.expectMessage(format("code: budget.allocated.budget.invalid, params: { %s; %d }", budgetLineItemDTO.getAllocatedBudget(), rowNumber));

    budgetLineItemTransformer.transform(budgetLineItemDTO, datePattern, rowNumber);
  }

  @Test
  public void shouldFloorAllocatedBudgetIs() {
    BudgetLineItemDTO budgetLineItemDTO = new BudgetLineItemDTO("F10", "HIV", null, "345.466", "My good notes");

    BudgetLineItem budgetLineItem = budgetLineItemTransformer.transform(budgetLineItemDTO, null, 1);

    assertThat(budgetLineItem.getAllocatedBudget(), is(valueOf(345.46)));
  }

}
