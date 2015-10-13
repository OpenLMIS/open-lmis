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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.core.message.OpenLmisMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * ProgramRnrTemplate corresponds to Rnr Template for a program and is a container for columns in that Template.
 */

@NoArgsConstructor
public class ProgramRnrTemplate extends Template {

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
  public static final String SKIPPED = "skipped";
  public static final String CALCULATED_ORDER_QUANTITY = "calculatedOrderQuantity";
  public static final String USER_NEEDS_TO_ENTER_DEPENDENT_FIELD = "user.needs.to.enter.dependent.field";
  public static final String INTERDEPENDENT_FIELDS_CAN_NOT_BE_CALCULATED = "error.interdependent.fields.can.not.be.calculated";
  public static final String COLUMN_SHOULD_BE_VISIBLE_IF_USER_INPUT = "error.column.should.be.visible.if.user.input";
  public static final String USER_NEED_TO_ENTER_REQUESTED_QUANTITY_REASON = "error.user.needs.to.enter.requested.quantity.reason";
  final List<String> nonPrintableFullSupplyColumnNames = asList(REMARKS, REASON_FOR_REQUESTED_QUANTITY);
  final List<String> printableNonFullSupplyColumnNames = asList(PRODUCT, PRODUCT_CODE, DISPENSING_UNIT, QUANTITY_REQUESTED, PACKS_TO_SHIP, PRICE, COST, QUANTITY_APPROVED);
  @Getter
  private Map<String, RnrColumn> rnrColumnsMap = new HashMap<>();
  private Map<String, OpenLmisMessage> errorMap = new HashMap<>();

  @Getter
  @Setter
  private Long modifiedBy;

  @Getter
  @Setter
  private Boolean ApplyDefaultZero = false;

  public ProgramRnrTemplate(Long programId, List<? extends Column> rnrColumns) {
    this.programId = programId;
    this.columns = rnrColumns;

    for (Column rnrColumn : rnrColumns) {
      rnrColumnsMap.put(rnrColumn.getName(), (RnrColumn) rnrColumn);
    }
  }

  public ProgramRnrTemplate(List<? extends Column> programRnrColumns) {
    this.columns = programRnrColumns;
    for (Column rnrColumn : columns) {
      rnrColumnsMap.put(rnrColumn.getName(), (RnrColumn) rnrColumn);
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
    validateQuantityRequested();
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

  private void validateQuantityRequested() {

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

  public List<? extends Column> getPrintableColumns(Boolean fullSupply) {
    List<RnrColumn> printableRnrColumns = new ArrayList<>();

    for (Column rnrColumn : columns) {
      if (rnrColumn.getVisible()) {
        if (fullSupply && !nonPrintableFullSupplyColumnNames.contains(rnrColumn.getName())) {
          printableRnrColumns.add((RnrColumn) rnrColumn);
        } else if (!fullSupply && printableNonFullSupplyColumnNames.contains(rnrColumn.getName())) {
          printableRnrColumns.add((RnrColumn) rnrColumn);
        }
      }
    }
    return printableRnrColumns;
  }

}
