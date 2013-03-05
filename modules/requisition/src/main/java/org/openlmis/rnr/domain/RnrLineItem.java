package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static java.lang.Math.floor;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;
import static org.openlmis.rnr.domain.ProgramRnrTemplate.*;
import static org.openlmis.rnr.domain.RnRColumnSource.USER_INPUT;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = NON_EMPTY)
public class RnrLineItem {

  public static final String RNR_VALIDATION_ERROR = "rnr.validation.error";

  public static final Float MULTIPLIER = 3f;
  public static final Float NUMBER_OF_DAYS = 30f;
  private Integer id;

  private Integer rnrId;

  //todo hack to display it on UI. This is concatenated string of Product properties like name, strength, form and dosage unit
  private String product;
  private String productCode;
  private String productCategory;
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

  private static Logger logger = LoggerFactory.getLogger(RnrLineItem.class);

  public RnrLineItem(Integer rnrId, FacilityApprovedProduct facilityApprovedProduct, Integer modifiedBy) {
    this.rnrId = rnrId;

    this.maxMonthsOfStock = facilityApprovedProduct.getMaxMonthsOfStock();
    ProgramProduct programProduct = facilityApprovedProduct.getProgramProduct();
    this.price = programProduct.getCurrentPrice();
    this.productCategory = programProduct.getProduct().getCategory().getName();
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


  public void calculate(ProcessingPeriod period, List<RnrColumn> rnrColumns) {
    ProgramRnrTemplate template = new ProgramRnrTemplate(rnrColumns);
    calculateNormalizedConsumption();
    calculateAmc(period);
    calculateTotalLossesAndAdjustments();
    calculateMaxStockQuantity();
    if (template.columnsCalculated(STOCK_IN_HAND)) calculateStockInHand();
    if (template.columnsCalculated(QUANTITY_DISPENSED)) calculateQuantityDispensed();
    calculateOrderQuantity();

    calculatePacksToShip();
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

  public void validateMandatoryFields(List<RnrColumn> templateColumns) {
    ProgramRnrTemplate template = new ProgramRnrTemplate(templateColumns);

    String[] nonNullableFields = {BEGINNING_BALANCE, QUANTITY_RECEIVED, QUANTITY_DISPENSED, NEW_PATIENT_COUNT, STOCK_OUT_DAYS};
    boolean valid = true;
    for (String fieldName : nonNullableFields) {
      if (template.columnsVisible(fieldName) && !template.columnsCalculated(fieldName)) {
        try {
          Field field = this.getClass().getDeclaredField(fieldName);
          if (field.get(this) == null) {
            valid = false;
            break;
          }
        } catch (Exception e) {
          logger.error("Error in reading RnrLineItem's field", e);
        }
      }
    }

    valid = valid && (!template.columnsVisible(QUANTITY_REQUESTED, REASON_FOR_REQUESTED_QUANTITY)
      || quantityRequested == null
      || isPresent(reasonForRequestedQuantity));

    if (!valid)
      throw new DataException(new OpenLmisMessage(RNR_VALIDATION_ERROR));

  }

  public void validateNonFullSupply() {
    if (!(quantityRequested != null && quantityRequested >= 0 && isPresent(reasonForRequestedQuantity)))
      throw new DataException(RNR_VALIDATION_ERROR);
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
    packsToShip = orderQuantity == 0 ? 0 : round(floor(orderQuantity / packSize), orderQuantity);
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

  public void copyUserEditableFields(RnrLineItem item, List<RnrColumn> programRnrColumns) {
    ProgramRnrTemplate template = new ProgramRnrTemplate(programRnrColumns);

    copyBeginningBalance(item, template);

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
    if (!template.columnsVisible(LOSSES_AND_ADJUSTMENTS)) totalLossesAndAdjustments = 0;
    newPatientCount = 0;
    stockOutDays = 0;
  }

  private void calculateStockInHand() {
    this.stockInHand = this.beginningBalance + this.quantityReceived + this.totalLossesAndAdjustments - this.quantityDispensed;
  }

  private void calculateQuantityDispensed() {
    this.quantityDispensed = this.beginningBalance + this.quantityReceived + this.totalLossesAndAdjustments - this.stockInHand;
  }

  public Money calculateCost() {
    return price.multiply(BigDecimal.valueOf(packsToShip));
  }
}
