/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.handler;

import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.core.domain.EDIFileTemplate;
import org.openlmis.core.exception.DataException;
import org.openlmis.order.service.OrderService;
import org.openlmis.shipment.ShipmentLineItemTransformer;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.dto.ShipmentLineItemDTO;
import org.openlmis.shipment.service.ShipmentFileTemplateService;
import org.openlmis.shipment.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static org.supercsv.prefs.CsvPreference.STANDARD_PREFERENCE;

/**
 * Exposes the services for processing shipment file and its line items. It validates the data received in
 * shipment file, processes each record and then logs the errors if any.
 */

@Component
@MessageEndpoint
@NoArgsConstructor
public class ShipmentFileProcessor {
  private static Logger logger = Logger.getLogger(ShipmentFileProcessor.class);

  @Autowired
  private ShipmentFilePostProcessHandler shipmentFilePostProcessHandler;

  @Autowired
  private ShipmentFileTemplateService shipmentFileTemplateService;

  @Autowired
  private ShipmentService shipmentService;

  @Autowired
  private ShipmentLineItemTransformer transformer;

  @Autowired
  private OrderService orderService;

  @Autowired
  private ApplicationContext applicationContext;

  private ShipmentFileProcessor getSpringProxy() {
    return applicationContext.getBean(this.getClass());
  }

  public void process(Message message) throws Exception {
    File shipmentFile = (File) message.getPayload();
    logger.debug("processing Shipment File " + shipmentFile.getName());
    Path path = Paths.get(shipmentFile.getPath());
    BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
    Date creationDate = new Date(attributes.creationTime().toMillis());
    Set<String> orderNumbers = new HashSet<>();

    EDIFileTemplate shipmentFileTemplate = shipmentFileTemplateService.get();

    boolean successfullyProcessed = true;
    try (ICsvListReader listReader = new CsvListReader(new FileReader(shipmentFile), STANDARD_PREFERENCE)) {

      ignoreFirstLineIfHeadersArePresent(shipmentFileTemplate, listReader);

      getSpringProxy().processShipmentLineItem(listReader, shipmentFileTemplate, orderNumbers, creationDate);
      logger.debug("Successfully processed file " + shipmentFile.getName());

    } catch (Exception e) {
      successfullyProcessed = false;
    }

    shipmentFilePostProcessHandler.process(orderNumbers, shipmentFile, successfullyProcessed);
  }

  //  TODO: important add unit test fot this method
  @Transactional
  public void processShipmentLineItem(ICsvListReader listReader,
                                      EDIFileTemplate shipmentFileTemplate,
                                      Set<String> orderSet,
                                      Date creationDate) throws Exception {
    boolean status = true;

    Collection<EDIFileColumn> includedColumns = shipmentFileTemplate.filterIncludedColumns();

    String packedDateFormat = shipmentFileTemplate.getDateFormatForColumn("packedDate");
    String shippedDateFormat = shipmentFileTemplate.getDateFormatForColumn("shippedDate");

    List<String> fieldsInOneRow;

    while ((fieldsInOneRow = listReader.read()) != null) {

      ShipmentLineItemDTO dto = ShipmentLineItemDTO.populate(fieldsInOneRow, includedColumns);

      // parse concatinated columns
      parseConcatenatedOrderId( dto );
      status = addShippableOrder(orderSet, dto) && status;

      if (status) {
        status = saveLineItem(dto, packedDateFormat, shippedDateFormat, creationDate);
      }
    }

    if (!status) {
      throw new DataException("shipment.file.error");
    }
    if (orderSet.size() == 0) {
      throw new DataException("error.mandatory.fields.missing");
    }
  }

  private void parseConcatenatedOrderId(ShipmentLineItemDTO dto) {
      if(dto.getConcatenatedOrderId() != null && dto.getOrderId() == null){
        // the concatenation formula is based on ZM's specification here.
        // this needs to be refactored to support different formats of concateonation.
        // for ZM, the concation is [3 letter Program code][6 digit number][Regular or Emergency(E/R)]
        //TODO: the hard coded substring needs to be moved to somewhere configurable.
        dto.setProgramCode(dto.getConcatenatedOrderId().substring(0,3));
        dto.setOrderId( dto.getConcatenatedOrderId().substring( 3, 9) );
      }
  }

  private boolean addShippableOrder(Set<String> orderNumbers, ShipmentLineItemDTO dto) {
    boolean status = true;
    try {
      String orderNumber = dto.getOrderNumber();

      if (!orderNumbers.contains(orderNumber)) {
        if (orderService.isShippable(orderNumber)) {
          orderNumbers.add(orderNumber);
        } else {
          status = false;
        }
      }
    } catch (NumberFormatException e) {
      logger.warn("invalid orderId: " + dto.getOrderNumber() + " in shipment file");
      status = false;
    }

    return status;
  }

  private boolean saveLineItem(ShipmentLineItemDTO dto,
                               String packedDateFormat,
                               String shippedDateFormat,
                               Date creationDate) {
    boolean savedSuccessfully = true;
    try {
      ShipmentLineItem lineItem = transformer.transform(dto, packedDateFormat, shippedDateFormat, creationDate);
      shipmentService.save(lineItem);
    } catch (DataException e) {
      logger.warn("Error in processing shipment file for orderId: " + dto.getOrderNumber(), e);
      savedSuccessfully = false;
    }

    return savedSuccessfully;
  }

  private void ignoreFirstLineIfHeadersArePresent(EDIFileTemplate shipmentFileTemplate,
                                                  ICsvListReader listReader) throws IOException {
    if (shipmentFileTemplate.getConfiguration().isHeaderInFile()) {
      listReader.getHeader(true);
    }
  }
}
