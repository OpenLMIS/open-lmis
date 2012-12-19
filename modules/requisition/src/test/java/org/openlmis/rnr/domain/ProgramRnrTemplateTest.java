package org.openlmis.rnr.domain;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.*;

public class ProgramRnrTemplateTest {


    public static final String PROGRAM_CODE = "HIV";

    @Test
    public void shouldGiveErrorIfDependentsAreMissing() throws Exception {
        ArrayList<RnrColumn> rnrColumns = new ArrayList<>();
        rnrColumns.add(newRnrColumn(STOCK_IN_HAND, true, "stock in hand"));
        rnrColumns.add(newRnrColumn(QUANTITY_DISPENSED, true, "quantity dispensed"));
        rnrColumns.add(newRnrColumn(QUANTITY_RECEIVED, false, "quantity received"));
        rnrColumns.add(newRnrColumn(BEGINNING_BALANCE, true, "beginning balance"));
        rnrColumns.add(newRnrColumn(LOSSES_AND_ADJUSTMENTS, true, "losses and adjustment"));

        Map<String,String> errors = new ProgramRnrTemplate(PROGRAM_CODE, rnrColumns).validate();

        assertThat(errors.get(STOCK_IN_HAND), is("User needs to enter beginning balance, quantity received, losses and adjustment to validate " +
                "user's entries quantity dispensed and stock in hand"));
    }

    @Test
    public void shouldNotGiveErrorIfDependentsAreNotMissing() throws Exception {
        ArrayList<RnrColumn> rnrColumns = new ArrayList<>();
        rnrColumns.add(newRnrColumn(STOCK_IN_HAND, true, "stock in hand"));
        rnrColumns.add(newRnrColumn(QUANTITY_DISPENSED, true, "quantity dispensed"));
        rnrColumns.add(newRnrColumn(QUANTITY_RECEIVED, true, "quantity received"));
        rnrColumns.add(newRnrColumn(BEGINNING_BALANCE, true, "beginning balance"));
        rnrColumns.add(newRnrColumn(LOSSES_AND_ADJUSTMENTS, true, "losses and adjustment"));

        Map<String,String> errors = new ProgramRnrTemplate(PROGRAM_CODE, rnrColumns).validate();

        assertThat(errors.size(), is(0));
    }

    private RnrColumn newRnrColumn(String columnName, boolean visible, String label) {
        RnrColumn rnrColumn = new RnrColumn();
        rnrColumn.setVisible(visible);
        rnrColumn.setName(columnName);
        rnrColumn.setLabel(label);
        rnrColumn.setFormulaValidated(true);
        return rnrColumn;
    }
}
