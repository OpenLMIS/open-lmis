/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.order.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.shipment.domain.ShipmentFileInfo;

/**
 * Requisition after approval is converted to an order which is an entity that keeps track of the supply line
 * responsible for replenishment. It also tracks its current status and details of the shipment file
 * received for that order.
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Order extends BaseModel implements Comparable {
  private Rnr rnr;
  private OrderStatus status;
  private String orderNumber;
  private SupplyLine supplyLine;
  private ShipmentFileInfo shipmentFileInfo;
  private String ftpComment;

  public Order(Rnr rnr) {
    this(rnr.getId());
    this.rnr = rnr;
    this.createdBy = rnr.getModifiedBy();
  }

  public Order(Long id) {
    this.id = id;
  }

  public Order(Rnr rnr, SupplyLine supplyLine) {
    this(rnr);
    this.supplyLine = supplyLine;
  }

  public Facility getSupplyingFacility() {
    return supplyLine == null ? null : supplyLine.getSupplyingFacility();
  }

  public Order(Long id, OrderStatus status) {
    this.id = id;
    this.status = status;
  }

  @Override
  public int compareTo(Object o) {
    Order order2 = (Order) o;
    int comparisonResult;
    if ((comparisonResult = this.getSupplyingFacility().getName().compareToIgnoreCase(order2.getSupplyingFacility().getName())) != 0) {
      return comparisonResult;
    }
    if ((comparisonResult = this.getRnr().getProgram().getName().compareToIgnoreCase(order2.getRnr().getProgram().getName())) != 0) {
      return comparisonResult;
    }
    return this.getCreatedDate().compareTo(order2.getCreatedDate());
  }

}
