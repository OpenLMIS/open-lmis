/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.domain;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.core.message.OpenLmisMessage;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.*;
import static org.openlmis.rnr.domain.RnRColumnSource.CALCULATED;
import static org.openlmis.rnr.domain.RnRColumnSource.USER_INPUT;

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

    assertThat(errors.get(QUANTITY_DISPENSED).getCode(), is("interdependent.fields.can.not.be.calculated"));
    assertThat(errors.get(QUANTITY_DISPENSED).getParams(), is(new String[]{"quantity dispensed", "stock in hand"}));

    assertThat(errors.get(STOCK_IN_HAND).getCode(), is("interdependent.fields.can.not.be.calculated"));
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
    assertThat(errors.get(QUANTITY_REQUESTED).getCode(), is("user.needs.to.enter.requested.quantity.reason"));
    assertThat(errors.get(QUANTITY_REQUESTED).getParams(), is(new String[]{"Requested Quantity","Requested Quantity Reason"}));
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
