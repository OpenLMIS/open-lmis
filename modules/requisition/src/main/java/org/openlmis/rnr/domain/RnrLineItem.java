package org.openlmis.rnr.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.FacilityApprovedProduct;
import org.openlmis.core.domain.Product;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.exception.DataException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
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

  private Float cost;
  private Integer packsToShip;
  private String remarks;

  private Integer modifiedBy;
  private Date modifiedDate;
  private Float price;

  public RnrLineItem(Integer rnrId, FacilityApprovedProduct facilityApprovedProduct, Integer modifiedBy) {
    this.rnrId = rnrId;

    this.maxMonthsOfStock = facilityApprovedProduct.getMaxMonthsOfStock();
    ProgramProduct programProduct = facilityApprovedProduct.getProgramProduct();
    // TODO : ugly
    Product product = programProduct.getProduct();
    this.productCode = product.getCode();
    this.dispensingUnit = product.getDispensingUnit();
    this.dosesPerDispensingUnit = product.getDosesPerDispensingUnit();
    this.dosesPerMonth = programProduct.getDosesPerMonth();
    this.packSize = product.getPackSize();
    this.roundToZero = product.getRoundToZero();
    this.packRoundingThreshold = product.getPackRoundingThreshold();
    this.product = productName(product);
    this.price = facilityApprovedProduct.getProgramProduct().getCurrentPrice();
    this.modifiedBy = modifiedBy;
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

  public boolean validate(boolean formulaValidated) {
    if (!validateMandatoryFields() || !validateCalculatedFields(formulaValidated)) {
      throw new DataException("R&R has errors, please correct them before submission");
    }
    return true;
  }

  private boolean validateMandatoryFields() {
    return !(!isPresent(beginningBalance) || !isPresent(quantityReceived) || !isPresent(quantityDispensed) ||
        !isPresent(newPatientCount) || !isPresent(stockOutDays)) && (quantityRequested == null || isPresent(reasonForRequestedQuantity));
  }

  private boolean validateCalculatedFields(boolean formulaValidated) {
    boolean validQuantityDispensed = true;
    if(formulaValidated) {
      validQuantityDispensed = quantityDispensed == (beginningBalance + quantityReceived + totalLossesAndAdjustments - stockInHand);
    }
    return validQuantityDispensed &&
        totalLossesAndAdjustments.equals(calculateTotalLossesAndAdjustments()) &&
        normalizedConsumption.intValue() == (calculateNormalizedConsumption()) &&
        normalizedConsumption.equals(amc) && maxStockQuantity.equals(calculateMaxStockQuantity()) &&
        calculatedOrderQuantity.equals(calculateOrderQuantity()) &&
        packsToShip.equals(calculatePacksToShip()) && cost.equals(calculateCost());
  }

  private boolean isPresent(Object value) {
    return value != null;
  }

  private Float calculateCost() {
    return packsToShip * price;
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
    return maxStockQuantity - stockInHand;
  }

  private Integer calculateMaxStockQuantity() {
    return maxMonthsOfStock * amc.intValue();
  }

  private Integer calculateNormalizedConsumption() {
    Float consumptionAdjustedWithStockOutDays = ((MULTIPLIER * NUMBER_OF_DAYS) - stockOutDays) == 0 ? quantityDispensed :
        (quantityDispensed * ((MULTIPLIER * NUMBER_OF_DAYS) / ((MULTIPLIER * NUMBER_OF_DAYS) - stockOutDays)));
    Float adjustmentForNewPatients = (newPatientCount * ((Double) Math.ceil(dosesPerMonth / dosesPerDispensingUnit)).floatValue()) * MULTIPLIER;

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
