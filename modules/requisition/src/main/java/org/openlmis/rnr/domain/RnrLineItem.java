package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.Money;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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

  private Float amc;
  private Float normalizedConsumption;
  private Integer calculatedOrderQuantity;
  private Integer maxStockQuantity;

  private Integer quantityApproved;

  private Integer packsToShip;
  private String remarks;

  private Integer modifiedBy;
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

  public boolean validate(boolean formulaValidationRequired) {
    if (!validateMandatoryFields() || !validateCalculatedFields(formulaValidationRequired)) {
      throw  new DataException(new OpenLmisMessage(Rnr.RNR_VALIDATION_ERROR));
    }
    return true;
  }

  public void calculate() {
    packsToShip = calculatePacksToShip();
  }

  private boolean validateMandatoryFields() {
    return !(!isPresent(beginningBalance) || !isPresent(quantityReceived) || !isPresent(quantityDispensed) ||
        !isPresent(newPatientCount) || !isPresent(stockOutDays)) && (quantityRequested == null || isPresent(reasonForRequestedQuantity));
  }

  private boolean validateCalculatedFields(boolean formulaValidationRequired) {
    boolean validQuantityDispensed = true;
    if(formulaValidationRequired) {
      validQuantityDispensed = (quantityDispensed == (beginningBalance + quantityReceived + totalLossesAndAdjustments - stockInHand));
    }
    return ((quantityDispensed >= 0) && (stockInHand >= 0)) && validQuantityDispensed &&
        totalLossesAndAdjustments.equals(calculateTotalLossesAndAdjustments()) &&
        (normalizedConsumption.intValue() == (calculateNormalizedConsumption())) &&
        normalizedConsumption.equals(amc) && maxStockQuantity.equals(calculateMaxStockQuantity()) &&
        calculatedOrderQuantity.equals(calculateOrderQuantity());
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
    return maxMonthsOfStock * amc.intValue();
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
}
