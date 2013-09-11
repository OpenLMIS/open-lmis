/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
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

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Order extends BaseModel {
  private Rnr rnr;
  private OrderStatus status;
  private SupplyLine supplyLine;
  private ShipmentFileInfo shipmentFileInfo;
  private String ftpComment;

  public Order(Rnr rnr) {
    this.rnr = rnr;
    this.createdBy = rnr.getModifiedBy();
  }

  public Order(Long id) {
    this.id = id;
  }

  public Order(Long orderId, Rnr rnr, SupplyLine supplyLine) {
    this.id = orderId;
    this.rnr = rnr;
    this.supplyLine = supplyLine;
  }
}
