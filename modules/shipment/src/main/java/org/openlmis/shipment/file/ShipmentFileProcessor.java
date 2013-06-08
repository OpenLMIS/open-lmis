/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.file;

import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
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
import org.springframework.integration.annotation.MessageEndpoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

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

  public void process(Message message) throws IOException {
    File shipmentFile = (File) message.getPayload();
    boolean processingError = false;
    ModelClass modelClass = new ModelClass(ShippedLineItem.class, true);
    try (FileInputStream inputStream = new FileInputStream(shipmentFile)) {
      logger.info("Starting to process file " + shipmentFile.getName());
      Date currentTimestamp = dbService.getCurrentTimestamp();
      csvParser.process(inputStream, modelClass, shipmentRecordHandler, new AuditFields(currentTimestamp));
      logger.info("Successfully processed file " + shipmentFile.getName());
    } catch (DataException | UploadException e) {
      logger.info("Error processing file " + shipmentFile.getName() + " with error " + e.getMessage());
      processingError = true;
    } finally {
      logger.info("Starting post processing file " + shipmentFile.getName());
      shipmentFilePostProcessHandler.process(shipmentFile, processingError);
      logger.info("Updated order statuses for file " + shipmentFile.getName());
      boolean deleteStatus = FileUtils.deleteQuietly(shipmentFile);
      if (deleteStatus)
        logger.info("Successfully deleted file " + shipmentFile.getName());
    }
  }

}
