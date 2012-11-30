package org.openlmis.rnr.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.domain.RnrColumnType;
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
                rnrColumn("quantityDispensed", true, RnrColumnType.Calculated, "quantityDispensed"),
                rnrColumn("beginningBalance", true, RnrColumnType.User_Input, "beginningBalance"),
                rnrColumn("quantityReceived", true, RnrColumnType.User_Input, "quantityReceived"),
                rnrColumn("lossesAndAdjustments", true, RnrColumnType.User_Input, "lossesAndAdjustments"),
                rnrColumn("stockInHand", false, RnrColumnType.User_Input, "stockInHand"),
                rnrColumn("quantityRequested", false,  null, "Requested Quantity"),
                rnrColumn("reasonForRequestedQuantity", false , null, "Requested Quantity Reason"),
                rnrColumn("stockOutDays", false, RnrColumnType.Calculated, "stockOutDays"),
                rnrColumn("normalizedConsumption", false, RnrColumnType.User_Input, "normalizedConsumption")));
        assertEquals("User needs to enter \"beginningBalance\",\"quantityReceived\",\"lossesAndAdjustments\",\"stockInHand\" to calculate \"quantityDispensed\""
                , errors.get("quantityDispensed"));
    }

    @Test
    public void shouldReturnValidationErrorWhenDependantColumnsForStockInHandIsNotVisible() {
        Map<String, String> errors = rnrTemplateRuleService.validate(asList(
                rnrColumn("stockInHand", true, RnrColumnType.Calculated, "stockInHand"),
                rnrColumn("beginningBalance", true, RnrColumnType.User_Input, "beginningBalance"),
                rnrColumn("quantityReceived", true, RnrColumnType.User_Input, "quantityReceived"),
                rnrColumn("lossesAndAdjustments", true, RnrColumnType.User_Input, "lossesAndAdjustments"),
                rnrColumn("quantityDispensed", false, RnrColumnType.User_Input, "quantityDispensed"),
                rnrColumn("quantityRequested", false,  null, "Requested Quantity"),
                rnrColumn("reasonForRequestedQuantity", false , null, "Requested Quantity Reason"),
                rnrColumn("stockOutDays", false, RnrColumnType.Calculated, "stockOutDays"),
                rnrColumn("normalizedConsumption", false, RnrColumnType.User_Input, "normalizedConsumption")));
        assertEquals("User needs to enter \"beginningBalance\",\"quantityReceived\",\"lossesAndAdjustments\",\"quantityDispensed\" to calculate \"stockInHand\""
                , errors.get("stockInHand"));
    }


    @Test
    public void shouldReturnValidationErrorWhenStockInHandAndQuantityDispensedBothAreCalculated() {
        Map<String, String> errors = rnrTemplateRuleService.validate(asList(
                rnrColumn("stockInHand", true, RnrColumnType.Calculated, "stockInHand"),
                rnrColumn("beginningBalance", true, RnrColumnType.User_Input, "beginningBalance"),
                rnrColumn("quantityReceived", true, RnrColumnType.User_Input, "quantityReceived"),
                rnrColumn("lossesAndAdjustments", true, RnrColumnType.User_Input, "lossesAndAdjustments"),
                rnrColumn("quantityDispensed", true, RnrColumnType.Calculated, "quantityDispensed"),
                rnrColumn("stockOutDays", false, RnrColumnType.Calculated, "stockOutDays"),
                rnrColumn("quantityRequested", false,  null, "Requested Quantity"),
                rnrColumn("reasonForRequestedQuantity", false , null, "Requested Quantity Reason") ,
                rnrColumn("normalizedConsumption", false, RnrColumnType.User_Input, "normalizedConsumption")));
        assertEquals("Interdependent fields (quantityDispensed,stockInHand) cannot be of type ‘calculated’ at the same time"
                , errors.get("stockInHand"));
        assertEquals("Interdependent fields (quantityDispensed,stockInHand) cannot be of type ‘calculated’ at the same time"
                , errors.get("quantityDispensed"));

    }

    @Test
    public void shouldReturnValidationErrorWhenOnlyTheNumberOfStockOutDaysIsSelectedButNotNormalizedConsumption() {
        Map<String, String> errors = rnrTemplateRuleService.validate(asList(
                rnrColumn("stockOutDays", true, RnrColumnType.Calculated, "stockOutDays"),
                rnrColumn("normalizedConsumption", false, RnrColumnType.User_Input, "normalizedConsumption"),
                rnrColumn("stockInHand", false, RnrColumnType.Calculated, "stockInHand"),
                rnrColumn("beginningBalance", true, RnrColumnType.User_Input, "beginningBalance"),
                rnrColumn("quantityReceived", true, RnrColumnType.User_Input, "quantityReceived"),
                rnrColumn("lossesAndAdjustments", true, RnrColumnType.User_Input, "lossesAndAdjustments"),
                rnrColumn("quantityRequested", false,  null, "Requested Quantity"),
                rnrColumn("reasonForRequestedQuantity", false , null, "Requested Quantity Reason"),
                rnrColumn("quantityDispensed", false, RnrColumnType.Calculated, "quantityDispensed")));
        assertEquals("\"normalizedConsumption\" is needed if you report \"stockOutDays\"", errors.get("stockOutDays"));
    }

    @Test
    public void shouldReturnValidationErrorWhenOnlyNormalizedConsumptionIsSelectedButNotNumberOfStockOutDays() {
        Map<String, String> errors = rnrTemplateRuleService.validate(asList(
                rnrColumn("stockOutDays", false, RnrColumnType.Calculated, "Number of Stock out days"),
                rnrColumn("normalizedConsumption", true, RnrColumnType.User_Input, "Normalized Consumption"),
                rnrColumn("stockInHand", false, RnrColumnType.Calculated, "stockInHand"),
                rnrColumn("beginningBalance", true, RnrColumnType.User_Input, "beginningBalance"),
                rnrColumn("quantityReceived", true, RnrColumnType.User_Input, "quantityReceived"),
                rnrColumn("lossesAndAdjustments", true, RnrColumnType.User_Input, "lossesAndAdjustments"),
                rnrColumn("quantityRequested", false,  null, "Requested Quantity"),
                rnrColumn("reasonForRequestedQuantity", false , null, "Requested Quantity Reason"),
                rnrColumn("quantityDispensed", false, RnrColumnType.Calculated, "quantityDispensed")));
        assertEquals("User needs to enter \"Number of Stock out days\" to calculate \"Normalized Consumption\"", errors.get("normalizedConsumption"));
    }

    @Test
    public void shouldReturnValidationErrorWhenOnlyRequestedAmountIsSelectedButNotReasonForRequestedAmount() {

        Map<String, String> errors = rnrTemplateRuleService.validate(asList(
                rnrColumn("stockOutDays", false, RnrColumnType.Calculated, "Number of Stock out days"),
                rnrColumn("normalizedConsumption", true, RnrColumnType.User_Input, "Normalized Consumption"),
                rnrColumn("stockInHand", false, RnrColumnType.Calculated, "stockInHand"),
                rnrColumn("beginningBalance", true, RnrColumnType.User_Input, "beginningBalance"),
                rnrColumn("quantityReceived", true, RnrColumnType.User_Input, "quantityReceived"),
                rnrColumn("lossesAndAdjustments", true, RnrColumnType.User_Input, "lossesAndAdjustments"),
                rnrColumn("quantityDispensed", false, RnrColumnType.Calculated, "quantityDispensed"),
                rnrColumn("quantityRequested", true,  null, "Requested Quantity"),
                rnrColumn("reasonForRequestedQuantity", false , null, "Requested Quantity Reason")));
        assertEquals("\"Requested Quantity\" must include an \"Requested Quantity Reason\" from the user", errors.get("quantityRequested"));
    }





    private RnrColumn rnrColumn(String columnName, boolean visible, RnrColumnType selectedColumnType, String label) {
        RnrColumn rnrColumn = new RnrColumn();
        rnrColumn.setName(columnName);
        rnrColumn.setVisible(visible);
        rnrColumn.setSelectedColumnType(selectedColumnType);
        rnrColumn.setLabel(label);
        return rnrColumn;
    }
}
