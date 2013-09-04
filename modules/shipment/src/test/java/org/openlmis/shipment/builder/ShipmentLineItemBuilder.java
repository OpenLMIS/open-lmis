package org.openlmis.shipment.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.shipment.domain.ShipmentLineItem;

import java.math.BigDecimal;
import java.util.Date;

public class ShipmentLineItemBuilder {


  public static Property<? super ShipmentLineItem, Long> rnrId = new Property<>();

  public static Property<? super ShipmentLineItem, String> productCode = new Property<>();

  public static Property<? super ShipmentLineItem, BigDecimal> cost = new Property<>();

  public static Property<? super ShipmentLineItem, Integer> quantityShipped = new Property<>();

  public static Property<? super ShipmentLineItem, Date> shippedDate = new Property<>();

  public static Property<? super ShipmentLineItem, Date> packedDate = new Property<>();


  public static final Instantiator<ShipmentLineItem> defaultShipmentLineItem = new Instantiator<ShipmentLineItem>() {

    @Override
    public ShipmentLineItem instantiate(PropertyLookup<ShipmentLineItem> lookup) {
      final BigDecimal nullCost = null;

      ShipmentLineItem lineItem = new ShipmentLineItem();

      lineItem.setRnrId(lookup.valueOf(rnrId, 1L));
      lineItem.setProductCode(lookup.valueOf(productCode, "P123"));
      lineItem.setQuantityShipped(lookup.valueOf(quantityShipped, 0));
      lineItem.setCost(lookup.valueOf(cost, nullCost));
      lineItem.setShippedDate(lookup.valueOf(shippedDate, new Date()));
      lineItem.setPackedDate(lookup.valueOf(packedDate, new Date()));
      return lineItem;
    }
  };
}
