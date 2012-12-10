package org.openlmis.rnr.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.rnr.domain.RnRColumnSource;
import org.openlmis.rnr.domain.RnrColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/applicationContext-requisition.xml")
public class RnrTemplateRuleServiceIT {

    @Autowired
    private RnrTemplateRuleService rnrTemplateRuleService;

    @Test
    public void shouldReturnValidationErrorWhenDependantColumnsForQuantityDispensedIsNotVisible() {
        Map<String, String> errors = rnrTemplateRuleService.validate(asList(
                rnrColumn("quantityDispensed", true, RnRColumnSource.CALCULATED, "quantityDispensed"),
                rnrColumn("beginningBalance", true, RnRColumnSource.USER_INPUT, "beginningBalance"),
                rnrColumn("quantityReceived", true, RnRColumnSource.USER_INPUT, "quantityReceived"),
                rnrColumn("lossesAndAdjustments", true, RnRColumnSource.USER_INPUT, "lossesAndAdjustments"),
                rnrColumn("stockInHand", false, RnRColumnSource.USER_INPUT, "stockInHand"),
                rnrColumn("quantityRequested", false,  null, "Requested Quantity"),
                rnrColumn("reasonForRequestedQuantity", false , null, "Requested Quantity Reason"),
                rnrColumn("stockOutDays", false, RnRColumnSource.CALCULATED, "stockOutDays"),
                rnrColumn("normalizedConsumption", false, RnRColumnSource.USER_INPUT, "normalizedConsumption")));
        assertEquals("User needs to enter \"beginningBalance\",\"quantityReceived\",\"lossesAndAdjustments\",\"stockInHand\" to calculate \"quantityDispensed\""
                , errors.get("quantityDispensed"));
    }

    @Test
    public void shouldReturnValidationErrorWhenDependantColumnsForStockInHandIsNotVisible() {
        Map<String, String> errors = rnrTemplateRuleService.validate(asList(
                rnrColumn("stockInHand", true, RnRColumnSource.CALCULATED, "stockInHand"),
                rnrColumn("beginningBalance", true, RnRColumnSource.USER_INPUT, "beginningBalance"),
                rnrColumn("quantityReceived", true, RnRColumnSource.USER_INPUT, "quantityReceived"),
                rnrColumn("lossesAndAdjustments", true, RnRColumnSource.USER_INPUT, "lossesAndAdjustments"),
                rnrColumn("quantityDispensed", false, RnRColumnSource.USER_INPUT, "quantityDispensed"),
                rnrColumn("quantityRequested", false,  null, "Requested Quantity"),
                rnrColumn("reasonForRequestedQuantity", false , null, "Requested Quantity Reason"),
                rnrColumn("stockOutDays", false, RnRColumnSource.CALCULATED, "stockOutDays"),
                rnrColumn("normalizedConsumption", false, RnRColumnSource.USER_INPUT, "normalizedConsumption")));
        assertEquals("User needs to enter \"beginningBalance\",\"quantityReceived\",\"lossesAndAdjustments\",\"quantityDispensed\" to calculate \"stockInHand\""
                , errors.get("stockInHand"));
    }


    @Test
    public void shouldReturnValidationErrorWhenStockInHandAndQuantityDispensedBothAreCalculated() {
        Map<String, String> errors = rnrTemplateRuleService.validate(asList(
                rnrColumn("stockInHand", true, RnRColumnSource.CALCULATED, "stockInHand"),
                rnrColumn("beginningBalance", true, RnRColumnSource.USER_INPUT, "beginningBalance"),
                rnrColumn("quantityReceived", true, RnRColumnSource.USER_INPUT, "quantityReceived"),
                rnrColumn("lossesAndAdjustments", true, RnRColumnSource.USER_INPUT, "lossesAndAdjustments"),
                rnrColumn("quantityDispensed", true, RnRColumnSource.CALCULATED, "quantityDispensed"),
                rnrColumn("stockOutDays", false, RnRColumnSource.CALCULATED, "stockOutDays"),
                rnrColumn("quantityRequested", false,  null, "Requested Quantity"),
                rnrColumn("reasonForRequestedQuantity", false , null, "Requested Quantity Reason") ,
                rnrColumn("normalizedConsumption", false, RnRColumnSource.USER_INPUT, "normalizedConsumption")));
        assertEquals("Interdependent fields (\"quantityDispensed\", \"stockInHand\") cannot be of type \"Calculated\" at the same time"
                , errors.get("stockInHand"));
        assertEquals("Interdependent fields (\"quantityDispensed\", \"stockInHand\") cannot be of type \"Calculated\" at the same time"
                , errors.get("quantityDispensed"));

    }

    @Test
    public void shouldReturnValidationErrorWhenOnlyTheNumberOfStockOutDaysIsSelectedButNotNormalizedConsumption() {
        Map<String, String> errors = rnrTemplateRuleService.validate(asList(
                rnrColumn("stockOutDays", true, RnRColumnSource.CALCULATED, "stockOutDays"),
                rnrColumn("normalizedConsumption", false, RnRColumnSource.USER_INPUT, "normalizedConsumption"),
                rnrColumn("stockInHand", false, RnRColumnSource.CALCULATED, "stockInHand"),
                rnrColumn("beginningBalance", true, RnRColumnSource.USER_INPUT, "beginningBalance"),
                rnrColumn("quantityReceived", true, RnRColumnSource.USER_INPUT, "quantityReceived"),
                rnrColumn("lossesAndAdjustments", true, RnRColumnSource.USER_INPUT, "lossesAndAdjustments"),
                rnrColumn("quantityRequested", false,  null, "Requested Quantity"),
                rnrColumn("reasonForRequestedQuantity", false , null, "Requested Quantity Reason"),
                rnrColumn("quantityDispensed", false, RnRColumnSource.CALCULATED, "quantityDispensed")));
        assertEquals("\"normalizedConsumption\" is needed if you report \"stockOutDays\"", errors.get("stockOutDays"));
    }

    @Test
    public void shouldReturnValidationErrorWhenOnlyNormalizedConsumptionIsSelectedButNotNumberOfStockOutDays() {
        Map<String, String> errors = rnrTemplateRuleService.validate(asList(
                rnrColumn("stockOutDays", false, RnRColumnSource.CALCULATED, "Number of Stock out days"),
                rnrColumn("normalizedConsumption", true, RnRColumnSource.USER_INPUT, "Normalized Consumption"),
                rnrColumn("stockInHand", false, RnRColumnSource.CALCULATED, "stockInHand"),
                rnrColumn("beginningBalance", true, RnRColumnSource.USER_INPUT, "beginningBalance"),
                rnrColumn("quantityReceived", true, RnRColumnSource.USER_INPUT, "quantityReceived"),
                rnrColumn("lossesAndAdjustments", true, RnRColumnSource.USER_INPUT, "lossesAndAdjustments"),
                rnrColumn("quantityRequested", false,  null, "Requested Quantity"),
                rnrColumn("reasonForRequestedQuantity", false , null, "Requested Quantity Reason"),
                rnrColumn("quantityDispensed", false, RnRColumnSource.CALCULATED, "quantityDispensed")));
        assertEquals("User needs to enter \"Number of Stock out days\" to calculate \"Normalized Consumption\"", errors.get("normalizedConsumption"));
    }

    @Test
    public void shouldReturnValidationErrorWhenOnlyRequestedAmountIsSelectedButNotReasonForRequestedAmount() {

        Map<String, String> errors = rnrTemplateRuleService.validate(asList(
                rnrColumn("stockOutDays", false, RnRColumnSource.CALCULATED, "Number of Stock out days"),
                rnrColumn("normalizedConsumption", true, RnRColumnSource.USER_INPUT, "Normalized Consumption"),
                rnrColumn("stockInHand", false, RnRColumnSource.CALCULATED, "stockInHand"),
                rnrColumn("beginningBalance", true, RnRColumnSource.USER_INPUT, "beginningBalance"),
                rnrColumn("quantityReceived", true, RnRColumnSource.USER_INPUT, "quantityReceived"),
                rnrColumn("lossesAndAdjustments", true, RnRColumnSource.USER_INPUT, "lossesAndAdjustments"),
                rnrColumn("quantityDispensed", false, RnRColumnSource.CALCULATED, "quantityDispensed"),
                rnrColumn("quantityRequested", true,  null, "Requested Quantity"),
                rnrColumn("reasonForRequestedQuantity", false , null, "Requested Quantity Reason")));
        assertEquals("\"Requested Quantity\" must include an \"Requested Quantity Reason\" from the user", errors.get("quantityRequested"));
    }





    private RnrColumn rnrColumn(String columnName, boolean visible, RnRColumnSource selectedColumnSource, String label) {
        RnrColumn rnrColumn = new RnrColumn();
        rnrColumn.setName(columnName);
        rnrColumn.setVisible(visible);
        rnrColumn.setSource(selectedColumnSource);
        rnrColumn.setLabel(label);
        return rnrColumn;
    }
}
