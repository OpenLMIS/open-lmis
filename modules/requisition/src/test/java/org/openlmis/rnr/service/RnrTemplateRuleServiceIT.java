package org.openlmis.rnr.service;


import org.junit.Test;
import org.junit.runner.RunWith;
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
                rnrColumn("quantityDispensed", true),
                rnrColumn("beginningBalance", true),
                rnrColumn("quantityReceived", true),
                rnrColumn("lossesAndAdjustments", true),
                rnrColumn("stockInHand", false)));
        assertEquals("one of {beginningBalance|quantityReceived|lossesAndAdjustments|stockInHand} is not checked", errors.get("quantityDispensed"));
    }

    private RnrColumn rnrColumn(String columnName, boolean visible) {
        RnrColumn rnrColumn = new RnrColumn();
        rnrColumn.setName(columnName);
        rnrColumn.setVisible(visible);
        return rnrColumn;
    }
}
