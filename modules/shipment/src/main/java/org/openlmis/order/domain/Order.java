/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.shipment.domain.ShipmentFileInfo;

import static org.openlmis.order.domain.OrderStatus.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Order extends BaseModel {
  private Rnr rnr;
  private OrderStatus status;
  private SupplyLine supplyLine;
  private ShipmentFileInfo shipmentFileInfo;

  public Order(Rnr rnr) {
    this.rnr = rnr;
    this.createdBy = rnr.getModifiedBy();
    this.status = IN_ROUTE;
  }

  public Order(Long id) {
    this.id = id;
  }

  public Order(Long orderId, Rnr rnr) {
    this.id = orderId;
    this.rnr = rnr;
  }

  public void updateShipmentFileInfo(ShipmentFileInfo shipmentFileInfo) {
    this.shipmentFileInfo = shipmentFileInfo;
    this.status = shipmentFileInfo.isProcessingError() ? RELEASED : PACKED;
  }
}
