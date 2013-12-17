package org.openlmis.core.transformer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.BudgetLineItem;
import org.openlmis.core.dto.BudgetLineItemDTO;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.transformer.budget.BudgetLineItemTransformer;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.math.BigDecimal.valueOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BudgetLineItemTransformerTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private MessageService messageService;

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
    String datePattern = null;

    BudgetLineItem budgetLineItem = budgetLineItemTransformer.transform(budgetLineItemDTO, datePattern, 1);

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
    when(messageService.message("budget.invalid.date.format", budgetLineItemDTO.getPeriodStartDate(), rowNumber)).thenReturn("Invalid date format");

    expectedException.expect(DataException.class);
    expectedException.expectMessage("Invalid date format");

    budgetLineItemTransformer.transform(budgetLineItemDTO, datePattern, rowNumber);
  }

  @Test
  public void shouldThrowErrorIfAllocatedBudgetIsNotValid() {
    BudgetLineItemDTO budgetLineItemDTO = new BudgetLineItemDTO("F10", "HIV", "12-12-2013", "345sdsa.45", "My good notes");
    int rowNumber = 1;
    String datePattern = "MM-dd-yyyy";
    when(messageService.message("budget.allocated.budget.invalid", budgetLineItemDTO.getAllocatedBudget(), rowNumber)).thenReturn("Invalid budget");

    expectedException.expect(DataException.class);
    expectedException.expectMessage("Invalid budget");

    budgetLineItemTransformer.transform(budgetLineItemDTO, datePattern, rowNumber);
  }

  @Test
  public void shouldThrowErrorIfAllocatedBudgetIsNegative() {
    BudgetLineItemDTO budgetLineItemDTO = new BudgetLineItemDTO("F10", "HIV", "12-12-2013", "-345.45", "My good notes");
    int rowNumber = 1;
    String datePattern = "MM-dd-yyyy";
    when(messageService.message("budget.allocated.budget.invalid", budgetLineItemDTO.getAllocatedBudget(), rowNumber)).thenReturn("Invalid budget");

    expectedException.expect(DataException.class);
    expectedException.expectMessage("Invalid budget");

    budgetLineItemTransformer.transform(budgetLineItemDTO, datePattern, rowNumber);
  }

  @Test
  public void shouldFloorAllocatedBudgetIs() {
    BudgetLineItemDTO budgetLineItemDTO = new BudgetLineItemDTO("F10", "HIV", null, "345.466", "My good notes");
    String datePattern = null;

    BudgetLineItem budgetLineItem = budgetLineItemTransformer.transform(budgetLineItemDTO, datePattern, 1);

    assertThat(budgetLineItem.getAllocatedBudget(), is(valueOf(345.46)));
  }

}
