/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.shipment.domain.ShipmentLineItem;

import java.math.BigDecimal;
import java.util.Date;

public class ShipmentLineItemBuilder {


  public static Property<? super ShipmentLineItem, Long> orderId = new Property<>();

  public static Property<? super ShipmentLineItem, String> productCode = new Property<>();

  public static Property<? super ShipmentLineItem, BigDecimal> cost = new Property<>();

  public static Property<? super ShipmentLineItem, Integer> quantityShipped = new Property<>();

  public static Property<? super ShipmentLineItem, Date> shippedDate = new Property<>();

  public static Property<? super ShipmentLineItem, Date> packedDate = new Property<>();

  public static Property<? super ShipmentLineItem, String> productName = new Property<>();

  public static Property<? super ShipmentLineItem, String> dispensingUnit = new Property<>();

  public static Property<? super ShipmentLineItem, String> productCategory = new Property<>();

  public static Property<? super ShipmentLineItem, Integer> packsToShip = new Property<>();


  public static final Instantiator<ShipmentLineItem> defaultShipmentLineItem = new Instantiator<ShipmentLineItem>() {

    @Override
    public ShipmentLineItem instantiate(PropertyLookup<ShipmentLineItem> lookup) {
      final BigDecimal nullCost = null;

      ShipmentLineItem lineItem = new ShipmentLineItem();

      lineItem.setOrderId(lookup.valueOf(orderId, 1L));
      lineItem.setProductCode(lookup.valueOf(productCode, "P123"));
      lineItem.setQuantityShipped(lookup.valueOf(quantityShipped, 0));
      lineItem.setCost(lookup.valueOf(cost, nullCost));
      lineItem.setShippedDate(lookup.valueOf(shippedDate, new Date()));
      lineItem.setPackedDate(lookup.valueOf(packedDate, new Date()));
      lineItem.setProductName(lookup.valueOf(productName, "P123 Product 123"));
      lineItem.setDispensingUnit(lookup.valueOf(dispensingUnit, "Tablet"));
      lineItem.setProductCategory(lookup.valueOf(productCategory, "Antibiotics"));
      lineItem.setPacksToShip(lookup.valueOf(packsToShip, 100));
      return lineItem;
    }
  };
}
