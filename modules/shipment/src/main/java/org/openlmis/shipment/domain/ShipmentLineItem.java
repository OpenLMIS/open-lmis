/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Product;
import org.openlmis.rnr.domain.LineItem;
import org.openlmis.rnr.domain.RnrLineItem;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Represents each record of ShipmentFile and keeps track of cost and quantity shipped of a product along with
 * product details.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ShipmentLineItem extends LineItem {

  private Long orderId;

  private String productCode;

  private String replacedProductCode;

  private Integer quantityShipped;

  private String productName;

  private String dispensingUnit;

  private String productCategory;

  private Integer packsToShip;

  private BigDecimal cost;

  private Date packedDate;

  private Date shippedDate;

  private Integer productCategoryDisplayOrder;

  private Integer productDisplayOrder;

  private Boolean fullSupply;

  private void setReferenceFields(String productName,
                                  String dispensingUnit,
                                  String productCategory,
                                  Integer packsToShip,
                                  Integer productCategoryDisplayOrder,
                                  Integer productDisplayOrder,
                                  Boolean fullSupply) {
    this.productName = productName;
    this.dispensingUnit = dispensingUnit;
    this.productCategory = productCategory;
    this.packsToShip = packsToShip;
    this.productCategoryDisplayOrder = productCategoryDisplayOrder;
    this.productDisplayOrder = productDisplayOrder;
    this.fullSupply = fullSupply;
  }

  public void fillReferenceFields(Product product) {
    this.setReferenceFields(product.getName(), product.getDispensingUnit(), product.getCategory().getName(), null,
      product.getCategory().getDisplayOrder(), product.getDisplayOrder(), product.getFullSupply());
  }

  public void fillReferenceFields(RnrLineItem lineItem) {
    this.setReferenceFields(lineItem.getProduct(), lineItem.getDispensingUnit(), lineItem.getProductCategory(),
      lineItem.getPacksToShip(), lineItem.getProductCategoryDisplayOrder(), lineItem.getProductDisplayOrder(),
      lineItem.getFullSupply());
  }

  @Override
  public boolean compareCategory(LineItem lineItem) {
    return false;
  }

  @Override
  public String getCategoryName() {
    return null;
  }

  @Override
  public String getValue(String columnName) throws NoSuchFieldException, IllegalAccessException {
    return null;
  }

  @Override
  public boolean isRnrLineItem() {
    return false;
  }
}

