/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.file.csv.handler;

import lombok.NoArgsConstructor;
import org.openlmis.order.domain.Order;
import org.openlmis.upload.parser.CsvBeanReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
@NoArgsConstructor
public class ShipmentFileReader {

  private static Logger logger = LoggerFactory.getLogger(ShipmentFileReader.class);

  public Set<Integer> getOrderIds(File shipmentFile) {
    Set<Integer> orderIds = new HashSet<>();
    try {
      CsvBeanReader csvBeanReader = new CsvBeanReader(RawShipment.class, shipmentFile);
      RawShipment rawShipment;
      while ((rawShipment = (RawShipment) csvBeanReader.readWithCellProcessors()) != null) {
        orderIds.add(rawShipment.getOrderNumber());
      }
    } catch (IOException e) {
      logger.error("Error processing file during processErrorFile for shipment file " + shipmentFile.getName() + " " + e.getMessage());
    }
    return orderIds;
  }
}
