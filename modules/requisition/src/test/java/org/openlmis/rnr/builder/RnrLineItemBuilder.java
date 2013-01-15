package org.openlmis.rnr.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.rnr.domain.LossesAndAdjustments;
import org.openlmis.rnr.domain.LossesAndAdjustmentsType;
import org.openlmis.rnr.domain.RnrLineItem;

import static com.natpryce.makeiteasy.Property.newProperty;

public class RnrLineItemBuilder {

  public static final Property<RnrLineItem,Float> cost = newProperty();
  public static final Instantiator<RnrLineItem> defaultRnrLineItem = new Instantiator<RnrLineItem>() {

    @Override
    public RnrLineItem instantiate(PropertyLookup<RnrLineItem> lookup) {
      RnrLineItem rnrLineItem = new RnrLineItem();

      rnrLineItem.setBeginningBalance(10);
      rnrLineItem.setQuantityReceived(3);
      LossesAndAdjustments oneLoss = new LossesAndAdjustments();
      oneLoss.setQuantity(1);
      LossesAndAdjustmentsType type = new LossesAndAdjustmentsType();
      type.setAdditive(true);
      oneLoss.setType(type);
      rnrLineItem.addLossesAndAdjustments(oneLoss);
      rnrLineItem.setTotalLossesAndAdjustments(1);
      rnrLineItem.setStockInHand(4);
      rnrLineItem.setQuantityDispensed(10);

      rnrLineItem.setDispensingUnit("tablet");
      rnrLineItem.setMaxMonthsOfStock(1);
      rnrLineItem.setPrice(2f);
      rnrLineItem.setQuantityApproved(2);

      rnrLineItem.setStockOutDays(3);
      rnrLineItem.setNewPatientCount(3);
      rnrLineItem.setDosesPerMonth(30);
      rnrLineItem.setDosesPerDispensingUnit(10);
      rnrLineItem.setNormalizedConsumption(37F);
      rnrLineItem.setAmc(37f);

      rnrLineItem.setMaxMonthsOfStock(2);
      rnrLineItem.setMaxStockQuantity(74);
      rnrLineItem.setCalculatedOrderQuantity(70);
      rnrLineItem.setPackSize(6);
      rnrLineItem.setPacksToShip(12);
      rnrLineItem.setPackRoundingThreshold(3);
      rnrLineItem.setRoundToZero(true);
      rnrLineItem.setPrice(4f);
      return rnrLineItem;
    }
  };
}
