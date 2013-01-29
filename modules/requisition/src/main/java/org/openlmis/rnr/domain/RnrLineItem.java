package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.Money;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.*;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
public class RnrLineItem {

  public static final Float MULTIPLIER = 3f;
  public static final Float NUMBER_OF_DAYS = 30f;
  private Integer id;

  private Integer rnrId;

  //todo hack to display it on UI. This is concatenated string of Product properties like name, strength, form and dosage unit
  private String product;
  private String productCode;
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
  private String remarks;

  private Boolean previousStockInHandAvailable = false;
  @JsonIgnore
  private Integer modifiedBy;

  @JsonIgnore
  private Date modifiedDate;

  private Money price;

  public RnrLineItem(Integer rnrId, FacilityApprovedProduct facilityApprovedProduct, Integer modifiedBy) {
    this.rnrId = rnrId;

    this.maxMonthsOfStock = facilityApprovedProduct.getMaxMonthsOfStock();
    this.price = facilityApprovedProduct.getProgramProduct().getCurrentPrice();
    ProgramProduct programProduct = facilityApprovedProduct.getProgramProduct();
    populateFromProduct(programProduct.getProduct());
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

  public boolean validate(List<RnrColumn> templateColumns) {
    RnrColumn column = templateColumns.get(0);
    if (!validateMandatoryFields(templateColumns) || !validateCalculatedFields(column.isFormulaValidationRequired())) {
      throw new DataException(new OpenLmisMessage(Rnr.RNR_VALIDATION_ERROR));
    }
    return true;
  }

  public void calculate() {
    packsToShip = calculatePacksToShip();
    amc = normalizedConsumption = calculateNormalizedConsumption();
    totalLossesAndAdjustments = calculateTotalLossesAndAdjustments();
    maxStockQuantity = calculateMaxStockQuantity();
    calculatedOrderQuantity = calculateOrderQuantity();
  }

  private boolean validateMandatoryFields(List<RnrColumn> templateColumns) {
    ProgramRnrTemplate template = new ProgramRnrTemplate(1, templateColumns);
    return
      !(
      (template.columnsVisible(BEGINNING_BALANCE) && !isPresent(beginningBalance)) ||
      (template.columnsVisible(QUANTITY_RECEIVED) && !isPresent(quantityReceived)) ||
      (template.columnsVisible(QUANTITY_DISPENSED) && !isPresent(quantityDispensed)) ||
      (template.columnsVisible(NEW_PATIENT_COUNT) && !isPresent(newPatientCount)) ||
      (template.columnsVisible(STOCK_OUT_DAYS) && !isPresent(stockOutDays))
      )
        &&
      (template.columnsVisible(QUANTITY_REQUESTED, REASON_FOR_REQUESTED_QUANTITY) && (quantityRequested == null || isPresent(reasonForRequestedQuantity))
      );
  }

  private boolean validateCalculatedFields(boolean arithmeticValidationRequired) {
    boolean validQuantityDispensed = true;
    if (arithmeticValidationRequired) {
      validQuantityDispensed = (quantityDispensed == (beginningBalance + quantityReceived + totalLossesAndAdjustments - stockInHand));
    }
    return (quantityDispensed >= 0 && stockInHand >= 0 && validQuantityDispensed);
  }

  private boolean isPresent(Object value) {
    return value != null;
  }

  private Integer calculatePacksToShip() {
    Integer orderQuantity = quantityRequested == null ? calculatedOrderQuantity : quantityRequested;
    Double packsToShip = Math.floor(orderQuantity / packSize);
    return rounded(packsToShip);
  }

  private Integer rounded(Double packsToShip) {
    Integer orderQuantity = quantityRequested == null ? calculatedOrderQuantity : quantityRequested;
    Integer remainderQuantity = orderQuantity % packSize;
    if (remainderQuantity >= packRoundingThreshold && packsToShip != 0) {
      packsToShip += 1;
    }

    if (packsToShip == 0 && !roundToZero) {
      packsToShip = 1d;
    }
    return packsToShip.intValue();
  }

  private Integer calculateOrderQuantity() {
    return (maxStockQuantity - stockInHand < 0) ? 0 : maxStockQuantity - stockInHand;
  }

  private Integer calculateMaxStockQuantity() {
    return maxMonthsOfStock * amc;
  }

  private Integer calculateNormalizedConsumption() {
    Float consumptionAdjustedWithStockOutDays = ((MULTIPLIER * NUMBER_OF_DAYS) - stockOutDays) == 0 ? quantityDispensed :
        (quantityDispensed * ((MULTIPLIER * NUMBER_OF_DAYS) / ((MULTIPLIER * NUMBER_OF_DAYS) - stockOutDays)));
    Float adjustmentForNewPatients = (newPatientCount * ((Double) Math.ceil(dosesPerMonth.doubleValue() / dosesPerDispensingUnit)).floatValue()) * MULTIPLIER;

    return Math.round(consumptionAdjustedWithStockOutDays + adjustmentForNewPatients);
  }

  private Integer calculateTotalLossesAndAdjustments() {
    Integer total = 0;
    for (LossesAndAdjustments lossAndAdjustment : lossesAndAdjustments) {
      if (lossAndAdjustment.getType().getAdditive()) {
        total += lossAndAdjustment.getQuantity();
      } else {
        total -= lossAndAdjustment.getQuantity();
      }
    }
    return total;
  }

  public Integer getQuantityApproved() {
    if (quantityApproved != null)
      return quantityApproved;

    return fullSupply ? calculatedOrderQuantity : quantityRequested;
  }

  public void setBeginningBalanceWhenPreviousStockInHandAvailable(Integer beginningBalance){
    this.beginningBalance = beginningBalance;
    this.previousStockInHandAvailable = Boolean.TRUE;
  }
}
