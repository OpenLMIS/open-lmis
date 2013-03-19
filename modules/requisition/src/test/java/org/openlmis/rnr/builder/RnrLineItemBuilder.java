package org.openlmis.rnr.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.Money;
import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.LossesAndAdjustmentsType;
import org.openlmis.rnr.domain.RnrLineItem;

import static com.natpryce.makeiteasy.Property.newProperty;

public class RnrLineItemBuilder {


  public static final Property<RnrLineItem, Float> cost = newProperty();
  public static final Property<RnrLineItem, Integer> stockInHand = newProperty();
  public static final Property<RnrLineItem, LossesAndAdjustments> lossesAndAdjustments = newProperty();
  public static final Property<RnrLineItem, Boolean> fullSupply = newProperty();
  public static final Property<RnrLineItem, String> productCode = newProperty();
  public static final Property<RnrLineItem, Integer> beginningBalance = newProperty();
  public static final Property<RnrLineItem, Integer> quantityReceived = newProperty();
  public static final Property<RnrLineItem, Integer> totalLossesAndAdjustments = newProperty();
  public static final Property<RnrLineItem, Integer> quantityDispensed = newProperty();
  public static final Property<RnrLineItem, Integer> newPatientCount = newProperty();
  public static final Property<RnrLineItem, Integer> stockOutDays = newProperty();
  public static final Property<RnrLineItem, Boolean> roundToZero = newProperty();
  public static final Property<RnrLineItem, Integer> packRoundingThreshold = newProperty();
  public static final Property<RnrLineItem, Integer> packSize = newProperty();
  public static final Property<RnrLineItem, Integer> quantityApproved = newProperty();
  public static final Property<RnrLineItem, String> reasonForRequestedQuantity = newProperty();
  public static final Property<RnrLineItem, Integer> packsToShip = newProperty();
  public static final Property<RnrLineItem, Integer> productCategoryDisplayOrder = newProperty();
  public static final Property<RnrLineItem, Integer> productDisplayOrder = newProperty();

  public static final Property<RnrLineItem, Integer> quantityRequested = newProperty();
  private static Property<RnrLineItem, String> productCategory = newProperty();

  public static final LossesAndAdjustments ONE_LOSS = new LossesAndAdjustments() {{
    setQuantity(1);
    LossesAndAdjustmentsType type = new LossesAndAdjustmentsType();
    type.setAdditive(true);
    type.setName("TRANSFER_IN");
    setType(type);
  }};
  public static final int STOCK_IN_HAND = 4;
  public static final int BEGINNING_BALANCE = 10;
  public static final int QUANTITY_RECEIVED = 3;
  public static final int TOTAL_LOSSES_AND_ADJUSTMENTS = 1;
  public static final int QUANTITY_DISPENSED = 10;
  public static final int NEW_PATIENT_COUNT = 3;
  public static final int STOCK_OUT_DAYS = 3;
  public static final int QUANTITY_REQUESTED = 6;
  public static final String REASON_FOR_REQUESTED_QUANTITY = "More patients";
  public static final String REMARKS = "Remarks";
  public static final boolean ROUND_To_ZERO = true;
  public static final int QUANTITY_APPROVED = 2;
  public static final int PACKS_TO_SHIP = 2;
  public static final int PRODUCT_CATEGORY_DISPLAY_ORDER = 1;
  public static final String PRODUCT_CODE = "P999";
  public static final Integer PRODUCT_DISPLAY_ORDER = null;
  public static final String PRODUCT_CATEGORY = "C1";

  public static final Instantiator<RnrLineItem> defaultRnrLineItem = new Instantiator<RnrLineItem>() {


    @Override
    public RnrLineItem instantiate(PropertyLookup<RnrLineItem> lookup) {
      RnrLineItem rnrLineItem = new RnrLineItem();
      rnrLineItem.setProductCode(lookup.valueOf(productCode, PRODUCT_CODE));
      rnrLineItem.setProductCategory(lookup.valueOf(productCategory, PRODUCT_CATEGORY));
      rnrLineItem.setBeginningBalance(lookup.valueOf(beginningBalance, BEGINNING_BALANCE));
      rnrLineItem.setQuantityReceived(lookup.valueOf(quantityReceived, QUANTITY_RECEIVED));

      rnrLineItem.addLossesAndAdjustments(lookup.valueOf(lossesAndAdjustments, ONE_LOSS));
      rnrLineItem.setTotalLossesAndAdjustments(lookup.valueOf(totalLossesAndAdjustments, TOTAL_LOSSES_AND_ADJUSTMENTS));
      rnrLineItem.setStockInHand(lookup.valueOf(stockInHand, STOCK_IN_HAND));
      rnrLineItem.setQuantityDispensed(lookup.valueOf(quantityDispensed, QUANTITY_DISPENSED));

      rnrLineItem.setDispensingUnit("tablet");
      rnrLineItem.setMaxMonthsOfStock(1);
      rnrLineItem.setPrice(new Money("2"));
      rnrLineItem.setQuantityApproved(lookup.valueOf(quantityApproved, QUANTITY_APPROVED));

      rnrLineItem.setFullSupply(lookup.valueOf(fullSupply, true));
      rnrLineItem.setStockOutDays(lookup.valueOf(stockOutDays, STOCK_OUT_DAYS));
      rnrLineItem.setNewPatientCount(lookup.valueOf(newPatientCount, NEW_PATIENT_COUNT));
      rnrLineItem.setDosesPerMonth(30);
      rnrLineItem.setDosesPerDispensingUnit(10);
      rnrLineItem.setNormalizedConsumption(37);
      rnrLineItem.setAmc(37);
      rnrLineItem.setMaxMonthsOfStock(2);
      rnrLineItem.setMaxStockQuantity(74);
      rnrLineItem.setCalculatedOrderQuantity(70);
      rnrLineItem.setPackSize(lookup.valueOf(packSize, 6));
      rnrLineItem.setPacksToShip(12);
      rnrLineItem.setPackRoundingThreshold(lookup.valueOf(packRoundingThreshold, 3));
      rnrLineItem.setRoundToZero(lookup.valueOf(roundToZero, ROUND_To_ZERO));
      rnrLineItem.setPrice(new Money("4"));
      rnrLineItem.setQuantityRequested(lookup.valueOf(quantityRequested, QUANTITY_REQUESTED));
      rnrLineItem.setReasonForRequestedQuantity(lookup.valueOf(reasonForRequestedQuantity, REASON_FOR_REQUESTED_QUANTITY));
      rnrLineItem.setPacksToShip(lookup.valueOf(packsToShip, PACKS_TO_SHIP));
      rnrLineItem.setRemarks(REMARKS);
      rnrLineItem.setProductCategoryDisplayOrder(lookup.valueOf(productCategoryDisplayOrder, PRODUCT_CATEGORY_DISPLAY_ORDER));
      rnrLineItem.setProductDisplayOrder(lookup.valueOf(productDisplayOrder, PRODUCT_DISPLAY_ORDER));
      return rnrLineItem;
    }
  };
}
