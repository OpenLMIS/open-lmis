package org.openlmis.rnr.domain;

import org.junit.Test;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.*;

public class ProgramRnrTemplateTest {


    @Test
    public void shouldGiveErrorIfDependentsAreMissing() throws Exception {
        Map<String,String> errors = new ProgramRnrTemplate("programCode", asList(
                rnrColumn("quantityRequested", false, null, "Requested Quantity"),
                rnrColumn("reasonForRequestedQuantity", false, null, "Requested Quantity Reason"),
                rnrColumn("stockOutDays", false, RnRColumnSource.CALCULATED, "stockOutDays"),
                rnrColumn("normalizedConsumption", false, RnRColumnSource.USER_INPUT, "normalizedConsumption"),
                rnrColumn(STOCK_IN_HAND, true, RnRColumnSource.USER_INPUT, "stock in hand"),
                rnrColumn(QUANTITY_DISPENSED, true, RnRColumnSource.USER_INPUT, "quantity dispensed"),
                rnrColumn(QUANTITY_RECEIVED, true, RnRColumnSource.USER_INPUT, "quantity received"),
                rnrColumn(BEGINNING_BALANCE, false, RnRColumnSource.USER_INPUT, "beginning balance"),
                rnrColumn(LOSSES_AND_ADJUSTMENTS, true, RnRColumnSource.USER_INPUT, "losses and adjustment")
        )).validate();

        assertThat(errors.get(STOCK_IN_HAND), is("User needs to enter 'beginning balance', 'quantity received', 'losses and adjustment to validate user's entries 'quantity dispensed' and 'stock in hand'"));
    }

    @Test
    public void shouldNotGiveErrorIfDependentsAreNotMissing() throws Exception {
        Map<String,String> errors = new ProgramRnrTemplate("programCode", asList(
                rnrColumn("quantityRequested", false, null, "Requested Quantity"),
                rnrColumn("reasonForRequestedQuantity", false, null, "Requested Quantity Reason"),
                rnrColumn("stockOutDays", false, RnRColumnSource.CALCULATED, "stockOutDays"),
                rnrColumn("normalizedConsumption", false, RnRColumnSource.USER_INPUT, "normalizedConsumption"),
                rnrColumn(STOCK_IN_HAND, true, RnRColumnSource.USER_INPUT, "stock in hand"),
                rnrColumn(QUANTITY_DISPENSED, true, RnRColumnSource.USER_INPUT, "quantity dispensed"),
                rnrColumn(QUANTITY_RECEIVED, true, RnRColumnSource.USER_INPUT, "quantity received"),
                rnrColumn(BEGINNING_BALANCE, true, RnRColumnSource.USER_INPUT, "beginning balance"),
                rnrColumn(LOSSES_AND_ADJUSTMENTS, true, RnRColumnSource.USER_INPUT, "losses and adjustment")
        )).validate();

        assertThat(errors.size(), is(0));
    }

    @Test
    public void shouldReturnValidationErrorWhenDependantColumnsForQuantityDispensedIsNotVisible() {
        Map<String,String> errors = new ProgramRnrTemplate("programCode", asList(
                rnrColumn("quantityRequested", false, null, "Requested Quantity"),
                rnrColumn("reasonForRequestedQuantity", false, null, "Requested Quantity Reason"),
                rnrColumn("stockOutDays", false, RnRColumnSource.CALCULATED, "stockOutDays"),
                rnrColumn("normalizedConsumption", false, RnRColumnSource.USER_INPUT, "normalizedConsumption"),
                rnrColumn(STOCK_IN_HAND, true, RnRColumnSource.USER_INPUT, "stock in hand"),
                rnrColumn(QUANTITY_DISPENSED, true, RnRColumnSource.CALCULATED, "quantity dispensed"),
                rnrColumn(QUANTITY_RECEIVED, true, RnRColumnSource.USER_INPUT, "quantity received"),
                rnrColumn(BEGINNING_BALANCE, false, RnRColumnSource.USER_INPUT, "beginning balance"),
                rnrColumn(LOSSES_AND_ADJUSTMENTS, true, RnRColumnSource.USER_INPUT, "losses and adjustment")
        )).validate();
        assertEquals("User needs to enter 'beginning balance', 'quantity received', 'losses and adjustment', 'stock in hand' to calculate 'quantity dispensed'"
                , errors.get("quantityDispensed"));
    }

    @Test
    public void shouldReturnValidationErrorWhenDependantColumnsForStockInHandIsNotVisible() {
        Map<String, String> errors = new ProgramRnrTemplate("programCode", asList(
                rnrColumn("quantityRequested", false, null, "Requested Quantity"),
                rnrColumn("reasonForRequestedQuantity", false, null, "Requested Quantity Reason"),
                rnrColumn("stockOutDays", false, RnRColumnSource.CALCULATED, "stockOutDays"),
                rnrColumn("normalizedConsumption", false, RnRColumnSource.USER_INPUT, "normalizedConsumption"),
                rnrColumn(STOCK_IN_HAND, true, RnRColumnSource.CALCULATED, "stock in hand"),
                rnrColumn(QUANTITY_DISPENSED, true, RnRColumnSource.USER_INPUT, "quantity dispensed"),
                rnrColumn(QUANTITY_RECEIVED, true, RnRColumnSource.USER_INPUT, "quantity received"),
                rnrColumn(BEGINNING_BALANCE, false, RnRColumnSource.USER_INPUT, "beginning balance"),
                rnrColumn(LOSSES_AND_ADJUSTMENTS, true, RnRColumnSource.USER_INPUT, "losses and adjustment")
        )).validate();
        assertEquals("User needs to enter 'beginning balance', 'quantity received', 'losses and adjustment', 'quantity dispensed' to calculate 'stock in hand'"
                , errors.get("stockInHand"));
    }


    @Test
    public void shouldReturnValidationErrorWhenStockInHandAndQuantityDispensedBothAreCalculated() {
        Map<String, String> errors = new ProgramRnrTemplate("programCode", asList(
                rnrColumn("quantityRequested", false, null, "Requested Quantity"),
                rnrColumn("reasonForRequestedQuantity", false, null, "Requested Quantity Reason"),
                rnrColumn("stockOutDays", false, RnRColumnSource.CALCULATED, "stockOutDays"),
                rnrColumn("normalizedConsumption", false, RnRColumnSource.USER_INPUT, "normalizedConsumption"),
                rnrColumn(STOCK_IN_HAND, true, RnRColumnSource.CALCULATED, "stock in hand"),
                rnrColumn(QUANTITY_DISPENSED, true, RnRColumnSource.CALCULATED, "quantity dispensed"),
                rnrColumn(QUANTITY_RECEIVED, true, RnRColumnSource.USER_INPUT, "quantity received"),
                rnrColumn(BEGINNING_BALANCE, true, RnRColumnSource.USER_INPUT, "beginning balance"),
                rnrColumn(LOSSES_AND_ADJUSTMENTS, true, RnRColumnSource.USER_INPUT, "losses and adjustment")
        )).validate();
        assertEquals("Interdependent fields ('quantity dispensed', 'stock in hand') cannot be of type Calculated at the same time"
                , errors.get("stockInHand"));
        assertEquals("Interdependent fields ('quantity dispensed', 'stock in hand') cannot be of type Calculated at the same time"
                , errors.get("quantityDispensed"));

    }

    @Test
    public void shouldReturnValidationErrorWhenOnlyTheNumberOfStockOutDaysIsSelectedButNotNormalizedConsumption() {
        Map<String, String> errors = new ProgramRnrTemplate("programCode", asList(
                rnrColumn("stockOutDays", true, RnRColumnSource.CALCULATED, "stockOutDays"),
                rnrColumn("normalizedConsumption", false, RnRColumnSource.USER_INPUT, "normalizedConsumption"),
                rnrColumn("stockInHand", false, RnRColumnSource.CALCULATED, "stockInHand"),
                rnrColumn("beginningBalance", true, RnRColumnSource.USER_INPUT, "beginningBalance"),
                rnrColumn("quantityReceived", true, RnRColumnSource.USER_INPUT, "quantityReceived"),
                rnrColumn("lossesAndAdjustments", true, RnRColumnSource.USER_INPUT, "lossesAndAdjustments"),
                rnrColumn("quantityRequested", false, null, "Requested Quantity"),
                rnrColumn("reasonForRequestedQuantity", false, null, "Requested Quantity Reason"),
                rnrColumn("quantityDispensed", false, RnRColumnSource.CALCULATED, "quantityDispensed"))).validate();
        assertEquals("'normalizedConsumption' is needed if you report 'stockOutDays'", errors.get("stockOutDays"));
    }

    @Test
    public void shouldReturnValidationErrorWhenOnlyNormalizedConsumptionIsSelectedButNotNumberOfStockOutDays() {
        Map<String, String> errors = new ProgramRnrTemplate("programCode", asList(
                rnrColumn("stockOutDays", false, RnRColumnSource.CALCULATED, "Number of Stock out days"),
                rnrColumn("normalizedConsumption", true, RnRColumnSource.USER_INPUT, "Normalized Consumption"),
                rnrColumn("stockInHand", false, RnRColumnSource.CALCULATED, "stockInHand"),
                rnrColumn("beginningBalance", true, RnRColumnSource.USER_INPUT, "beginningBalance"),
                rnrColumn("quantityReceived", true, RnRColumnSource.USER_INPUT, "quantityReceived"),
                rnrColumn("lossesAndAdjustments", true, RnRColumnSource.USER_INPUT, "lossesAndAdjustments"),
                rnrColumn("quantityRequested", false, null, "Requested Quantity"),
                rnrColumn("reasonForRequestedQuantity", false, null, "Requested Quantity Reason"),
                rnrColumn("quantityDispensed", false, RnRColumnSource.CALCULATED, "quantityDispensed"))).validate();
        assertEquals("User needs to enter 'Number of Stock out days' to calculate 'Normalized Consumption'", errors.get("normalizedConsumption"));
    }

    @Test
    public void shouldReturnValidationErrorWhenOnlyRequestedAmountIsSelectedButNotReasonForRequestedAmount() {

        Map<String, String> errors = new ProgramRnrTemplate("programCode", asList(
                rnrColumn("stockOutDays", false, RnRColumnSource.CALCULATED, "Number of Stock out days"),
                rnrColumn("normalizedConsumption", true, RnRColumnSource.USER_INPUT, "Normalized Consumption"),
                rnrColumn("stockInHand", false, RnRColumnSource.CALCULATED, "stockInHand"),
                rnrColumn("beginningBalance", true, RnRColumnSource.USER_INPUT, "beginningBalance"),
                rnrColumn("quantityReceived", true, RnRColumnSource.USER_INPUT, "quantityReceived"),
                rnrColumn("lossesAndAdjustments", true, RnRColumnSource.USER_INPUT, "lossesAndAdjustments"),
                rnrColumn("quantityDispensed", false, RnRColumnSource.CALCULATED, "quantityDispensed"),
                rnrColumn("quantityRequested", true, null, "Requested Quantity"),
                rnrColumn("reasonForRequestedQuantity", false, null, "Requested Quantity Reason"))).validate();
        assertEquals("'Requested Quantity' must include an 'Requested Quantity Reason' from the user", errors.get("quantityRequested"));
    }

    private RnrColumn rnrColumn(String columnName, boolean visible, RnRColumnSource selectedColumnSource, String label) {

        RnrColumn rnrColumn = new RnrColumn();
        rnrColumn.setSource(selectedColumnSource);
        rnrColumn.setVisible(visible);
        rnrColumn.setName(columnName);
        rnrColumn.setLabel(label);
        rnrColumn.setFormulaValidated(true);
        return rnrColumn;
    }

}
