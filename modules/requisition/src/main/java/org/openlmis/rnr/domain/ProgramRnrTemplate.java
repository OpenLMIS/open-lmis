package org.openlmis.rnr.domain;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgramRnrTemplate {

    public static final String STOCK_IN_HAND = "stockInHand";
    public static final String QUANTITY_DISPENSED = "quantityDispensed";
    public static final String BEGINNING_BALANCE = "beginningBalance";
    public static final String QUANTITY_RECEIVED = "quantityReceived";
    public static final String LOSSES_AND_ADJUSTMENTS = "lossesAndAdjustments";
    private Map<String, RnrColumn> rnrColumnsMap = new HashMap<>();
    private Map<String, String> errorMap = new HashMap<>();

    @Getter
    private String programCode;
    @Getter
    private List<RnrColumn> rnrColumns;

    public ProgramRnrTemplate(String programCode, List<RnrColumn> rnrColumns) {
        this.programCode = programCode;
        this.rnrColumns = rnrColumns;

        for (RnrColumn rnrColumn : rnrColumns) {
            rnrColumnsMap.put(rnrColumn.getName(), rnrColumn);
        }
    }

    public RnrColumn rnrColumn(String name) {
        return rnrColumnsMap.get(name);
    }

    public boolean columnsVisible(String... rnrColumnNames) {
        boolean visible = true;
        for (String rnrColumnName : rnrColumnNames) {
            visible = visible && rnrColumnsMap.get(rnrColumnName).isVisible();
        }
        return visible;
    }

    public boolean columnsCalculated(String... rnrColumnNames) {
        boolean calculated = false;
        for (String rnrColumnName : rnrColumnNames) {
            calculated = calculated || (rnrColumnsMap.get(rnrColumnName).getSource() == RnRColumnSource.CALCULATED);
        }
        return calculated;
    }

    public String getRnrColumnLabelFor(String columnName) {
        return rnrColumnsMap.get(columnName).getLabel();
    }

    public Map<String,String> validate() {
        validateDependentFieldsAreSelected();
        return errorMap;
    }

    private void validateDependentFieldsAreSelected() {
        if((columnsVisible(STOCK_IN_HAND, QUANTITY_DISPENSED)) && (!columnsCalculated(STOCK_IN_HAND, QUANTITY_DISPENSED)) && rnrColumnsMap.get(STOCK_IN_HAND).isFormulaValidated()){
            if(!columnsVisible(BEGINNING_BALANCE, QUANTITY_RECEIVED, LOSSES_AND_ADJUSTMENTS)){
                    errorMap.put(STOCK_IN_HAND,"User needs to enter " +
                        getRnrColumnLabelFor(BEGINNING_BALANCE) + ", " +
                        getRnrColumnLabelFor(QUANTITY_RECEIVED) + ", " +
                        getRnrColumnLabelFor(LOSSES_AND_ADJUSTMENTS) +" to validate user's entries " +
                        getRnrColumnLabelFor(QUANTITY_DISPENSED) + " and "+
                        getRnrColumnLabelFor(STOCK_IN_HAND));
            }

        }
    }
}
