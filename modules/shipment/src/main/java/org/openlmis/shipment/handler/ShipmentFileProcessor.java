/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.handler;

import lombok.NoArgsConstructor;
import org.apache.commons.collections.Predicate;
import org.openlmis.core.exception.DataException;
import org.openlmis.order.dto.ShipmentLineItemDTO;
import org.openlmis.shipment.ShipmentLineItemTransformer;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.openlmis.shipment.domain.ShipmentFileTemplate;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.service.ShipmentFileTemplateService;
import org.openlmis.shipment.service.ShipmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Long.parseLong;
import static org.apache.commons.collections.CollectionUtils.select;
import static org.supercsv.prefs.CsvPreference.STANDARD_PREFERENCE;

@MessageEndpoint
@NoArgsConstructor
public class ShipmentFileProcessor {
  private static Logger logger = LoggerFactory.getLogger(ShipmentFileProcessor.class);

  @Autowired
  private ShipmentFilePostProcessHandler shipmentFilePostProcessHandler;

  @Autowired
  private ShipmentFileTemplateService shipmentFileTemplateService;

  @Autowired
  private ShipmentService shipmentService;

  @Autowired
  private ShipmentLineItemTransformer transformer;


  public void process(Message message) throws Exception {
    File shipmentFile = (File) message.getPayload();
    logger.debug("processing Shipment File " + shipmentFile.getName());

    ShipmentFileTemplate shipmentFileTemplate = shipmentFileTemplateService.get();

    try (ICsvListReader listReader = new CsvListReader(new FileReader(shipmentFile), STANDARD_PREFERENCE)) {

      ignoreFirstLineIfHeadersArePresent(shipmentFileTemplate, listReader);

      processShipmentLineItem(listReader, shipmentFileTemplate, shipmentFile);

      logger.debug("Successfully processed file " + shipmentFile.getName());
    }
  }

  @Transactional
  private void processShipmentLineItem(ICsvListReader listReader,
                                       ShipmentFileTemplate shipmentFileTemplate,
                                       File shipmentFile) throws Exception {
    Set<Long> orderIds = new HashSet<>();
    boolean errorInFile = false;

    try {

      List<ShipmentFileColumn> shipmentFileColumns = shipmentFileTemplate.getShipmentFileColumns();

      Collection<ShipmentFileColumn> includedColumns = filterIncludedColumns(shipmentFileColumns);

      String packedDateFormat = getFormatForField("packedDate", shipmentFileColumns);
      String shippedDateFormat = getFormatForField("shippedDate", shipmentFileColumns);

      int maxPosition = findMaximumPosition(includedColumns);
      List<String> fieldsInOneRow;
      while ((fieldsInOneRow = listReader.read()) != null) {

        if (fieldsInOneRow.size() < maxPosition) {
          logger.warn("Shipment file should contain at least " + maxPosition + " columns");
          errorInFile = true;
        } else {
          ShipmentLineItemDTO dto = populateDTO(fieldsInOneRow, includedColumns);
          errorInFile = saveLineItem(dto, orderIds, errorInFile, packedDateFormat, shippedDateFormat);
        }
      }

      if (errorInFile) {
        throw new DataException("shipment.file.error");
      }

    } finally {
      shipmentFilePostProcessHandler.process(orderIds, shipmentFile, errorInFile);
    }
  }

  private boolean saveLineItem(ShipmentLineItemDTO dto,
                               Set<Long> orderIds,
                               final boolean errorInFile,
                               String packedDateFormat,
                               String shippedDateFormat) {
    try {
      ShipmentLineItem lineItem = transformer.transform(dto, packedDateFormat, shippedDateFormat);
      if (!errorInFile) {
        shipmentService.insertShippedLineItem(lineItem);
      }
    } catch (DataException e) {
      logger.warn("Error in processing shipment file for orderId: " + dto.getOrderId(), e);
      return true;
    } finally {
      addOrderId(orderIds, dto);
    }

    return errorInFile;
  }

  private void addOrderId(Set<Long> orderIds, ShipmentLineItemDTO lineItemDTO) {
    Long orderId = null;
    try {
      orderId = parseLong(lineItemDTO.getOrderId());
    } catch (Exception e) {
      logger.warn("invalid order id", e);
    }
    if (orderId != null) {
      orderIds.add(orderId);
    }
  }


  private Collection<ShipmentFileColumn> filterIncludedColumns(List<ShipmentFileColumn> shipmentFileColumns) {
    return select(shipmentFileColumns, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        return ((ShipmentFileColumn) o).getInclude();
      }
    });
  }

  private ShipmentLineItemDTO populateDTO(List<String> fieldsInOneRow, Collection<ShipmentFileColumn> shipmentFileColumns) {

    ShipmentLineItemDTO dto = new ShipmentLineItemDTO();

    for (ShipmentFileColumn shipmentFileColumn : shipmentFileColumns) {
      Integer position = shipmentFileColumn.getPosition();
      String name = shipmentFileColumn.getName();
      try {
        Field field = ShipmentLineItemDTO.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(dto, fieldsInOneRow.get(position - 1));
      } catch (Exception e) {
        logger.error("Exception in creating DTO from shipment file", e);
      }
    }

    return dto;
  }

  private void ignoreFirstLineIfHeadersArePresent(ShipmentFileTemplate shipmentFileTemplate,
                                                  ICsvListReader listReader) throws IOException {
    if (shipmentFileTemplate.getShipmentConfiguration().isHeaderInFile()) {
      listReader.getHeader(true);
    }
  }

  private int findMaximumPosition(Collection<ShipmentFileColumn> shipmentFileColumns) {
    int maxPosition = 0;
    for (ShipmentFileColumn shipmentFileColumn : shipmentFileColumns) {
      if (shipmentFileColumn.getPosition() > maxPosition) {
        maxPosition = shipmentFileColumn.getPosition();
      }
    }
    return maxPosition;
  }

  private String getFormatForField(String fieldName, List<ShipmentFileColumn> shipmentFileColumns) {
    for (ShipmentFileColumn shipmentFileColumn : shipmentFileColumns) {
      if (shipmentFileColumn.getName().equals(fieldName)) {
        return shipmentFileColumn.getDatePattern();
      }
    }
    return null;
  }


}
