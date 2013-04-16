/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.file.csv.handler;

import org.openlmis.upload.parser.CsvBeanReader;
import org.openlmis.upload.model.ModelClass;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class ShipmentFileCsvErrorHandler {

  public void process(File file) throws IOException {

    List<String> orderIds = new ArrayList<>();
    RawShipment rawShipment;
    ModelClass modelClass = new ModelClass(RawShipment.class);
    InputStream inputStream = new FileInputStream(file);
    CsvBeanReader csvBeanReader = new CsvBeanReader(modelClass, inputStream);

    while ((rawShipment = (RawShipment) csvBeanReader.read()) != null) {
      if (rawShipment.getOrderNumber() != null) {
        orderIds.add(rawShipment.getOrderNumber());
      }
    }
    System.out.println(orderIds);
    //call service
  }

}
