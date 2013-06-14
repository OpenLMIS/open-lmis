/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.domain.ShippedLineItem;
import org.openlmis.shipment.repository.mapper.ShipmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
@NoArgsConstructor
public class ShipmentRepository {

  private ShipmentMapper shipmentMapper;

  @Autowired
  public ShipmentRepository(ShipmentMapper shipmentMapper) {
    this.shipmentMapper = shipmentMapper;
  }

  public void insertShippedLineItem(ShippedLineItem shippedLineItem) {
    try {
      shipmentMapper.insertShippedLineItem(shippedLineItem);
    } catch (DataIntegrityViolationException exception) {
      if (exception.getMessage().contains("violates foreign key constraint \"shipped_line_items_rnrid_fkey\""))
        throw new DataException("Unknown order number");

      if (exception.getMessage().contains("violates foreign key constraint \"shipped_line_items_productcode_fkey\""))
        throw new DataException("Unknown product code");

      throw new DataException("error.incorrect.length");
    }
  }

  public void insertShipmentFileInfo(ShipmentFileInfo shipmentFileInfo) {
    shipmentMapper.insertShipmentFileInfo(shipmentFileInfo);
  }

  public ShippedLineItem getShippedLineItem(ShippedLineItem shippedLineItem) {
    return shipmentMapper.getShippedLineItem(shippedLineItem);
  }

  public void updateShippedLineItem(ShippedLineItem shippedLineItem) {
    shipmentMapper.updateShippedLineItem(shippedLineItem);
  }

  public Date getProcessedTimeStamp(ShippedLineItem shippedLineItem) {
    return shipmentMapper.getProcessedTimeStamp(shippedLineItem);
  }
}
