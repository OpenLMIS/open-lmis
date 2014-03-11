/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
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

    assertThat(budgetLineItem.getPeriodDate(), is(date));
    assertThat(budgetLineItem.getAllocatedBudget(), is(valueOf(345.45)));
    assertThat(budgetLineItem.getNotes(), is("My good notes"));
  }

  @Test
  public void shouldTransformBudgetLineItemDTOWithoutParsingDateWhenPatternNotAvailable() throws Exception {
    BudgetLineItemDTO budgetLineItemDTO = new BudgetLineItemDTO("F10", "HIV", null, "345.45", "My good notes");

    BudgetLineItem budgetLineItem = budgetLineItemTransformer.transform(budgetLineItemDTO, null, 1);

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
    BudgetLineItemDTO budgetLineItemDTO = new BudgetLineItemDTO("F10", "HIV", "12-12-2013", "345Word.45", "My good notes");
    int rowNumber = 1;
    String datePattern = "MM-dd-yyyy";

    expectedException.expect(DataException.class);
    expectedException.expectMessage(format("code: budget.allocated.invalid, params: { %s; %d }", budgetLineItemDTO.getAllocatedBudget(), rowNumber));

    budgetLineItemTransformer.transform(budgetLineItemDTO, datePattern, rowNumber);
  }

  @Test
  public void shouldThrowErrorIfAllocatedBudgetIsNegative() {
    BudgetLineItemDTO budgetLineItemDTO = new BudgetLineItemDTO("F10", "HIV", "12-12-2013", "-345.45", "My good notes");
    int rowNumber = 1;
    String datePattern = "MM-dd-yyyy";

    expectedException.expect(DataException.class);
    expectedException.expectMessage(format("code: budget.allocated.invalid, params: { %s; %d }", budgetLineItemDTO.getAllocatedBudget(), rowNumber));

    budgetLineItemTransformer.transform(budgetLineItemDTO, datePattern, rowNumber);
  }

  @Test
  public void shouldFloorAllocatedBudgetIs() {
    BudgetLineItemDTO budgetLineItemDTO = new BudgetLineItemDTO("F10", "HIV", null, "345.466", "My good notes");

    BudgetLineItem budgetLineItem = budgetLineItemTransformer.transform(budgetLineItemDTO, null, 1);

    assertThat(budgetLineItem.getAllocatedBudget(), is(valueOf(345.46)));
  }

}
