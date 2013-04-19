/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.repository;

import lombok.NoArgsConstructor;
import org.openlmis.shipment.domain.ShippedLineItem;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.repository.mapper.ShipmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@NoArgsConstructor
public class ShipmentRepository {

  private ShipmentMapper shipmentMapper;

  @Autowired
  public ShipmentRepository(ShipmentMapper shipmentMapper) {
    this.shipmentMapper = shipmentMapper;
  }

  public void insertShippedLineItem(ShippedLineItem shippedLineItem) {
    shipmentMapper.insertShippedLineItem(shippedLineItem);
  }

  public void insertShipmentFileInfo(ShipmentFileInfo shipmentFileInfo) {
   shipmentMapper.insertShipmentFileInfo(shipmentFileInfo);
  }
}
