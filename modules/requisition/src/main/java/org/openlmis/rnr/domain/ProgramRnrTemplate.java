/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.domain;

import lombok.Getter;
import lombok.Setter;
import org.openlmis.core.message.OpenLmisMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class ProgramRnrTemplate {

  public static final String STOCK_IN_HAND = "stockInHand";
  public static final String REMARKS = "remarks";
  public static final String QUANTITY_DISPENSED = "quantityDispensed";
  public static final String BEGINNING_BALANCE = "beginningBalance";
  public static final String QUANTITY_RECEIVED = "quantityReceived";
  public static final String QUANTITY_APPROVED = "quantityApproved";
  public static final String LOSSES_AND_ADJUSTMENTS = "lossesAndAdjustments";
  public static final String STOCK_OUT_DAYS = "stockOutDays";
  public static final String NORMALIZED_CONSUMPTION = "normalizedConsumption";
  public static final String QUANTITY_REQUESTED = "quantityRequested";
  public static final String REASON_FOR_REQUESTED_QUANTITY = "reasonForRequestedQuantity";
  public static final String NEW_PATIENT_COUNT = "newPatientCount";
  public static final String COST = "cost";
  public static final String PRICE = "price";
  public static final String TOTAL = "total";
  public static final String PRODUCT = "product";
  public static final String DISPENSING_UNIT = "dispensingUnit";
  public static final String PRODUCT_CODE = "productCode";
  public static final String PACKS_TO_SHIP = "packsToShip";

  final List<String> nonPrintableFullSupplyColumnNames = asList(REMARKS, REASON_FOR_REQUESTED_QUANTITY);
  final List<String> printableNonFullSupplyColumnNames = asList(PRODUCT, PRODUCT_CODE, DISPENSING_UNIT, QUANTITY_REQUESTED, PACKS_TO_SHIP, PRICE, COST, QUANTITY_APPROVED);

  public static final String USER_NEEDS_TO_ENTER_DEPENDENT_FIELD = "user.needs.to.enter.dependent.field";
  public static final String INTERDEPENDENT_FIELDS_CAN_NOT_BE_CALCULATED = "error.interdependent.fields.can.not.be.calculated";
  public static final String COLUMN_SHOULD_BE_VISIBLE_IF_USER_INPUT = "error.column.should.be.visible.if.user.input";
  public static final String USER_NEED_TO_ENTER_REQUESTED_QUANTITY_REASON = "error.user.needs.to.enter.requested.quantity.reason";


  @Getter
  private Map<String, RnrColumn> rnrColumnsMap = new HashMap<>();
  private Map<String, OpenLmisMessage> errorMap = new HashMap<>();

  @Getter
  private Long programId;

  @Getter
  private List<RnrColumn> rnrColumns;

  @Getter
  @Setter
  private Long modifiedBy;

  public ProgramRnrTemplate(Long programId, List<RnrColumn> rnrColumns) {
    this.programId = programId;
    this.rnrColumns = rnrColumns;

    for (RnrColumn rnrColumn : rnrColumns) {
      rnrColumnsMap.put(rnrColumn.getName(), rnrColumn);
    }
  }

  public ProgramRnrTemplate(List<RnrColumn> programRnrColumns) {
    this.rnrColumns = programRnrColumns;
    for (RnrColumn rnrColumn : rnrColumns) {
      rnrColumnsMap.put(rnrColumn.getName(), rnrColumn);
    }
  }

  public boolean columnsVisible(String... rnrColumnNames) {
    boolean visible = true;
    for (String rnrColumnName : rnrColumnNames) {
      visible = (rnrColumnsMap.get(rnrColumnName) != null) && visible && rnrColumnsMap.get(rnrColumnName).getVisible();
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

  public boolean columnsUserInput(String... rnrColumnNames) {
    boolean userInput = false;
    for (String rnrColumnName : rnrColumnNames) {
      userInput = userInput || (rnrColumnsMap.get(rnrColumnName).getSource() == RnRColumnSource.USER_INPUT);
    }
    return userInput;
  }

  public String getRnrColumnLabelFor(String columnName) {
    return rnrColumnsMap.get(columnName).getLabel();
  }

  private boolean areSelectedTogether(String column1, String column2) {
    return (columnsVisible(column1) && columnsVisible(column2)) || (!columnsVisible(column1) && !columnsVisible(column2));
  }


  public Map<String, OpenLmisMessage> validateToSave() {
    validateColumnsTobeCheckedIfUserInput();
    validateCalculatedColumnHasDependentChecked(STOCK_IN_HAND, QUANTITY_DISPENSED);
    validateCalculatedColumnHasDependentChecked(QUANTITY_DISPENSED, STOCK_IN_HAND);
    validateQuantityDispensedAndStockInHandCannotBeCalculatedAtSameTime();
    quantityRequestedAndReasonForRequestedQuantityBothShouldBeVisibleTogether();
    return errorMap;
  }

  private void validateColumnsTobeCheckedIfUserInput() {
    validateColumnToBeCheckedIfUserInput(STOCK_IN_HAND);
    validateColumnToBeCheckedIfUserInput(QUANTITY_DISPENSED);
  }

  private void validateColumnToBeCheckedIfUserInput(String column) {
    if (columnIsUserInput(column) && !columnsVisible(column)) {
      errorMap.put(column, new OpenLmisMessage(COLUMN_SHOULD_BE_VISIBLE_IF_USER_INPUT, getRnrColumnLabelFor(column)));
    }
  }

  private boolean columnIsUserInput(String column) {
    return !columnsCalculated(column);
  }

  private void validateQuantityDispensedAndStockInHandCannotBeCalculatedAtSameTime() {
    if (columnsCalculated(QUANTITY_DISPENSED) && columnsCalculated(STOCK_IN_HAND)) {
      OpenLmisMessage errorMessage = new OpenLmisMessage(INTERDEPENDENT_FIELDS_CAN_NOT_BE_CALCULATED,
        getRnrColumnLabelFor(QUANTITY_DISPENSED),
        getRnrColumnLabelFor(STOCK_IN_HAND));
      errorMap.put(QUANTITY_DISPENSED, errorMessage);
      errorMap.put(STOCK_IN_HAND, errorMessage);
    }
  }

  private void validateCalculatedColumnHasDependentChecked(String columnToEvaluate, String dependent) {
    if (columnsCalculated(columnToEvaluate) && !columnsVisible(dependent)) {
      errorMap.put(columnToEvaluate, new OpenLmisMessage(USER_NEEDS_TO_ENTER_DEPENDENT_FIELD, getRnrColumnLabelFor(dependent), getRnrColumnLabelFor(columnToEvaluate)));
    }
  }

  private void quantityRequestedAndReasonForRequestedQuantityBothShouldBeVisibleTogether() {

    if (!areSelectedTogether(QUANTITY_REQUESTED, REASON_FOR_REQUESTED_QUANTITY)) {

      if (columnsVisible(QUANTITY_REQUESTED)) {
        errorMap.put(QUANTITY_REQUESTED, new OpenLmisMessage(USER_NEED_TO_ENTER_REQUESTED_QUANTITY_REASON,
          getRnrColumnLabelFor(QUANTITY_REQUESTED),
          getRnrColumnLabelFor(REASON_FOR_REQUESTED_QUANTITY)));
      } else {
        errorMap.put(REASON_FOR_REQUESTED_QUANTITY, new OpenLmisMessage(USER_NEED_TO_ENTER_REQUESTED_QUANTITY_REASON,
          getRnrColumnLabelFor(REASON_FOR_REQUESTED_QUANTITY),
          getRnrColumnLabelFor(QUANTITY_REQUESTED)));
      }
    }
  }

  public List<RnrColumn> getPrintableColumns(boolean fullSupply) {
    List<RnrColumn> printableRnrColumns = new ArrayList<>();

    for (RnrColumn rnrColumn : rnrColumns) {
      if (rnrColumn.getVisible()) {
        if (fullSupply && !nonPrintableFullSupplyColumnNames.contains(rnrColumn.getName())) {
          printableRnrColumns.add(rnrColumn);
        } else if (!fullSupply && printableNonFullSupplyColumnNames.contains(rnrColumn.getName())) {
          printableRnrColumns.add(rnrColumn);
        }
      }
    }
    return printableRnrColumns;
  }

}
