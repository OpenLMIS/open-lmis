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
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Product;
import org.openlmis.rnr.domain.RnrLineItem;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ShipmentLineItem extends BaseModel {

  private Long orderId;

  private String productCode;

  private Integer quantityShipped;

  private String productName;

  private String dispensingUnit;

  private String productCategory;

  private Integer packsToShip;

  private BigDecimal cost;

  private Date packedDate;

  private Date shippedDate;

  private void setReferenceFields(String productName, String dispensingUnit, String productCategory, Integer packsToShip) {
    this.productName = productName;
    this.dispensingUnit = dispensingUnit;
    this.productCategory = productCategory;
    this.packsToShip = packsToShip;
  }

  public void fillReferenceFields(Product product) {
    this.setReferenceFields(product.getName(), product.getDispensingUnit(), product.getCategory().getName(), null);
  }

  public void fillReferenceFields(RnrLineItem lineItem) {
    this.setReferenceFields(lineItem.getProduct(), lineItem.getDispensingUnit(), lineItem.getProductCategory(), lineItem.getPacksToShip());
  }
}

