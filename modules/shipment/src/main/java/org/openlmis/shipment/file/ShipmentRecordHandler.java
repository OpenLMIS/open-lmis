/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.file;

import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.service.ShipmentService;
import org.openlmis.upload.model.AuditFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

import static java.lang.String.format;

@Component
@NoArgsConstructor
public class ShipmentRecordHandler {
  private static Logger logger = LoggerFactory.getLogger(ShipmentRecordHandler.class);

  @Autowired
  private ShipmentService shipmentService;

  public void execute(ShipmentLineItem shipmentLineItem, int rowNumber, AuditFields auditFields) {
    shipmentLineItem.setModifiedDate(auditFields.getCurrentTimestamp());


    Date processTimeStamp = shipmentService.getProcessedTimeStamp(shipmentLineItem);
    if (processTimeStamp != null && !processTimeStamp.equals(shipmentLineItem.getModifiedDate())) {
      logger.error(format("Process timestamp %s is not equal to modified timestamp %s in row %d",
        processTimeStamp, shipmentLineItem.getModifiedDate(), rowNumber));
      throw new DataException("error.duplicate.order");
    }

    ShipmentLineItem shipmentLineItemFromDB = shipmentService.getShippedLineItem(shipmentLineItem);

    if (shipmentLineItemFromDB == null) {
      shipmentService.insertShippedLineItem(shipmentLineItem);
    } else {
      shipmentLineItem.setId(shipmentLineItemFromDB.getId());
      shipmentService.updateShippedLineItem(shipmentLineItem);
    }
  }

}