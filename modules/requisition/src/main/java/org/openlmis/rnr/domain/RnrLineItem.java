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
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.calculation.RnrCalculationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.valueOf;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.*;
import static org.openlmis.rnr.domain.RnrStatus.AUTHORIZED;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = true)
public class RnrLineItem extends LineItem {

  public static final String RNR_VALIDATION_ERROR = "error.rnr.validation";

  public static final BigDecimal MULTIPLIER = new BigDecimal(3);
  public static final BigDecimal NUMBER_OF_DAYS = new BigDecimal(30);

  //TODO : hack to display it on UI. This is concatenated string of Product properties like name, strength, form and dosage unit
  private String product;
  private Integer productDisplayOrder;
  private String productCode;
  private String productCategory;
  private Integer productCategoryDisplayOrder;
  private Boolean roundToZero;
  private Integer packRoundingThreshold;
  private Integer packSize;
  private Integer dosesPerMonth;
  private Integer dosesPerDispensingUnit;
  private String dispensingUnit;
  private Integer maxMonthsOfStock;
  private Boolean fullSupply;

  private Integer quantityReceived;
  private Integer quantityDispensed;
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
  private Integer calculatedOrderQuantity;
  private Integer maxStockQuantity;
  private Integer quantityApproved;
  private Integer daysSinceLastLineItem;

  private Integer packsToShip;
  private String expirationDate;
  private String remarks;

  private List<Integer> previousNormalizedConsumptions = new ArrayList<>();

  private Money price;
  private Integer total;

  @SuppressWarnings("unused")
  private Boolean skipped = false;

  private static Logger logger = LoggerFactory.getLogger(RnrLineItem.class);

  public RnrLineItem(Long rnrId, FacilityTypeApprovedProduct facilityTypeApprovedProduct, Long modifiedBy, Long createdBy) {
    this.rnrId = rnrId;
    this.maxMonthsOfStock = facilityTypeApprovedProduct.getMaxMonthsOfStock();
    ProgramProduct programProduct = facilityTypeApprovedProduct.getProgramProduct();
    this.price = programProduct.getCurrentPrice();
    ProductCategory category = programProduct.getProduct().getCategory();
    this.productCategory = category.getName();
    this.productCategoryDisplayOrder = category.getDisplayOrder();
    this.populateFromProduct(programProduct.getProduct());
    this.dosesPerMonth = programProduct.getDosesPerMonth();
    this.modifiedBy = modifiedBy;
    this.createdBy = createdBy;
  }

  private void populateFromProduct(Product product) {
    this.productCode = product.getCode();
    this.dispensingUnit = product.getDispensingUnit();
    this.dosesPerDispensingUnit = product.getDosesPerDispensingUnit();
    this.packSize = product.getPackSize();
    this.roundToZero = product.getRoundToZero();
    this.packRoundingThreshold = product.getPackRoundingThreshold();
    this.product = productName(product);
    this.fullSupply = product.getFullSupply();
    this.productDisplayOrder = product.getDisplayOrder();
  }

  private String productName(Product product) {
    return (product.getPrimaryName() == null ? "" : (product.getPrimaryName() + " ")) +
      (product.getForm().getCode() == null ? "" : (product.getForm().getCode() + " ")) +
      (product.getStrength() == null ? "" : (product.getStrength() + " ")) +
      (product.getDosageUnit().getCode() == null ? "" : product.getDosageUnit().getCode());
  }

  public void setFieldsForApproval(RnrCalculationStrategy calcStrategy) {
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
      this.reasonForRequestedQuantity = null;
      this.normalizedConsumption = null;
      this.packsToShip = null;
      this.remarks = null;
      this.expirationDate = null;
    }
    quantityApproved = calcStrategy.calculateDefaultApprovedQuantity(fullSupply, calculatedOrderQuantity, quantityRequested);
  }

  public void setBeginningBalanceWhenPreviousStockInHandAvailable(RnrLineItem lineItem) {
    if (lineItem == null) {
      this.beginningBalance = 0;
      return;
    }
    this.beginningBalance = lineItem.getStockInHand();
  }

  public void setLineItemFieldsAccordingToTemplate(ProgramRnrTemplate template) {
    if (!template.columnsVisible(QUANTITY_RECEIVED)) quantityReceived = 0;
    if (!template.columnsVisible(QUANTITY_DISPENSED)) quantityDispensed = 0;
    totalLossesAndAdjustments = 0;
    newPatientCount = 0;
    stockOutDays = 0;
  }

  public void validateForApproval() {
    if (!skipped && quantityApproved == null) throw new DataException(RNR_VALIDATION_ERROR);
  }

  public void validateMandatoryFields(ProgramRnrTemplate template) {
    String[] nonNullableFields = {BEGINNING_BALANCE, QUANTITY_RECEIVED, STOCK_IN_HAND, QUANTITY_DISPENSED, NEW_PATIENT_COUNT, STOCK_OUT_DAYS};
    for (String fieldName : nonNullableFields) {
      if (template.columnsVisible(fieldName) && !template.columnsCalculated(fieldName) && (getValueFor(fieldName) == null || (Integer) getValueFor(fieldName) < 0)) {
        throw new DataException(RNR_VALIDATION_ERROR);
      }
    }
    requestedQuantityConditionalValidation(template);
  }

  public void validateNonFullSupply() {
    if (!(quantityRequested != null && quantityRequested >= 0 && isPresent(reasonForRequestedQuantity)))
      throw new DataException(RNR_VALIDATION_ERROR);
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

  public void calculateForFullSupply(RnrCalculationStrategy calcStrategy,
                                     ProcessingPeriod period,
                                     ProgramRnrTemplate template,
                                     RnrStatus rnrStatus,
                                     List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
    calculateTotalLossesAndAdjustments(calcStrategy, lossesAndAdjustmentsTypes);

    if (template.columnsCalculated(STOCK_IN_HAND)) {
      calculateStockInHand(calcStrategy);
    }

    if (template.columnsCalculated(QUANTITY_DISPENSED)) {
      calculateQuantityDispensed(calcStrategy);
    }

    calculateNormalizedConsumption(calcStrategy);

    if (rnrStatus == AUTHORIZED) {
      calculateAmc(calcStrategy);
      calculateMaxStockQuantity(calcStrategy);
      calculateOrderQuantity(calcStrategy);
    }

    calculatePacksToShip(calcStrategy);
  }

  public void calculateAmc(RnrCalculationStrategy calcStrategy) {
    amc = calcStrategy.calculateAmc(normalizedConsumption, previousNormalizedConsumptions);
  }

  public void calculatePacksToShip(RnrCalculationStrategy calcStrategy) {
    packsToShip = calcStrategy.calculatePacksToShip(getOrderQuantity(), packSize, packRoundingThreshold, roundToZero);
  }

  public void calculateMaxStockQuantity(RnrCalculationStrategy calcStrategy) {
    maxStockQuantity = calcStrategy.calculateMaxStockQuantity(maxMonthsOfStock, amc);
  }

  public void calculateOrderQuantity(RnrCalculationStrategy calcStrategy) {
    calculatedOrderQuantity = calcStrategy.calculateOrderQuantity(maxStockQuantity, stockInHand);
  }

  public void calculateNormalizedConsumption(RnrCalculationStrategy calcStrategy) {
    normalizedConsumption = calcStrategy.calculateNormalizedConsumption(stockOutDays, quantityDispensed, newPatientCount, dosesPerMonth, dosesPerDispensingUnit, daysSinceLastLineItem);
  }

  public void calculateTotalLossesAndAdjustments(RnrCalculationStrategy calcStrategy, List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
    totalLossesAndAdjustments = calcStrategy.calculateTotalLossesAndAdjustments(lossesAndAdjustments, lossesAndAdjustmentsTypes);
  }

  public void calculateQuantityDispensed(RnrCalculationStrategy calcStrategy) {
    quantityDispensed = calcStrategy.calculateQuantityDispensed(beginningBalance, quantityReceived, totalLossesAndAdjustments, stockInHand);
  }

  public void calculateStockInHand(RnrCalculationStrategy calcStrategy) {
    stockInHand = calcStrategy.calculateStockInHand(beginningBalance, quantityReceived, totalLossesAndAdjustments, quantityDispensed);
  }

  public Money calculateCost() {
    if (packsToShip != null) {
      return price.multiply(valueOf(packsToShip));
    }
    return new Money("0");
  }

  private void copyField(String fieldName, RnrLineItem lineItem, ProgramRnrTemplate template) {
    if (!template.columnsVisible(fieldName) || !template.columnsUserInput(fieldName)) {
      return;
    }

    try {
      Field field = this.getClass().getDeclaredField(fieldName);
      field.set(this, field.get(lineItem));
    } catch (Exception e) {
      logger.error("Error in reading RnrLineItem's field", e);
    }
  }

  private void copyTotalLossesAndAdjustments(RnrLineItem item, ProgramRnrTemplate template) {
    if (template.columnsVisible(LOSSES_AND_ADJUSTMENTS))
      this.totalLossesAndAdjustments = item.totalLossesAndAdjustments;
  }

  public void copyCreatorEditableFieldsForFullSupply(RnrLineItem lineItem, ProgramRnrTemplate template) {
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
    String[] approverEditableFields = {QUANTITY_APPROVED, REMARKS};
    for (String fieldName : approverEditableFields) {
      copyField(fieldName, lineItem, template);
    }
  }

  public void addLossesAndAdjustments(LossesAndAdjustments lossesAndAdjustments) {
    this.lossesAndAdjustments.add(lossesAndAdjustments);
  }

  private void requestedQuantityConditionalValidation(ProgramRnrTemplate template) {
    if (template.columnsVisible(QUANTITY_REQUESTED)
      && quantityRequested != null
      && reasonForRequestedQuantity == null) {
      throw new DataException(RNR_VALIDATION_ERROR);
    }
  }

  private Object getValueFor(String fieldName) {
    Object value = null;
    try {
      Field field = this.getClass().getDeclaredField(fieldName);
      value = field.get(this);
    } catch (Exception e) {
      logger.error("Error in reading RnrLineItem's field", e);
    }
    return value;
  }

  private boolean isPresent(Object value) {
    return value != null;
  }

  private Integer getOrderQuantity() {
    if (quantityApproved != null) return quantityApproved;
    if (quantityRequested != null) return quantityRequested;
    else return calculatedOrderQuantity;
  }

  public void addPreviousNormalizedConsumptionFrom(RnrLineItem rnrLineItem) {
    if (rnrLineItem != null)
      this.previousNormalizedConsumptions.add(rnrLineItem.normalizedConsumption);
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

}
