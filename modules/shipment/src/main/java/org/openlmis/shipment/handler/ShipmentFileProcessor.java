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
import org.openlmis.order.service.OrderService;
import org.openlmis.shipment.ShipmentLineItemTransformer;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.openlmis.shipment.domain.ShipmentFileTemplate;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.service.ShipmentFileTemplateService;
import org.openlmis.shipment.service.ShipmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static java.lang.Long.parseLong;
import static org.apache.commons.collections.CollectionUtils.select;
import static org.supercsv.prefs.CsvPreference.STANDARD_PREFERENCE;

@Component
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
    Set<Long> orderIds = new HashSet<>();

    ShipmentFileTemplate shipmentFileTemplate = shipmentFileTemplateService.get();

    boolean successfullyProcessed = true;
    try (ICsvListReader listReader = new CsvListReader(new FileReader(shipmentFile), STANDARD_PREFERENCE)) {

      ignoreFirstLineIfHeadersArePresent(shipmentFileTemplate, listReader);

      getSpringProxy().processShipmentLineItem(listReader, shipmentFileTemplate, orderIds, creationDate);
      logger.debug("Successfully processed file " + shipmentFile.getName());

    } catch (Exception e) {
      successfullyProcessed = false;
    }

    shipmentFilePostProcessHandler.process(orderIds, shipmentFile, successfullyProcessed);
  }

  @Transactional
  public void processShipmentLineItem(ICsvListReader listReader,
                                      ShipmentFileTemplate shipmentFileTemplate, Set<Long> orderSet, Date creationDate) throws Exception {
    boolean status = true;

    List<ShipmentFileColumn> shipmentFileColumns = shipmentFileTemplate.getShipmentFileColumns();

    Collection<ShipmentFileColumn> includedColumns = filterIncludedColumns(shipmentFileColumns);

    String packedDateFormat = getFormatForField("packedDate", shipmentFileColumns);
    String shippedDateFormat = getFormatForField("shippedDate", shipmentFileColumns);

    List<String> fieldsInOneRow;
    while ((fieldsInOneRow = listReader.read()) != null) {

      ShipmentLineItemDTO dto = populateDTO(fieldsInOneRow, includedColumns);
      status = addShippableOrder(orderSet, dto) && status;

      if (status) {
        status = saveLineItem(dto, packedDateFormat, shippedDateFormat, creationDate);
      }
    }

    if (!status) {
      throw new DataException("shipment.file.error");
    }
    if(orderSet.size() == 0) {
      throw new DataException("mandatory.field.missing");
    }
  }

  private boolean addShippableOrder(Set<Long> orderIds, ShipmentLineItemDTO dto) {
    boolean status = true;
    try {
      Long orderId = parseLong(dto.getOrderId());

      if (!orderIds.contains(orderId)) {
        if (orderService.isShippable(orderId)) {
          orderIds.add(orderId);
        } else {
          status = false;
        }
      }
    } catch (NumberFormatException e) {
      logger.warn("invalid orderId: " + dto.getOrderId() + " in shipment file");
      status = false;
    }

    return status;
  }

  private boolean saveLineItem(ShipmentLineItemDTO dto,
                               String packedDateFormat,
                               String shippedDateFormat, Date creationDate) {
    boolean savedSuccessfully = true;
    try {
      ShipmentLineItem lineItem = transformer.transform(dto, packedDateFormat, shippedDateFormat, creationDate);
      shipmentService.insertOrUpdate(lineItem);
    } catch (DataException e) {
      logger.warn("Error in processing shipment file for orderId: " + dto.getOrderId(), e);
      savedSuccessfully = false;
    }

    return savedSuccessfully;
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
        logger.error("Unable to set field '" + name +
          "' in ShipmentLinetItemDTO, check mapping between DTO and ShipmentFileColumn", e);
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

  private String getFormatForField(String fieldName, List<ShipmentFileColumn> shipmentFileColumns) {
    for (ShipmentFileColumn shipmentFileColumn : shipmentFileColumns) {
      if (shipmentFileColumn.getName().equals(fieldName)) {
        return shipmentFileColumn.getDatePattern();
      }
    }
    return null;
  }


}
