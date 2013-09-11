/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.repository.mapper.ShipmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
@NoArgsConstructor
public class ShipmentRepository {

  private ShipmentMapper shipmentMapper;

  @Autowired
  public ShipmentRepository(ShipmentMapper shipmentMapper) {
    this.shipmentMapper = shipmentMapper;
  }

  public void insertShippedLineItem(ShipmentLineItem shipmentLineItem) {
    try {
      shipmentMapper.insertShippedLineItem(shipmentLineItem);
    } catch (DataIntegrityViolationException exception) {
      throw new DataException("error.incorrect.length");
    }
  }

  public void insertShipmentFileInfo(ShipmentFileInfo shipmentFileInfo) {
    shipmentMapper.insertShipmentFileInfo(shipmentFileInfo);
  }

  public ShipmentLineItem getShippedLineItem(ShipmentLineItem shipmentLineItem) {
    return shipmentMapper.getShippedLineItem(shipmentLineItem);
  }

  public void updateShippedLineItem(ShipmentLineItem shipmentLineItem) {
    shipmentMapper.updateShippedLineItem(shipmentLineItem);
  }
}
