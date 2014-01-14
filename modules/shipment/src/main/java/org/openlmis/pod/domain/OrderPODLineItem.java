/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.pod.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.message.OpenLmisMessage;
import org.openlmis.rnr.domain.RnrLineItem;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = Inclusion.NON_EMPTY)
public class OrderPODLineItem extends BaseModel {

  private Long podId;
  private String productCode;
  private String productCategory;
  private Integer productCategoryDisplayOrder;
  private Integer quantityReceived;
  private String productName;
  private String dispensingUnit;
  private Integer packsToShip;
  private Integer quantityShipped;
  private Boolean fullSupply;
  private String notes;

  public OrderPODLineItem(Long podId, String productCode, Integer quantityReceived) {
    this.podId = podId;
    this.productCode = productCode;
    this.quantityReceived = quantityReceived;
  }

  public void validate() {
    if (isEmpty(productCode) || quantityReceived == null) {
      throw new DataException("error.mandatory.fields.missing");
    }
    if (quantityReceived < 0) {
      throw new DataException(new OpenLmisMessage("error.invalid.received.quantity"));
    }
  }

  //Factory Pattern
  public static OrderPODLineItem createFrom(RnrLineItem rnrLineItem) {
    OrderPODLineItem orderPODLineItem = new OrderPODLineItem();
    orderPODLineItem.setProductCode(rnrLineItem.getProductCode());
    orderPODLineItem.setProductCategory(rnrLineItem.getProductCategory());
    orderPODLineItem.setProductCategoryDisplayOrder(rnrLineItem.getProductCategoryDisplayOrder());
    orderPODLineItem.setProductName(rnrLineItem.getProduct());
    orderPODLineItem.setDispensingUnit(rnrLineItem.getDispensingUnit());
    orderPODLineItem.setPacksToShip(rnrLineItem.getPacksToShip());
    orderPODLineItem.setFullSupply(rnrLineItem.getFullSupply());
    return orderPODLineItem;
  }
}