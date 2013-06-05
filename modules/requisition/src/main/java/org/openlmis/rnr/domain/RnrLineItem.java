/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static java.lang.Math.floor;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.*;
import static org.openlmis.rnr.domain.RnRColumnSource.USER_INPUT;
import static org.openlmis.rnr.domain.RnrStatus.AUTHORIZED;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
@EqualsAndHashCode(callSuper = false)
public class RnrLineItem extends BaseModel{

  public static final String RNR_VALIDATION_ERROR = "rnr.validation.error";

  public static final Float MULTIPLIER = 3f;
  public static final Float NUMBER_OF_DAYS = 30f;

  private Long rnrId;

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
  private String reasonForLossesAndAdjustments;
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

  private Integer packsToShip;
  private String expirationDate;
  private String remarks;

  private List<Integer> previousNormalizedConsumptions = new ArrayList<>();

  private Boolean previousStockInHandAvailable = false;

  private Money price;

  private static Logger logger = LoggerFactory.getLogger(RnrLineItem.class);

  public RnrLineItem(Long rnrId, FacilityApprovedProduct facilityApprovedProduct, Long modifiedBy) {
    this.rnrId = rnrId;

    this.maxMonthsOfStock = facilityApprovedProduct.getMaxMonthsOfStock();
    ProgramProduct programProduct = facilityApprovedProduct.getProgramProduct();
    this.price = programProduct.getCurrentPrice();
    ProductCategory category = programProduct.getProduct().getCategory();
    this.productCategory = category.getName();
    this.productCategoryDisplayOrder = category.getDisplayOrder();
    this.populateFromProduct(programProduct.getProduct());
    this.dosesPerMonth = programProduct.getDosesPerMonth();
    this.modifiedBy = modifiedBy;
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

  public void addLossesAndAdjustments(LossesAndAdjustments lossesAndAdjustments) {
    this.lossesAndAdjustments.add(lossesAndAdjustments);
  }


  public void calculate(ProcessingPeriod period, List<RnrColumn> rnrColumns, RnrStatus rnrStatus, List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);
    calculateTotalLossesAndAdjustments(lossesAndAdjustmentsTypes);
    if (template.columnsCalculated(STOCK_IN_HAND)) calculateStockInHand();
    if (template.columnsCalculated(QUANTITY_DISPENSED))
      calculateQuantityDispensed();
    calculateNormalizedConsumption();
    if (rnrStatus == AUTHORIZED) {
      calculateAmc(period);
      calculateMaxStockQuantity();
      calculateOrderQuantity();
    }

    calculatePacksToShip();
  }

  public void calculateAmc(ProcessingPeriod period) {
    int denominator = period.getNumberOfMonths() * (1 + previousNormalizedConsumptions.size());
    amc = Math.round(((float) normalizedConsumption + sumOfPreviousNormalizedConsumptions()) / denominator);
  }

  private Integer sumOfPreviousNormalizedConsumptions() {
    Integer total = 0;
    for (Integer consumption : previousNormalizedConsumptions) {
      total += consumption;
    }
    return total;
  }

  public void validateMandatoryFields(List<RnrColumn> templateColumns) {
    ProgramRnrTemplate template = new ProgramRnrTemplate(templateColumns);

    String[] nonNullableFields = {BEGINNING_BALANCE, QUANTITY_RECEIVED, QUANTITY_DISPENSED, NEW_PATIENT_COUNT, STOCK_OUT_DAYS};
    for (String fieldName : nonNullableFields) {
      if (template.columnsVisible(fieldName) && !template.columnsCalculated(fieldName)) {
        if (getValueFor(fieldName) == null) {
          throw new DataException(RNR_VALIDATION_ERROR);
        }
      }
    }

    requestedQuantityConditionalValidation(template);
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

  public void validateNonFullSupply() {
    if (!(quantityRequested != null && quantityRequested >= 0 && isPresent(reasonForRequestedQuantity)))
      throw new DataException(RNR_VALIDATION_ERROR);
    quantityApproved = quantityRequested;
  }

  public void validateCalculatedFields(List<RnrColumn> rnrColumns) {
    boolean validQuantityDispensed = true;
    if (rnrColumns.get(0).isFormulaValidationRequired()) {
      validQuantityDispensed = (quantityDispensed == (beginningBalance + quantityReceived + totalLossesAndAdjustments - stockInHand));
    }
    boolean valid = quantityDispensed >= 0 && stockInHand >= 0 && validQuantityDispensed;
    if (!valid) throw new DataException(RNR_VALIDATION_ERROR);
  }

  private boolean isPresent(Object value) {
    return value != null;
  }

  public void calculatePacksToShip() {
    Integer orderQuantity = getOrderQuantity();
    if (orderQuantity == null || packSize == null) {
      packsToShip = null;
    } else {
      packsToShip = orderQuantity == 0 ? 0 : round(floor(orderQuantity / packSize), orderQuantity);
    }
  }

  private Integer getOrderQuantity() {
    if (quantityApproved != null) return quantityApproved;
    if (quantityRequested != null) return quantityRequested;
    else return calculatedOrderQuantity;
  }

  private Integer round(Double packsToShip, Integer orderQuantity) {
    Integer remainderQuantity = orderQuantity % packSize;
    if (remainderQuantity >= packRoundingThreshold && packsToShip != 0) {
      packsToShip += 1;
    }

    if (packsToShip == 0 && !roundToZero) {
      packsToShip = 1d;
    }
    return packsToShip.intValue();
  }

  public void calculateOrderQuantity() {
    if (isAnyNull(maxStockQuantity, stockInHand)) {
      calculatedOrderQuantity = null;
    } else {
      calculatedOrderQuantity = (maxStockQuantity - stockInHand < 0) ? 0 : maxStockQuantity - stockInHand;
    }
  }

  public void calculateMaxStockQuantity() {
    maxStockQuantity = maxMonthsOfStock * amc;
  }

  public void calculateNormalizedConsumption() {
    Float consumptionAdjustedWithStockOutDays = ((MULTIPLIER * NUMBER_OF_DAYS) - stockOutDays) == 0 ? quantityDispensed :
      (quantityDispensed * ((MULTIPLIER * NUMBER_OF_DAYS) / ((MULTIPLIER * NUMBER_OF_DAYS) - stockOutDays)));
    Float adjustmentForNewPatients = (newPatientCount * ((Double) Math.ceil(
      dosesPerMonth.doubleValue() / dosesPerDispensingUnit)).floatValue()) * MULTIPLIER;

    normalizedConsumption = Math.round(consumptionAdjustedWithStockOutDays + adjustmentForNewPatients);
  }

  public void calculateTotalLossesAndAdjustments(List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
    totalLossesAndAdjustments = 0;
    for (LossesAndAdjustments lossAndAdjustment : lossesAndAdjustments) {
      if (getAdditive(lossAndAdjustment, lossesAndAdjustmentsTypes)) {
        totalLossesAndAdjustments += lossAndAdjustment.getQuantity();
      } else {
        totalLossesAndAdjustments -= lossAndAdjustment.getQuantity();
      }
    }
  }

  private boolean getAdditive(final LossesAndAdjustments lossAndAdjustment, List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
    Predicate predicate = new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        return lossAndAdjustment.getType().getName().equals(((LossesAndAdjustmentsType) o).getName());
      }
    };

    LossesAndAdjustmentsType lossAndAdjustmentTypeFromList = (LossesAndAdjustmentsType) CollectionUtils.find(
      lossesAndAdjustmentsTypes, predicate);

    return lossAndAdjustmentTypeFromList.getAdditive();
  }

  public void addPreviousNormalizedConsumptionFrom(RnrLineItem rnrLineItem) {
    if (rnrLineItem != null)
      this.previousNormalizedConsumptions.add(rnrLineItem.normalizedConsumption);
  }

  public void setDefaultApprovedQuantity() {
    quantityApproved = fullSupply ? calculatedOrderQuantity : quantityRequested;
  }


  public void copyApproverEditableFields(RnrLineItem item) {
    if (item == null) return;
    this.quantityApproved = item.quantityApproved;
    calculatePacksToShip();
    this.remarks = item.remarks;
  }

  public void copyUserEditableFields(RnrLineItem item, List<RnrColumn> programRnrColumns) {
    ProgramRnrTemplate template = new ProgramRnrTemplate(programRnrColumns);

    copyBeginningBalance(item, template);

    copyTotalLossesAndAdjustments(item, template);

    for (RnrColumn column : template.getRnrColumns()) {
      if (!column.isVisible() || column.getSource() != USER_INPUT || column.getName().equals(QUANTITY_APPROVED)) {
        continue;
      }

      try {
        Field field = this.getClass().getDeclaredField(column.getName());
        field.set(this, field.get(item));
      } catch (Exception e) {
        logger.error("Error in reading RnrLineItem's field", e);
      }
    }
  }

  private void copyTotalLossesAndAdjustments(RnrLineItem item, ProgramRnrTemplate template) {
    if (template.columnsVisible(LOSSES_AND_ADJUSTMENTS))
      this.totalLossesAndAdjustments = item.totalLossesAndAdjustments;
  }

  private void copyBeginningBalance(RnrLineItem item, ProgramRnrTemplate template) {
    if (!this.previousStockInHandAvailable && template.columnsVisible(BEGINNING_BALANCE))
      this.beginningBalance = item.beginningBalance;
  }

  public void setBeginningBalanceWhenPreviousStockInHandAvailable(RnrLineItem lineItem) {
    if (lineItem == null) {
      this.beginningBalance = 0;
      return;
    }
    this.beginningBalance = lineItem.getStockInHand();
    this.setPreviousStockInHandAvailable(TRUE);
  }

  void setLineItemFieldsAccordingToTemplate(ProgramRnrTemplate template) {
    if (!template.columnsVisible(QUANTITY_RECEIVED)) quantityReceived = 0;
    if (!template.columnsVisible(QUANTITY_DISPENSED)) quantityDispensed = 0;
    if (!template.columnsVisible(LOSSES_AND_ADJUSTMENTS))
      totalLossesAndAdjustments = 0;
    newPatientCount = 0;
    stockOutDays = 0;
  }

  public void calculateStockInHand() {
    this.stockInHand = this.beginningBalance + this.quantityReceived + this.totalLossesAndAdjustments - this.quantityDispensed;
  }

  public void calculateQuantityDispensed() {
    if (isAnyNull(beginningBalance, quantityReceived, totalLossesAndAdjustments, stockInHand)) {
      quantityDispensed = null;
    } else {
      this.quantityDispensed = this.beginningBalance + this.quantityReceived + this.totalLossesAndAdjustments - this.stockInHand;
    }
  }

  public Money calculateCost() {
    if (packsToShip != null) {
      return price.multiply(BigDecimal.valueOf(packsToShip));
    }
    return new Money("0");
  }

  private boolean isAnyNull(Integer... fields) {
    for (Integer field : fields) {
      if (field == null) return true;
    }
    return false;
  }

  public void validateForApproval() {
    if (quantityApproved == null) throw new DataException(RNR_VALIDATION_ERROR);
  }

}
