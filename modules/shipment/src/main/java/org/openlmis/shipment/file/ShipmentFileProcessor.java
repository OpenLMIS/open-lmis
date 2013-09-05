/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.file;

import lombok.NoArgsConstructor;
import org.openlmis.db.service.DbService;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.openlmis.shipment.domain.ShipmentFileTemplate;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.file.csv.handler.ShipmentFilePostProcessHandler;
import org.openlmis.shipment.service.ShipmentFileTemplateService;
import org.openlmis.shipment.service.ShipmentService;
import org.openlmis.upload.parser.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.annotation.MessageEndpoint;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.supercsv.prefs.CsvPreference.STANDARD_PREFERENCE;

@MessageEndpoint
@NoArgsConstructor
public class ShipmentFileProcessor {
  private static Logger logger = LoggerFactory.getLogger(ShipmentFileProcessor.class);

  @Autowired
  private CSVParser csvParser;

  @Autowired
  private ShipmentFilePostProcessHandler shipmentFilePostProcessHandler;

  @Autowired
  private DbService dbService;

  @Autowired
  private MessageChannel ftpArchiveOutputChannel;

  @Autowired
  private ShipmentFileTemplateService shipmentFileTemplateService;

  @Autowired
  private ShipmentService shipmentService;


  public void process(Message message) throws IOException {
    File shipmentFile = (File) message.getPayload();

    ShipmentFileTemplate shipmentFileTemplate = shipmentFileTemplateService.get();
    List<ShipmentFileColumn> shipmentFileColumns = shipmentFileTemplate.getShipmentFileColumns();

    int maxPosition = findMaximumPosition(shipmentFileColumns);

    try (ICsvListReader listReader = new CsvListReader(new FileReader(shipmentFile), STANDARD_PREFERENCE)) {
      List<String> fieldsInOneRow;
      while ((fieldsInOneRow = listReader.read()) != null) {
        if (fieldsInOneRow.size() < maxPosition) {
          logger.warn("Shipment file should contain at least " + maxPosition + " columns");
        } else {
          ShipmentLineItem shipmentLineItem = new ShipmentLineItem();
          shipmentService.insertShippedLineItem(shipmentLineItem);
        }
      }
    }
  }

  private int findMaximumPosition(List<ShipmentFileColumn> shipmentFileColumns) {
    int maxPosition = 0;
    for (ShipmentFileColumn shipmentFileColumn : shipmentFileColumns) {
      if (shipmentFileColumn.getInclude() && shipmentFileColumn.getPosition() > maxPosition) {
        maxPosition = shipmentFileColumn.getPosition();
      }
    }
    return maxPosition;
  }

//  public void process(Message message) throws IOException {
//    File shipmentFile = (File) message.getPayload();
//    boolean processingError = false;
//    ModelClass modelClass = new ModelClass(ShippedLineItem.class, true);
//    try (FileInputStream inputStream = new FileInputStream(shipmentFile)) {
//
//      Date currentTimestamp = dbService.getCurrentTimestamp();
//      csvParser.process(inputStream, modelClass, shipmentRecordHandler, new AuditFields(currentTimestamp));
//
//      logger.debug("Successfully processed file " + shipmentFile.getName());
//      sendArchiveToFtp(shipmentFile);
//
//    } catch (DataException | UploadException e) {
//      logger.warn("Error processing file " + shipmentFile.getName() + " with error " + e.getMessage());
//      processingError = true;
//    } finally {
//      shipmentFilePostProcessHandler.process(shipmentFile, processingError);
//      logger.info("Updated order statuses for file " + shipmentFile.getName());
//      boolean deleteStatus = deleteQuietly(shipmentFile);
//      if (!deleteStatus) {
//        logger.error("Unable to delete temporary shipment file " + shipmentFile.getName());
//      }
//    }
//  }

//  private void sendArchiveToFtp(File file) {
//    Message<File> message = withPayload(file).build();
//    ftpArchiveOutputChannel.send(message);
//    logger.debug("Shipment file " + file.getName() + " archived to FTP");
//  }

}
