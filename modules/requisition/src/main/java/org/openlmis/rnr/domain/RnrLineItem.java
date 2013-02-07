package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.*;
import static org.openlmis.rnr.domain.Rnr.RNR_VALIDATION_ERROR;
import static org.openlmis.rnr.domain.RnrStatus.IN_APPROVAL;

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

  private List<Integer> previousNormalizedConsumptions = new ArrayList<>();

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
      throw new DataException(new OpenLmisMessage(RNR_VALIDATION_ERROR));
    }
    return true;
  }

  public void calculate(ProcessingPeriod period, RnrStatus status) {
    calculateNormalizedConsumption();
    calculateAmc(period);
    calculateTotalLossesAndAdjustments();
    calculateMaxStockQuantity();
    calculateOrderQuantity();
    if (status == IN_APPROVAL) calculatePacksToShipWithQuantityApproved();
    else calculatePacksToShipWithQuantityRequested();
  }

  private void calculateAmc(ProcessingPeriod period) {
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
            (!template.columnsVisible(QUANTITY_REQUESTED, REASON_FOR_REQUESTED_QUANTITY) || (quantityRequested == null || isPresent(reasonForRequestedQuantity))
            );
  }

  private boolean validateCalculatedFields(boolean arithmeticValidationRequired) {
    boolean validQuantityDispensed = true;
    stockInHand = setToZeroIfNotPresent(stockInHand);
    beginningBalance = setToZeroIfNotPresent(beginningBalance);
    quantityReceived = setToZeroIfNotPresent(quantityReceived);
    totalLossesAndAdjustments = setToZeroIfNotPresent(totalLossesAndAdjustments);
    if (arithmeticValidationRequired) {
      validQuantityDispensed = (quantityDispensed == (beginningBalance + quantityReceived + totalLossesAndAdjustments - stockInHand));
    }
    return (quantityDispensed >= 0 && stockInHand >= 0 && validQuantityDispensed);
  }

  private Integer setToZeroIfNotPresent(Integer field) {
    return isPresent(field) ? field : 0;
  }

  private boolean isPresent(Object value) {
    return value != null;
  }

  private void calculatePacksToShipWithQuantityRequested() {
    Integer orderQuantity = quantityRequested == null ? calculatedOrderQuantity : quantityRequested;
    packsToShip = rounded(Math.floor(orderQuantity / packSize));
  }

  private Integer calculatePacksToShipWithQuantityApproved() {
    if (quantityApproved == null) throw new DataException(RNR_VALIDATION_ERROR);

    Double packsToShip = Math.floor(quantityApproved / packSize);
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

  private void calculateOrderQuantity() {
    calculatedOrderQuantity = (maxStockQuantity - stockInHand < 0) ? 0 : maxStockQuantity - stockInHand;
  }

  private void calculateMaxStockQuantity() {
    maxStockQuantity = maxMonthsOfStock * amc;
  }

  private void calculateNormalizedConsumption() {
    Float consumptionAdjustedWithStockOutDays = ((MULTIPLIER * NUMBER_OF_DAYS) - stockOutDays) == 0 ? quantityDispensed :
        (quantityDispensed * ((MULTIPLIER * NUMBER_OF_DAYS) / ((MULTIPLIER * NUMBER_OF_DAYS) - stockOutDays)));
    Float adjustmentForNewPatients = (newPatientCount * ((Double) Math.ceil(dosesPerMonth.doubleValue() / dosesPerDispensingUnit)).floatValue()) * MULTIPLIER;

    normalizedConsumption = Math.round(consumptionAdjustedWithStockOutDays + adjustmentForNewPatients);
  }

  private void calculateTotalLossesAndAdjustments() {
    totalLossesAndAdjustments = 0;
    for (LossesAndAdjustments lossAndAdjustment : lossesAndAdjustments) {
      if (lossAndAdjustment.getType().getAdditive()) {
        totalLossesAndAdjustments += lossAndAdjustment.getQuantity();
      } else {
        totalLossesAndAdjustments -= lossAndAdjustment.getQuantity();
      }
    }
  }

  public void setBeginningBalanceWhenPreviousStockInHandAvailable(Integer beginningBalance) {
    this.beginningBalance = beginningBalance;
    this.previousStockInHandAvailable = Boolean.TRUE;
  }

  public void addPreviousNormalizedConsumptionFrom(RnrLineItem rnrLineItem) {
    if (rnrLineItem != null) this.previousNormalizedConsumptions.add(rnrLineItem.normalizedConsumption);
  }

  public void setDefaultApprovedQuantity() {
    quantityApproved = fullSupply ? calculatedOrderQuantity : quantityRequested;
  }


  public void copyApproverEditableFields(RnrLineItem item) {
    if (item == null) return;
    this.quantityApproved = item.quantityApproved;
    this.remarks = item.remarks;
  }

  public void copyUserEditableFieldsForSaveSubmitOrAuthorize(RnrLineItem item) {
    if (item == null) return;
    this.remarks = item.remarks;
    if (!this.previousStockInHandAvailable)
      this.beginningBalance = item.beginningBalance;
    this.quantityReceived = item.quantityReceived;
    this.quantityDispensed = item.quantityDispensed;
    this.lossesAndAdjustments = item.lossesAndAdjustments;
    this.stockInHand = item.stockInHand;
    this.newPatientCount = item.newPatientCount;
    this.stockOutDays = item.stockOutDays;
    this.quantityRequested = item.quantityRequested;
    this.reasonForRequestedQuantity = item.reasonForRequestedQuantity;
  }
}
