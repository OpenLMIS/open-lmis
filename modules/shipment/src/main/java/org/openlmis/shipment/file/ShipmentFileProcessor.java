/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.file;

import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.service.DbService;
import org.openlmis.shipment.domain.ShippedLineItem;
import org.openlmis.shipment.file.csv.handler.ShipmentFilePostProcessHandler;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.exception.UploadException;
import org.openlmis.upload.model.AuditFields;
import org.openlmis.upload.model.ModelClass;
import org.openlmis.upload.parser.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.annotation.MessageEndpoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.springframework.integration.support.MessageBuilder.withPayload;

@MessageEndpoint
@NoArgsConstructor
public class ShipmentFileProcessor {
  private static Logger logger = LoggerFactory.getLogger(ShipmentFileProcessor.class);

  @Autowired
  private CSVParser csvParser;

  @Autowired
  private RecordHandler shipmentRecordHandler;

  @Autowired
  private ShipmentFilePostProcessHandler shipmentFilePostProcessHandler;

  @Autowired
  private DbService dbService;

  @Autowired
  private MessageChannel ftpArchiveOutputChannel;


  public void process(Message message) throws IOException {
    File shipmentFile = (File) message.getPayload();
    boolean processingError = false;
    ModelClass modelClass = new ModelClass(ShippedLineItem.class, true);
    try (FileInputStream inputStream = new FileInputStream(shipmentFile)) {

      Date currentTimestamp = dbService.getCurrentTimestamp();
      csvParser.process(inputStream, modelClass, shipmentRecordHandler, new AuditFields(currentTimestamp));

      logger.debug("Successfully processed file " + shipmentFile.getName());
      sendArchiveToFtp(shipmentFile);

    } catch (DataException | UploadException e) {
      logger.warn("Error processing file " + shipmentFile.getName() + " with error " + e.getMessage());
      processingError = true;
    } finally {
      shipmentFilePostProcessHandler.process(shipmentFile, processingError);
      logger.info("Updated order statuses for file " + shipmentFile.getName());
      boolean deleteStatus = deleteQuietly(shipmentFile);
      if (!deleteStatus) {
        logger.error("Unable to delete temporary shipment file " + shipmentFile.getName());
      }
    }
  }

  private void sendArchiveToFtp(File file) {
    Message<File> message = withPayload(file).build();
    ftpArchiveOutputChannel.send(message);
    logger.debug("Shipment file " + file.getName() + " archived to FTP");
  }

}
