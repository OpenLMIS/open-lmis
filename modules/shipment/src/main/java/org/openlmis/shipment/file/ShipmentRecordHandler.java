/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.file;

import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.shipment.domain.ShippedLineItem;
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

  public void execute(ShippedLineItem shippedLineItem, int rowNumber, AuditFields auditFields) {
    shippedLineItem.setModifiedDate(auditFields.getCurrentTimestamp());


    Date processTimeStamp = shipmentService.getProcessedTimeStamp(shippedLineItem);
    if (processTimeStamp != null && !processTimeStamp.equals(shippedLineItem.getModifiedDate())) {
      logger.error(format("Process timestamp %s is not equal to modified timestamp %s in row %d",
        processTimeStamp, shippedLineItem.getModifiedDate(), rowNumber));
      throw new DataException("error.duplicate.order");
    }

    ShippedLineItem shippedLineItemFromDB = shipmentService.getShippedLineItem(shippedLineItem);

    if (shippedLineItemFromDB == null) {
      shipmentService.insertShippedLineItem(shippedLineItem);
    } else {
      shippedLineItem.setId(shippedLineItemFromDB.getId());
      shipmentService.updateShippedLineItem(shippedLineItem);
    }
  }

}