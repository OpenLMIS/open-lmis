/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.file.csv.handler;

import lombok.NoArgsConstructor;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.service.ShipmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

@Component
@NoArgsConstructor
public class ShipmentFilePostProcessHandler {

  private static Logger logger = LoggerFactory.getLogger(ShipmentFilePostProcessHandler.class);
  @Autowired
  private ShipmentService shipmentService;
  @Autowired
  private MessageChannel ftpOutputChannel;
  @Autowired
  private ShipmentFileReader shipmentFileReader;


  public void process(File shipmentFile, boolean processingError) {
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo(shipmentFile.getName(), processingError);
    shipmentService.insertShipmentFileInfo(shipmentFileInfo);

    Set<Long> orderIds = shipmentFileReader.getOrderIds(shipmentFile);
    shipmentService.updateStatusAndShipmentIdForOrders(new ArrayList<>(orderIds), shipmentFileInfo);

    if (processingError) sendErrorFileToFtp(shipmentFile);
  }

  private void sendErrorFileToFtp(File file) {
    Message<File> message = MessageBuilder.withPayload(file).build();
    ftpOutputChannel.send(message);
    logger.info("Sent  error file to FTP " + file.getName());
  }

}
