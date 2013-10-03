/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.db.categories.UnitTests;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.*;
import static org.openlmis.rnr.domain.RnRColumnSource.CALCULATED;
import static org.openlmis.rnr.domain.RnRColumnSource.USER_INPUT;

@Category(UnitTests.class)
public class ProgramRnrTemplateTest {


  private ProgramRnrTemplate template;

  @Before
  public void setup() {
    template = new ProgramRnrTemplate(1L, asList(
      rnrColumn(QUANTITY_REQUESTED, false, null, "Requested Quantity"),
      rnrColumn(REASON_FOR_REQUESTED_QUANTITY, false, null, "Requested Quantity Reason"),
      rnrColumn(STOCK_OUT_DAYS, false, CALCULATED, "stockOutDays"),
      rnrColumn(NORMALIZED_CONSUMPTION, false, USER_INPUT, "normalizedConsumption"),
      rnrColumn(STOCK_IN_HAND, true, USER_INPUT, "stock in hand"),
      rnrColumn(QUANTITY_DISPENSED, true, CALCULATED, "quantity dispensed"),
      rnrColumn(QUANTITY_RECEIVED, true, USER_INPUT, "quantity received"),
      rnrColumn(BEGINNING_BALANCE, false, USER_INPUT, "beginning balance"),
      rnrColumn(LOSSES_AND_ADJUSTMENTS, true, USER_INPUT, "losses and adjustment")
    ));
  }

  @Test
  public void shouldNotGiveErrorIfDependentsAreNotMissing() throws Exception {
    Map<String, OpenLmisMessage> errors = new ProgramRnrTemplate(1L, asList(
      rnrColumn(QUANTITY_REQUESTED, false, null, "Requested Quantity"),
      rnrColumn(REASON_FOR_REQUESTED_QUANTITY, false, null, "Requested Quantity Reason"),
      rnrColumn(STOCK_OUT_DAYS, false, CALCULATED, "stockOutDays"),
      rnrColumn(NORMALIZED_CONSUMPTION, false, USER_INPUT, "normalizedConsumption"),
      rnrColumn(STOCK_IN_HAND, true, USER_INPUT, "stock in hand"),
      rnrColumn(QUANTITY_DISPENSED, true, USER_INPUT, "quantity dispensed"),
      rnrColumn(QUANTITY_RECEIVED, true, USER_INPUT, "quantity received"),
      rnrColumn(BEGINNING_BALANCE, true, USER_INPUT, "beginning balance"),
      rnrColumn(LOSSES_AND_ADJUSTMENTS, true, USER_INPUT, "losses and adjustment")
    )).validateToSave();

    assertThat(errors.size(), is(0));
  }

  @Test
  public void shouldReturnValidationErrorWhenDependantColumnsForQuantityDispensedIsNotVisible() {
    Map<String, RnrColumn> rnrColumnsMap = template.getRnrColumnsMap();
    rnrColumnsMap.put(QUANTITY_DISPENSED, rnrColumn(QUANTITY_DISPENSED, true, CALCULATED, "quantity dispensed"));
    rnrColumnsMap.put(STOCK_IN_HAND, rnrColumn(STOCK_IN_HAND, false, USER_INPUT, "stock in hand"));

    Map<String, OpenLmisMessage> errors = template.validateToSave();
    OpenLmisMessage openLmisMessage = errors.get("quantityDispensed");

    assertThat(openLmisMessage.getCode(), is("user.needs.to.enter.dependent.field"));
    assertThat(openLmisMessage.getParams(), is(new String[]{"stock in hand", "quantity dispensed"}));

  }

  @Test
  public void shouldReturnValidationErrorWhenDependantColumnsForStockInHandIsNotVisible() {
    Map<String, RnrColumn> rnrColumnsMap = template.getRnrColumnsMap();

    rnrColumnsMap.put(QUANTITY_DISPENSED, rnrColumn(QUANTITY_DISPENSED, false, USER_INPUT, "quantity dispensed"));
    rnrColumnsMap.put(STOCK_IN_HAND, rnrColumn(STOCK_IN_HAND, true, CALCULATED, "stock in hand"));

    Map<String, OpenLmisMessage> errors = template.validateToSave();
    OpenLmisMessage openLmisMessage = errors.get("stockInHand");

    assertThat(openLmisMessage.getCode(), is("user.needs.to.enter.dependent.field"));
    assertThat(openLmisMessage.getParams(), is(new String[]{"quantity dispensed", "stock in hand"}));
  }


  @Test
  public void shouldReturnValidationErrorWhenStockInHandAndQuantityDispensedBothAreCalculated() {
    Map<String, RnrColumn> rnrColumnsMap = template.getRnrColumnsMap();

    rnrColumnsMap.put(QUANTITY_DISPENSED, rnrColumn(QUANTITY_DISPENSED, false, CALCULATED, "quantity dispensed"));
    rnrColumnsMap.put(STOCK_IN_HAND, rnrColumn(STOCK_IN_HAND, false, CALCULATED, "stock in hand"));

    Map<String, OpenLmisMessage> errors = template.validateToSave();

    assertThat(errors.get(QUANTITY_DISPENSED).getCode(), is("error.interdependent.fields.can.not.be.calculated"));
    assertThat(errors.get(QUANTITY_DISPENSED).getParams(), is(new String[]{"quantity dispensed", "stock in hand"}));

    assertThat(errors.get(STOCK_IN_HAND).getCode(), is("error.interdependent.fields.can.not.be.calculated"));
    assertThat(errors.get(STOCK_IN_HAND).getParams(), is(new String[]{"quantity dispensed", "stock in hand"}));

  }

  @Test
  public void shouldReturnValidationErrorColumnIsUserInputAndNotVisible() throws Exception {
    Map<String, RnrColumn> rnrColumnsMap = template.getRnrColumnsMap();

    rnrColumnsMap.put(QUANTITY_DISPENSED, rnrColumn(QUANTITY_DISPENSED, false, USER_INPUT, "quantity dispensed"));
    rnrColumnsMap.put(STOCK_IN_HAND, rnrColumn(STOCK_IN_HAND, false, USER_INPUT, "stock in hand"));

    Map<String, OpenLmisMessage> errorMap = template.validateToSave();

    OpenLmisMessage errorMessageForQuantityDispensed = errorMap.get(QUANTITY_DISPENSED);
    assertThat(errorMessageForQuantityDispensed.getCode(), is(COLUMN_SHOULD_BE_VISIBLE_IF_USER_INPUT));
    assertThat(errorMessageForQuantityDispensed.getParams(), is(new String[]{"quantity dispensed"}));

    OpenLmisMessage errorMessageForStockInHand = errorMap.get(STOCK_IN_HAND);
    assertThat(errorMessageForStockInHand.getCode(), is(COLUMN_SHOULD_BE_VISIBLE_IF_USER_INPUT));
    assertThat(errorMessageForStockInHand.getParams(), is(new String[]{"stock in hand"}));

  }

  @Test
  public void shouldReturnValidationErrorWhenOnlyRequestedAmountIsSelectedButNotReasonForRequestedAmount() {
    template.getRnrColumnsMap().put(QUANTITY_REQUESTED, rnrColumn(QUANTITY_REQUESTED, true, USER_INPUT, "Requested Quantity"));
    template.getRnrColumnsMap().put(REASON_FOR_REQUESTED_QUANTITY, rnrColumn(REASON_FOR_REQUESTED_QUANTITY, false, USER_INPUT, "Requested Quantity Reason"));

    Map<String, OpenLmisMessage> errors = template.validateToSave();
    assertThat(errors.get(QUANTITY_REQUESTED).getCode(), is("error.user.needs.to.enter.requested.quantity.reason"));
    assertThat(errors.get(QUANTITY_REQUESTED).getParams(), is(new String[]{"Requested Quantity", "Requested Quantity Reason"}));
  }

  @Test
  public void shouldGetOnlyVisibleColumnAsPrintableColumnsForFullSupply() throws Exception {
    boolean visible = true, invisible = false;
    boolean fullSupply = true;

    ProgramRnrTemplate programRnrTemplate = new ProgramRnrTemplate(1L, asList(
      rnrColumn(REMARKS, visible),
      rnrColumn(REASON_FOR_REQUESTED_QUANTITY, visible),
      rnrColumn(QUANTITY_REQUESTED, visible),
      rnrColumn(STOCK_OUT_DAYS, invisible)
    ));
    List<? extends Column> printableColumns = programRnrTemplate.getPrintableColumns(fullSupply);
    assertThat(printableColumns.size(), is(1));
    assertThat(printableColumns.get(0).getName(), is(QUANTITY_REQUESTED));
  }

  @Test
  public void shouldGetOnlyVisibleColumnsAsPrintableColumnsForNonFullSupply() throws Exception {
    boolean visible = true;
    boolean fullSupply = false;

    ProgramRnrTemplate programRnrTemplate = new ProgramRnrTemplate(1L, asList(
      rnrColumn(PRODUCT, visible),
      rnrColumn(PRODUCT_CODE, visible),
      rnrColumn(DISPENSING_UNIT, visible),
      rnrColumn(QUANTITY_REQUESTED, visible),
      rnrColumn(PACKS_TO_SHIP, visible),
      rnrColumn(PRICE, visible),
      rnrColumn(COST, visible),
      rnrColumn(QUANTITY_APPROVED, visible),

      rnrColumn(QUANTITY_DISPENSED, visible)
    ));

    List<? extends Column> printableColumns = programRnrTemplate.getPrintableColumns(fullSupply);

    assertThat(printableColumns.size(), is(8));
    assertFalse(printableColumns.contains(rnrColumn(QUANTITY_DISPENSED, visible)));
    assertTrue(printableColumns.contains(rnrColumn(QUANTITY_APPROVED, visible)));
  }

  @Test
  public void shouldNotGetInvisibleColumnsAsPrintableColumnsForNonFullSupply() throws Exception {
    boolean visible = true;
    boolean inVisible = false;
    boolean fullSupply = false;

    ProgramRnrTemplate programRnrTemplate = new ProgramRnrTemplate(1L, asList(
      rnrColumn(PRODUCT, visible),
      rnrColumn(QUANTITY_APPROVED, inVisible),

      rnrColumn(QUANTITY_DISPENSED, visible)
    ));

    List<? extends Column> printableColumns = programRnrTemplate.getPrintableColumns(fullSupply);

    assertThat(printableColumns.size(), is(1));
    assertThat(printableColumns.get(0).getName(), is(PRODUCT));
  }


  private RnrColumn rnrColumn(String columnName, boolean visible) {
    return rnrColumn(columnName, visible, null, null);
  }

  private RnrColumn rnrColumn(String columnName, boolean visible, RnRColumnSource selectedColumnSource, String label) {
    RnrColumn rnrColumn = new RnrColumn();
    rnrColumn.setSource(selectedColumnSource);
    rnrColumn.setVisible(visible);
    rnrColumn.setName(columnName);
    rnrColumn.setLabel(label);
    rnrColumn.setFormulaValidationRequired(true);
    return rnrColumn;
  }

}
