package org.openlmis.rnr.domain;

import lombok.Getter;
import org.openlmis.core.message.OpenLmisMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class ProgramRnrTemplate {

  public static final String STOCK_IN_HAND = "stockInHand";
  public static final String QUANTITY_DISPENSED = "quantityDispensed";
  public static final String BEGINNING_BALANCE = "beginningBalance";
  public static final String QUANTITY_RECEIVED = "quantityReceived";
  public static final String LOSSES_AND_ADJUSTMENTS = "lossesAndAdjustments";
  public static final String STOCK_OUT_DAYS = "stockOutDays";
  public static final String NORMALIZED_CONSUMPTION = "normalizedConsumption";
  public static final String QUANTITY_REQUESTED = "quantityRequested";
  public static final String REASON_FOR_REQUESTED_QUANTITY = "reasonForRequestedQuantity";

  private Map<String, RnrColumn> rnrColumnsMap = new HashMap<>();
  private Map<String, OpenLmisMessage> errorMap = new HashMap<>();

  @Getter
  private Integer programId;
  @Getter
  private List<RnrColumn> rnrColumns;
  private static final String NOT_ALL_DEPENDENT_FIELDS_SELECTED_TO_ARITHMETICALLY_VALIDATE = "not.all.dependent.fields.for.arithmetical.validation";

  public ProgramRnrTemplate(Integer programId, List<RnrColumn> rnrColumns) {
    this.programId = programId;
    this.rnrColumns = rnrColumns;

    for (RnrColumn rnrColumn : rnrColumns) {
      rnrColumnsMap.put(rnrColumn.getName(), rnrColumn);
    }
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

  private boolean validateDependentsVisible(String columnToEvaluate, List<String> dependents) {
    return !(columnsVisible(columnToEvaluate) && columnsCalculated(columnToEvaluate)) ||
        columnsVisible(dependents.toArray(new String[dependents.size()]));
  }

  private boolean areSelectedTogether(String column1, String column2) {
    return (columnsVisible(column1) && columnsVisible(column2)) || (!columnsVisible(column1) && !columnsVisible(column2));
  }


  public Map<String, OpenLmisMessage> validateToSave() {
    validateDependentFieldsAreSelected();
    validateStockInHandHasAllDependentColumnsChecked();
    validateQuantityDispensedHasAllDependentColumnsChecked();
    validateQuantityDispensedAndStockInHandCannotBeCalculated();
    stockOutDaysAndNormalizedConsumptionShouldBeVisibleTogether();
    quantityRequestedAndReasonForRequestedQuantityBothShouldBeVisibleTogether();
    return errorMap;
  }

  private void validateDependentFieldsAreSelected() {
    if ((columnsVisible(STOCK_IN_HAND, QUANTITY_DISPENSED)) && (!columnsCalculated(STOCK_IN_HAND, QUANTITY_DISPENSED)) && rnrColumnsMap.get(STOCK_IN_HAND).isFormulaValidationRequired()) {
      if (!columnsVisible(BEGINNING_BALANCE, QUANTITY_RECEIVED, LOSSES_AND_ADJUSTMENTS)) {
        errorMap.put(STOCK_IN_HAND,new OpenLmisMessage(NOT_ALL_DEPENDENT_FIELDS_SELECTED_TO_ARITHMETICALLY_VALIDATE,
            getRnrColumnLabelFor(BEGINNING_BALANCE), getRnrColumnLabelFor(QUANTITY_RECEIVED), getRnrColumnLabelFor(LOSSES_AND_ADJUSTMENTS), getRnrColumnLabelFor(QUANTITY_DISPENSED), getRnrColumnLabelFor(STOCK_IN_HAND)));
      }
    }
  }

  private void validateQuantityDispensedAndStockInHandCannotBeCalculated() {

    if (columnsVisible(QUANTITY_DISPENSED) && columnsVisible(STOCK_IN_HAND)) {
      if (columnsCalculated(QUANTITY_DISPENSED) && columnsCalculated(STOCK_IN_HAND)) {
        errorMap.put(QUANTITY_DISPENSED, new OpenLmisMessage("Interdependent fields ('" + getRnrColumnLabelFor(QUANTITY_DISPENSED) + "', '" + getRnrColumnLabelFor(STOCK_IN_HAND) + "') cannot be of type Calculated at the same time"));
        errorMap.put(STOCK_IN_HAND,  new OpenLmisMessage("Interdependent fields ('" + getRnrColumnLabelFor(QUANTITY_DISPENSED) + "', '" + getRnrColumnLabelFor(STOCK_IN_HAND) + "') cannot be of type Calculated at the same time"));
      }
    }
  }

  private void validateStockInHandHasAllDependentColumnsChecked() {
    if (!validateDependentsVisible(STOCK_IN_HAND, asList(BEGINNING_BALANCE, QUANTITY_RECEIVED, LOSSES_AND_ADJUSTMENTS, QUANTITY_DISPENSED))) {
      errorMap.put(STOCK_IN_HAND,  new OpenLmisMessage("User needs to enter '" + getRnrColumnLabelFor(BEGINNING_BALANCE) + "', '" +
          getRnrColumnLabelFor(QUANTITY_RECEIVED) + "', '" +
          getRnrColumnLabelFor(LOSSES_AND_ADJUSTMENTS) + "', '" +
          getRnrColumnLabelFor(QUANTITY_DISPENSED) + "' to calculate '" +
          getRnrColumnLabelFor(STOCK_IN_HAND) + "'"));
    }
  }

  private void validateQuantityDispensedHasAllDependentColumnsChecked() {

    if (!validateDependentsVisible(QUANTITY_DISPENSED, asList(BEGINNING_BALANCE, QUANTITY_RECEIVED, LOSSES_AND_ADJUSTMENTS, STOCK_IN_HAND))) {
      errorMap.put(QUANTITY_DISPENSED,  new OpenLmisMessage("User needs to enter '" + getRnrColumnLabelFor(BEGINNING_BALANCE) + "', '" +
          getRnrColumnLabelFor(QUANTITY_RECEIVED) + "', '" +
          getRnrColumnLabelFor(LOSSES_AND_ADJUSTMENTS) + "', '" +
          getRnrColumnLabelFor(STOCK_IN_HAND) + "' to calculate '" +
          getRnrColumnLabelFor(QUANTITY_DISPENSED) + "'"));
    }
  }

  private void stockOutDaysAndNormalizedConsumptionShouldBeVisibleTogether() {

    if (!areSelectedTogether(STOCK_OUT_DAYS, NORMALIZED_CONSUMPTION)) {
      if (columnsVisible(STOCK_OUT_DAYS)) {
        errorMap.put(STOCK_OUT_DAYS,  new OpenLmisMessage("'" + getRnrColumnLabelFor(NORMALIZED_CONSUMPTION) + "' is needed if you report '" + getRnrColumnLabelFor(STOCK_OUT_DAYS) + "'"));
      } else {
        errorMap.put(NORMALIZED_CONSUMPTION,  new OpenLmisMessage("User needs to enter '" + getRnrColumnLabelFor(STOCK_OUT_DAYS) + "' to calculate '" + getRnrColumnLabelFor(NORMALIZED_CONSUMPTION) + "'"));
      }
    }
  }

  private void quantityRequestedAndReasonForRequestedQuantityBothShouldBeVisibleTogether() {
    if (!areSelectedTogether(QUANTITY_REQUESTED, REASON_FOR_REQUESTED_QUANTITY)) {
      if (columnsVisible(QUANTITY_REQUESTED)) {
        errorMap.put(QUANTITY_REQUESTED,  new OpenLmisMessage("'" + getRnrColumnLabelFor(QUANTITY_REQUESTED) + "' must include an '" + getRnrColumnLabelFor(REASON_FOR_REQUESTED_QUANTITY) + "' from the user"));

      } else {
        errorMap.put(REASON_FOR_REQUESTED_QUANTITY,  new OpenLmisMessage("'" + getRnrColumnLabelFor(QUANTITY_REQUESTED) + "' must include an '" + getRnrColumnLabelFor(REASON_FOR_REQUESTED_QUANTITY) + "' from the user"));
      }
    }
  }


}
