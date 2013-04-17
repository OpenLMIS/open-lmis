/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.file;

import org.apache.commons.io.FileUtils;
import org.openlmis.core.exception.DataException;
import org.openlmis.shipment.domain.Shipment;
import org.openlmis.shipment.file.csv.handler.ShipmentFileCsvErrorHandler;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.exception.UploadException;
import org.openlmis.upload.model.ModelClass;
import org.openlmis.upload.parser.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@MessageEndpoint
public class ShipmentFileProcessor {

  private static Logger logger = LoggerFactory.getLogger(ShipmentFileProcessor.class);

  @Autowired
  private CSVParser csvParser;
  @Autowired
  private RecordHandler shipmentRecordHandler;
  @Autowired
  private ShipmentFileCsvErrorHandler shipmentFileCsvErrorHandler;
  @Autowired
  private MessageChannel ftpOutputChannel;


  @Transactional
  public void process(Message message) throws IOException {
    File shipmentFile = (File) message.getPayload();
    try (FileInputStream inputStream = new FileInputStream(shipmentFile)) {
      ModelClass modelClass = new ModelClass(Shipment.class, true);
      csvParser.process(inputStream, modelClass, shipmentRecordHandler);
    } catch (DataException | UploadException | IOException e) {
      processErrorFile(shipmentFile);
    } finally {
      FileUtils.deleteQuietly(shipmentFile);
    }
  }

  private void processErrorFile(File file) throws IOException {
    shipmentFileCsvErrorHandler.process(file);
    Message<?> message = MessageBuilder.withPayload(file).build();
    ftpOutputChannel.send(message);
  }
}
