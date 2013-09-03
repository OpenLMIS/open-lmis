package org.openlmis.shipment.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.shipment.domain.ShippedLineItem;

import java.math.BigDecimal;
import java.util.Date;

public class ShipmentLineItemBuilder {


  public static Property<? super ShippedLineItem, Long> rnrId = new Property<>();

  public static Property<? super ShippedLineItem, String> productCode = new Property<>();

  public static Property<? super ShippedLineItem, BigDecimal> cost = new Property<>();

  public static Property<? super ShippedLineItem, Integer> quantityShipped = new Property<>();

  public static Property<? super ShippedLineItem, Date> shippedDate = new Property<>();

  public static Property<? super ShippedLineItem, Date> packedDate = new Property<>();


  public static final Instantiator<ShippedLineItem> defaultShipmentLineItem = new Instantiator<ShippedLineItem>() {

    @Override
    public ShippedLineItem instantiate(PropertyLookup<ShippedLineItem> lookup) {
      final BigDecimal nullCost = null;

      ShippedLineItem lineItem = new ShippedLineItem();

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
