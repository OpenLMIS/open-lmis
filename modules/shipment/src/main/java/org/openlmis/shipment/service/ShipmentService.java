/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProductService;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class ShipmentService {
  @Autowired
  private ShipmentRepository shipmentRepository;

  @Autowired
  private ProductService productService;


  public void insertOrUpdate(ShipmentLineItem shipmentLineItem) {
    validateForSave(shipmentLineItem);
    validateShipment(shipmentLineItem);
    if (shipmentRepository.getShippedLineItem(shipmentLineItem) == null) {
      shipmentRepository.insertShippedLineItem(shipmentLineItem);
    } else {
      shipmentRepository.updateShippedLineItem(shipmentLineItem);
    }
  }

  private void validateShipment(ShipmentLineItem shipmentLineItem) {
    if (productService.getIdForCode(shipmentLineItem.getProductCode()) == null) {
      throw new DataException("error.unknown.product");
    }
  }

  private void validateForSave(ShipmentLineItem shipmentLineItem) {
    if (shipmentLineItem.getQuantityShipped() < 0) {
      throw new DataException("error.negative.shipped.quantity");
    }
  }

  public void insertShipmentFileInfo(ShipmentFileInfo shipmentFileInfo) {
    shipmentRepository.insertShipmentFileInfo(shipmentFileInfo);
  }

}
