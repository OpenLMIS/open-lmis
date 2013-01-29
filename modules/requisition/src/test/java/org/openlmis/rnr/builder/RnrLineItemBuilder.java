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
  public static final LossesAndAdjustments ONE_LOSS = new LossesAndAdjustments() {{
    setQuantity(1);
    LossesAndAdjustmentsType type = new LossesAndAdjustmentsType();
    type.setAdditive(true);
    type.setName("TRANSFER_IN");
    setType(type);
  }};
  public static final int STOCK_IN_HAND = 4;
  public static final int BEGINNING_BALANCE = 10;
  public static final Instantiator<RnrLineItem> defaultRnrLineItem = new Instantiator<RnrLineItem>() {

    @Override
    public RnrLineItem instantiate(PropertyLookup<RnrLineItem> lookup) {
      RnrLineItem rnrLineItem = new RnrLineItem();
      rnrLineItem.setProductCode(lookup.valueOf(productCode, "P999"));
      rnrLineItem.setBeginningBalance(BEGINNING_BALANCE);
      rnrLineItem.setQuantityReceived(3);

      rnrLineItem.addLossesAndAdjustments(lookup.valueOf(lossesAndAdjustments, ONE_LOSS));
      rnrLineItem.setTotalLossesAndAdjustments(1);
      rnrLineItem.setStockInHand(lookup.valueOf(stockInHand, STOCK_IN_HAND));
      rnrLineItem.setQuantityDispensed(10);

      rnrLineItem.setDispensingUnit("tablet");
      rnrLineItem.setMaxMonthsOfStock(1);
      rnrLineItem.setPrice(new Money("2"));
      rnrLineItem.setQuantityApproved(2);

      rnrLineItem.setFullSupply(lookup.valueOf(fullSupply, true));
      rnrLineItem.setStockOutDays(3);
      rnrLineItem.setNewPatientCount(3);
      rnrLineItem.setDosesPerMonth(30);
      rnrLineItem.setDosesPerDispensingUnit(10);
      rnrLineItem.setNormalizedConsumption(37);
      rnrLineItem.setAmc(37);
      rnrLineItem.setMaxMonthsOfStock(2);
      rnrLineItem.setMaxStockQuantity(74);
      rnrLineItem.setCalculatedOrderQuantity(70);
      rnrLineItem.setPackSize(6);
      rnrLineItem.setPacksToShip(12);
      rnrLineItem.setPackRoundingThreshold(3);
      rnrLineItem.setRoundToZero(true);
      rnrLineItem.setPrice(new Money("4"));
      return rnrLineItem;
    }
  };
}
