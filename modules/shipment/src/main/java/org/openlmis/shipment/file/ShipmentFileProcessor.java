/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.file;

import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.openlmis.core.exception.DataException;
import org.openlmis.shipment.domain.ShippedLineItem;
import org.openlmis.shipment.file.csv.handler.ShipmentFilePostProcessHandler;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.exception.UploadException;
import org.openlmis.upload.model.ModelClass;
import org.openlmis.upload.parser.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@MessageEndpoint
@NoArgsConstructor
public class ShipmentFileProcessor {

  private CSVParser csvParser;
  private RecordHandler shipmentRecordHandler;
  private ShipmentFilePostProcessHandler shipmentFilePostProcessHandler;

  @Autowired
  public ShipmentFileProcessor(CSVParser csvParser, RecordHandler shipmentRecordHandler,
                               ShipmentFilePostProcessHandler shipmentFileCsvPostProcessHandler){
    this.csvParser = csvParser;
    this.shipmentRecordHandler = shipmentRecordHandler;
    this.shipmentFilePostProcessHandler = shipmentFileCsvPostProcessHandler;
  }

  @Transactional
  public void process(Message message) throws IOException {
    File shipmentFile = (File) message.getPayload();
    boolean processingError = false;
    ModelClass modelClass = new ModelClass(ShippedLineItem.class, true);

    try (FileInputStream inputStream = new FileInputStream(shipmentFile)) {
      csvParser.process(inputStream, modelClass, shipmentRecordHandler);
    } catch (DataException | UploadException e) {
      processingError = true;
    } finally {
      shipmentFilePostProcessHandler.process(shipmentFile, processingError);
      FileUtils.deleteQuietly(shipmentFile);
    }
  }

}
