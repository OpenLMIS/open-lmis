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

  public static Property<? super ShipmentLineItem, String> orderNumber = new Property<>();

  public static Property<? super ShipmentLineItem, String> productCode = new Property<>();

  public static Property<? super ShipmentLineItem, String> replacedProductCode = new Property<>();

  public static Property<? super ShipmentLineItem, BigDecimal> cost = new Property<>();

  public static Property<? super ShipmentLineItem, Integer> quantityShipped = new Property<>();

  public static Property<? super ShipmentLineItem, Date> shippedDate = new Property<>();

  public static Property<? super ShipmentLineItem, Date> packedDate = new Property<>();

  public static Property<? super ShipmentLineItem, String> productName = new Property<>();

  public static Property<? super ShipmentLineItem, String> dispensingUnit = new Property<>();

  public static Property<? super ShipmentLineItem, String> productCategory = new Property<>();

  public static Property<? super ShipmentLineItem, Integer> packsToShip = new Property<>();

  public static Property<? super ShipmentLineItem, Integer> productCategoryDisplayOrder = new Property<>();

  public static Property<? super ShipmentLineItem, Integer> productDisplayOrder = new Property<>();

  public static Property<? super ShipmentLineItem, Boolean> fullSupply = new Property<>();

  public static String defaultProductCategory = "Antibiotics";
  public static String defaultProductName = "P123 Product 123";
  public static String defaultProductDispensingUnit = "Tablet";
  public static String defaultProductCode = "P123";
  public static String defaultReplacedProductCode = "P133";
  public static int defaultProductCategoryDisplayOrder = 1;
  public static int defaultProductDisplayOrder = 1;
  public static boolean defaultFullSupply = true;
  public static int defaultPacksToShip = 100;
  public static BigDecimal nullCost = null;

  public static final Instantiator<ShipmentLineItem> defaultShipmentLineItem = new Instantiator<ShipmentLineItem>() {

    @Override
    public ShipmentLineItem instantiate(PropertyLookup<ShipmentLineItem> lookup) {

      ShipmentLineItem lineItem = new ShipmentLineItem();

      lineItem.setOrderId(lookup.valueOf(orderId, 1L));
      lineItem.setOrderNumber(lookup.valueOf(orderNumber, "OYELL_FVR00000001R"));
      lineItem.setProductCode(lookup.valueOf(productCode, defaultProductCode));
      lineItem.setReplacedProductCode(lookup.valueOf(replacedProductCode, defaultReplacedProductCode));
      lineItem.setQuantityShipped(lookup.valueOf(quantityShipped, 0));
      lineItem.setCost(lookup.valueOf(cost, nullCost));
      lineItem.setShippedDate(lookup.valueOf(shippedDate, new Date()));
      lineItem.setPackedDate(lookup.valueOf(packedDate, new Date()));
      lineItem.setProductName(lookup.valueOf(productName, defaultProductName));
      lineItem.setDispensingUnit(lookup.valueOf(dispensingUnit, defaultProductDispensingUnit));
      lineItem.setProductCategory(lookup.valueOf(productCategory, defaultProductCategory));
      lineItem.setPacksToShip(lookup.valueOf(packsToShip, defaultPacksToShip));
      lineItem.setProductCategoryDisplayOrder(
        lookup.valueOf(productCategoryDisplayOrder, defaultProductCategoryDisplayOrder));
      lineItem.setProductDisplayOrder(lookup.valueOf(productDisplayOrder, defaultProductDisplayOrder));
      lineItem.setFullSupply(lookup.valueOf(fullSupply, defaultFullSupply));
      return lineItem;
    }
  };
}
