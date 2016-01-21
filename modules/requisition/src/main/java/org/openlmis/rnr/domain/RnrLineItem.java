/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.floor;
import static java.lang.Math.round;
import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;
import static org.apache.commons.collections.CollectionUtils.find;
import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_EMPTY;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.*;
import static org.openlmis.rnr.domain.Rnr.RNR_VALIDATION_ERROR;
import static org.openlmis.rnr.domain.RnrStatus.AUTHORIZED;

/**
 * This class represents the data captured against a product for each Requisition and contains methods to determine
 * normalisedConsumption, averageMonthlyConsumption, stockOutDays, packsToShip, orderQuantity, maxStockQuantity and
 * quantityDispensed of that product.
 */

@Data
@NoArgsConstructor
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = true)
public class RnrLineItem extends LineItem {

  public static final BigDecimal NUMBER_OF_DAYS = new BigDecimal(30);

  public static final MathContext MATH_CONTEXT = new MathContext(12, HALF_UP);
  public static final String DISPENSED_PLUS_NEW_PATIENTS = "DISPENSED_PLUS_NEW_PATIENTS";
  public static final String DISPENSED_X_90 = "DISPENSED_X_90";
  public static final String DEFAULT = "DEFAULT";
  public static final String DISPENSED_X_2 = "DISPENSED_X_2";
  public static final String CONSUMPTION_X_2 = "CONSUMPTION_X_2";
  private static final Logger LOGGER = LoggerFactory.getLogger(RnrLineItem.class);
  //TODO : hack to display it on UI. This is concatenated string of Product properties like name, strength, form and dosage unit
  private String product;
  private Integer productDisplayOrder;
  private String productCode;
  private String productPrimaryName;
  private String productCategory;
  private Integer productCategoryDisplayOrder;
  private String productStrength;
  private Boolean roundToZero;
  private Integer packRoundingThreshold;
  private Integer packSize;
  private Integer dosesPerMonth;
  private Integer dosesPerDispensingUnit;
  private String dispensingUnit;
  private Double maxMonthsOfStock;
  private Boolean fullSupply;
  private Integer quantityReceived;
  private Integer quantityDispensed;
  private Integer previousStockInHand;
  private Integer beginningBalance;
  private List<LossesAndAdjustments> lossesAndAdjustments = new ArrayList<>();
  private Integer totalLossesAndAdjustments = 0;
  private Integer stockInHand;
  private Integer stockOutDays;
  private Integer newPatientCount;
  private Integer quantityRequested;
  private String reasonForRequestedQuantity;
  private Integer amc;
  private Integer normalizedConsumption;
  private Integer periodNormalizedConsumption;
  private Integer calculatedOrderQuantity;
  private Integer maxStockQuantity;
  private Integer quantityApproved;
  private Integer reportingDays;
  private Integer packsToShip;
  private String expirationDate;
  private String remarks;
  private List<Integer> previousNormalizedConsumptions = new ArrayList<>();
  private Money price;
  private Integer total;
  @SuppressWarnings("unused")
  private Boolean skipped = false;

  public RnrLineItem(Long rnrId,
                     FacilityTypeApprovedProduct facilityTypeApprovedProduct,
                     Long modifiedBy,
                     Long createdBy) {
    this.rnrId = rnrId;
    this.maxMonthsOfStock = facilityTypeApprovedProduct.getMaxMonthsOfStock();
    ProgramProduct programProduct = facilityTypeApprovedProduct.getProgramProduct();
    this.price = programProduct.getCurrentPrice();
    ProductCategory category = programProduct.getProductCategory();
    this.productCategory = category.getName();
    this.productCategoryDisplayOrder = category.getDisplayOrder();
    this.populateFromProduct(programProduct);
    this.dosesPerMonth = programProduct.getDosesPerMonth();

    this.modifiedBy = modifiedBy;
    this.createdBy = createdBy;
  }

  public void setFieldsForApproval() {
    if (this.skipped) {
      this.quantityReceived = null;
      this.quantityDispensed = null;
      this.beginningBalance = null;
      this.lossesAndAdjustments = new ArrayList<>();
      this.totalLossesAndAdjustments = 0;
      this.stockInHand = null;
      this.stockOutDays = null;
      this.newPatientCount = null;
      this.quantityRequested = null;
      this.quantityApproved = null;
      this.reasonForRequestedQuantity = null;
      this.normalizedConsumption = null;
      this.periodNormalizedConsumption = null;
      this.packsToShip = null;
      this.remarks = null;
      this.expirationDate = null;
    }
    if(quantityApproved == null){
      quantityApproved = (quantityRequested == null) ? calculatedOrderQuantity : quantityRequested;
    }
  }

  public void setBeginningBalanceWhenPreviousStockInHandAvailable(RnrLineItem previousLineItem,
                                                                  Boolean beginningBalanceVisible) {
    if (previousLineItem == null || (!beginningBalanceVisible && previousLineItem.getSkipped())) {
      this.beginningBalance = 0;
      return;
    }
    this.beginningBalance = previousLineItem.getStockInHand();
    this.previousStockInHand = previousLineItem.getStockInHand();
  }

  public void setSkippedValueWhenPreviousLineItemIsAvailable(RnrLineItem previousLineItem){
    if(previousLineItem != null){
      this.setSkipped(previousLineItem.getSkipped());
    }
  }

  public void setLineItemFieldsAccordingToTemplate(ProgramRnrTemplate template) {
    if (!template.columnsVisible(QUANTITY_RECEIVED)) quantityReceived = 0;
    if (!template.columnsVisible(QUANTITY_DISPENSED)) quantityDispensed = 0;
    totalLossesAndAdjustments = 0;
    newPatientCount = 0;
    stockOutDays = 0;

    if(template.getApplyDefaultZero()){
      quantityReceived = quantityDispensed = stockInHand = calculatedOrderQuantity = 0;

      if(beginningBalance == null){
        beginningBalance = 0;
      } else{
        stockInHand = beginningBalance;
      }
    }


    totalLossesAndAdjustments = newPatientCount = stockOutDays = 0;
  }

  public void validateForApproval() {
    if (!skipped && quantityApproved == null) throw new DataException(RNR_VALIDATION_ERROR);
  }

  public void validateMandatoryFields(ProgramRnrTemplate template) {
    String[] nonNullableFields = {BEGINNING_BALANCE, QUANTITY_RECEIVED, STOCK_IN_HAND,
      QUANTITY_DISPENSED, NEW_PATIENT_COUNT, STOCK_OUT_DAYS};

    for (String fieldName : nonNullableFields) {
      if (template.columnsVisible(fieldName) &&
        !template.columnsCalculated(fieldName) &&
        (getValueFor(fieldName) == null || (Integer) getValueFor(fieldName) < 0)) {
        throw new DataException(RNR_VALIDATION_ERROR);
      }
    }
    requestedQuantityConditionalValidation(template);
  }

  public void validateNonFullSupply() {
    if (!(quantityRequested != null && quantityRequested >= 0 && reasonForRequestedQuantity != null)) {
      throw new DataException(RNR_VALIDATION_ERROR);
    }
  }

  public void validateCalculatedFields(ProgramRnrTemplate template) {
    boolean validQuantityDispensed = true;

    RnrColumn rnrColumn = (RnrColumn) template.getColumns().get(0);

    if (rnrColumn.isFormulaValidationRequired()) {
      validQuantityDispensed = (quantityDispensed == (beginningBalance + quantityReceived + totalLossesAndAdjustments - stockInHand));
    }
    boolean valid = quantityDispensed >= 0 && stockInHand >= 0 && validQuantityDispensed;

    if (!valid) throw new DataException(RNR_VALIDATION_ERROR);
  }

  public void calculateForFullSupply(ProgramRnrTemplate template,
                                     RnrStatus rnrStatus,
                                     List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes, Integer numberOfMonths) {
    calculateTotalLossesAndAdjustments(lossesAndAdjustmentsTypes);

    if (template.columnsCalculated(STOCK_IN_HAND)) {
      calculateStockInHand();
    }

    if (template.columnsCalculated(QUANTITY_DISPENSED)) {
      calculateQuantityDispensed();
    }

    calculateNormalizedConsumption(template);

    calculatePeriodNormalizedConsumption(numberOfMonths);

    if (rnrStatus == AUTHORIZED) {
      calculateAmc(numberOfMonths);
      calculateMaxStockQuantity(template);
      if (!(template.getRnrColumnsMap().get(CALCULATED_ORDER_QUANTITY) != null && template.columnsUserInput(CALCULATED_ORDER_QUANTITY))) {
        calculateOrderQuantity();
      }
    }

    calculatePacksToShip();
  }

  public void calculatePeriodNormalizedConsumption(Integer numberOfMonths) {
    periodNormalizedConsumption = normalizedConsumption * numberOfMonths;
  }

  public void calculateAmc(Integer numberOfMonths) {
    Integer sumOfNCs = normalizedConsumption;
    for (Integer previousNC : previousNormalizedConsumptions) {
      sumOfNCs += previousNC;
    }

    BigDecimal countOfNCs = new BigDecimal((previousNormalizedConsumptions.size() + 1) * numberOfMonths);

    amc = new BigDecimal(sumOfNCs).divide(countOfNCs, MATH_CONTEXT).setScale(0, HALF_UP).intValue();
  }

  public void calculatePacksToShip() {
    Integer orderQuantity = getOrderQuantity();
    if (allNotNull(orderQuantity, packSize)) {
      packsToShip = ((orderQuantity == 0) ? (roundToZero ? 0 : 1) : applyRoundingRules(orderQuantity));
    }
  }

  public void calculateMaxStockQuantity( ProgramRnrTemplate template) {
    RnrColumn column = template.getRnrColumnsMap().get("maxStockQuantity");
    String columnOption = DEFAULT;
    if(column != null){
      columnOption = column.getCalculationOption();
    }
    if(CONSUMPTION_X_2.equalsIgnoreCase(columnOption)){
      maxStockQuantity = this.normalizedConsumption * 2;
    }else if(DISPENSED_X_2.equalsIgnoreCase(columnOption)){
      maxStockQuantity = this.quantityDispensed * 2;
    } else{
      // apply the default calculation if there was no other calculation that works here
      maxStockQuantity = (int) round(maxMonthsOfStock * amc);
    }
  }

  public void calculateOrderQuantity() {
    if (allNotNull(maxStockQuantity, stockInHand)) {
      calculatedOrderQuantity = ((maxStockQuantity - stockInHand) < 0) ? 0 : maxStockQuantity - stockInHand;
    }
  }

  public void calculateNormalizedConsumption(ProgramRnrTemplate template) {

    prepareFieldsForCalculation();
    RnrColumn column = template.getRnrColumnsMap().get("normalizedConsumption");
    String selectedColumnOption = DEFAULT;
    if (column != null) {
      selectedColumnOption = column.getCalculationOption();
    }
    if (DISPENSED_PLUS_NEW_PATIENTS.equalsIgnoreCase(selectedColumnOption)) {
      normalizedConsumption = quantityDispensed + newPatientCount;
    } else if (DISPENSED_X_90.equalsIgnoreCase(selectedColumnOption)) {
      if (stockOutDays < 90) {
        normalizedConsumption = (new BigDecimal(90 * quantityDispensed)
            .divide(
                new BigDecimal(90 - stockOutDays)
                , MATH_CONTEXT)
        ).intValue();
      } else {
        normalizedConsumption = (90 * quantityDispensed);
      }
    } else {
      BigDecimal dosesPerDispensingUnit = new BigDecimal(Math.max(1, this.dosesPerDispensingUnit));

      normalizedConsumption = calculateNormalizedConsumption(
          new BigDecimal(stockOutDays),
          new BigDecimal(quantityDispensed),
          new BigDecimal(newPatientCount),
          new BigDecimal(dosesPerMonth), dosesPerDispensingUnit, reportingDays, template);
    }
  }

  private void prepareFieldsForCalculation() {
    // prepare fields for calculation
    if(stockOutDays == null){
      stockOutDays = 0;
    }
    if(newPatientCount == null){
      newPatientCount = 0;
    }
  }

  public void calculateTotalLossesAndAdjustments(List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
    if (lossesAndAdjustments.isEmpty()) {
      return;
    }
    Integer total = 0;
    for (LossesAndAdjustments lossAndAdjustment : lossesAndAdjustments) {
      if (getAdditive(lossAndAdjustment, lossesAndAdjustmentsTypes)) {
        total += lossAndAdjustment.getQuantity();
      } else {
        total -= lossAndAdjustment.getQuantity();
      }
    }
    totalLossesAndAdjustments = total;
  }

  public void calculateQuantityDispensed() {
    if (allNotNull(beginningBalance, quantityReceived, totalLossesAndAdjustments, stockInHand)) {
      quantityDispensed = beginningBalance + quantityReceived + totalLossesAndAdjustments - stockInHand;
    }
  }

  public void calculateStockInHand() {
    stockInHand = beginningBalance + quantityReceived + totalLossesAndAdjustments - quantityDispensed;
  }

  public Money calculateCost() {
    if (packsToShip != null) {
      return price.multiply(valueOf(packsToShip));
    }
    return new Money("0");
  }

  public void copyCreatorEditableFieldsForFullSupply(RnrLineItem lineItem, ProgramRnrTemplate template) {
    this.previousStockInHand = lineItem.previousStockInHand;
    copyTotalLossesAndAdjustments(lineItem, template);
    for (Column column : template.getColumns()) {
      String fieldName = column.getName();
      if (fieldName.equals(QUANTITY_APPROVED)) continue;
      copyField(fieldName, lineItem, template);
    }
  }

  public void copyCreatorEditableFieldsForNonFullSupply(RnrLineItem lineItem, ProgramRnrTemplate template) {
    String[] editableFields = {QUANTITY_REQUESTED, REMARKS, REASON_FOR_REQUESTED_QUANTITY};

    for (String fieldName : editableFields) {
      copyField(fieldName, lineItem, template);
    }
  }

  public void copyApproverEditableFields(RnrLineItem lineItem, ProgramRnrTemplate template) {
    String[] approverEditableFields = {QUANTITY_APPROVED, REMARKS, SKIPPED};

    for (String fieldName : approverEditableFields) {
      copyField(fieldName, lineItem, template);
    }
  }

  public void addLossesAndAdjustments(LossesAndAdjustments lossesAndAdjustments) {
    this.lossesAndAdjustments.add(lossesAndAdjustments);
  }

  private Integer calculateNormalizedConsumption(BigDecimal stockOutDays,
                                                 BigDecimal quantityDispensed,
                                                 BigDecimal newPatientCount,
                                                 BigDecimal dosesPerMonth,
                                                 BigDecimal dosesPerDispensingUnit,
                                                 Integer reportingDays,
                                                 ProgramRnrTemplate template) {

    BigDecimal newPatientFactor;
    if (template.getRnrColumnsMap().get("newPatientCount").getConfiguredOption() != null && template.getRnrColumnsMap().get("newPatientCount").getConfiguredOption().getName().equals("newPatientCount")) {
      newPatientFactor = newPatientCount.multiply(dosesPerMonth.divide(dosesPerDispensingUnit, MATH_CONTEXT)
        .setScale(0, HALF_UP));
    } else {
      newPatientFactor = newPatientCount;
    }

    if (reportingDays == null || stockOutDays.compareTo(new BigDecimal(reportingDays)) >= 0) {
      return quantityDispensed.add(newPatientFactor).setScale(0, HALF_UP).intValue();
    }

    BigDecimal stockOutFactor = quantityDispensed.multiply(NUMBER_OF_DAYS
      .divide((new BigDecimal(reportingDays).subtract(stockOutDays)), MATH_CONTEXT));

    return stockOutFactor.add(newPatientFactor).setScale(0, HALF_UP).intValue();
  }

  private void copyField(String fieldName, RnrLineItem lineItem, ProgramRnrTemplate template) {
    if (!template.columnsVisible(fieldName) || !template.columnsUserInput(fieldName)) {
      return;
    }

    try {
      Field field = this.getClass().getDeclaredField(fieldName);
      field.set(this, field.get(lineItem));
    } catch (Exception e) {
      LOGGER.error("Error in copying RnrLineItem's field", e);
    }
  }

  private void copyTotalLossesAndAdjustments(RnrLineItem item, ProgramRnrTemplate template) {
    if (template.columnsVisible(LOSSES_AND_ADJUSTMENTS))
      this.totalLossesAndAdjustments = item.totalLossesAndAdjustments;
  }

  private void populateFromProduct(ProgramProduct programProduct) {
    Product product = programProduct.getProduct();
    this.productCode = product.getCode();
    this.dispensingUnit = product.getDispensingUnit();
    this.dosesPerDispensingUnit = product.getDosesPerDispensingUnit();
    this.packSize = product.getPackSize();
    this.roundToZero = product.getRoundToZero();
    this.packRoundingThreshold = product.getPackRoundingThreshold();
    this.product = product.getName();
    // use the program product setting instead of the one on the product table.
    this.fullSupply = programProduct.isFullSupply();
    this.productDisplayOrder = programProduct.getDisplayOrder();
  }

  private void requestedQuantityConditionalValidation(ProgramRnrTemplate template) {
    if (template.columnsVisible(QUANTITY_REQUESTED)
      && quantityRequested != null
      && StringUtils.isEmpty(reasonForRequestedQuantity)) {
      throw new DataException(RNR_VALIDATION_ERROR);
    }
  }

  private Object getValueFor(String fieldName) {
    Object value = null;
    try {
      Field field = this.getClass().getDeclaredField(fieldName);
      value = field.get(this);
    } catch (Exception e) {
      LOGGER.error("Error in reading RnrLineItem's field", e);
    }
    return value;
  }

  @Override
  public boolean compareCategory(LineItem lineItem) {
    return this.getProductCategory().equals(((RnrLineItem) lineItem).getProductCategory());
  }

  @Override
  public String getCategoryName() {
    return this.productCategory;
  }

  @Override
  public String getValue(String columnName) throws NoSuchFieldException, IllegalAccessException {
    if (columnName.equals("lossesAndAdjustments")) {
      return this.getTotalLossesAndAdjustments().toString();
    }
    if (columnName.equals("cost")) {
      return this.calculateCost().toString();
    }
    if (columnName.equals("price")) {
      return this.getPrice().toString();
    }

    if (columnName.equals("total") && this.getBeginningBalance() != null && this.getQuantityReceived() != null) {
      return String.valueOf((this.getBeginningBalance() + this.getQuantityReceived()));
    }

    Field field = RnrLineItem.class.getDeclaredField(columnName);
    field.setAccessible(true);
    Object fieldValue = field.get(this);

    return (fieldValue == null) ? "" : fieldValue.toString();
  }

  @Override
  public boolean isRnrLineItem() {
    return true;
  }

  private Integer getOrderQuantity() {
    if (quantityApproved != null) return quantityApproved;
    if (quantityRequested != null) return quantityRequested;
    else return calculatedOrderQuantity;
  }

  private Integer applyRoundingRules(Integer orderQuantity) {
    Double packsToShip = floor(orderQuantity / packSize);
    Integer remainderQuantity = orderQuantity % packSize;
    if (remainderQuantity >= packRoundingThreshold) {
      packsToShip += 1;
    }

    if (packsToShip == 0 && !roundToZero) {
      packsToShip = 1d;
    }
    return packsToShip.intValue();
  }

  private boolean allNotNull(Integer... fields) {
    for (Integer field : fields) {
      if (field == null) return false;
    }
    return true;
  }

  private Boolean getAdditive(final LossesAndAdjustments lossAndAdjustment,
                              List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
    Predicate predicate = new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        return lossAndAdjustment.getType().getName().equals(((LossesAndAdjustmentsType) o).getName());
      }
    };

    LossesAndAdjustmentsType lossAndAdjustmentTypeFromList = (LossesAndAdjustmentsType) find(lossesAndAdjustmentsTypes,
      predicate);

    return lossAndAdjustmentTypeFromList.getAdditive();
  }

}
