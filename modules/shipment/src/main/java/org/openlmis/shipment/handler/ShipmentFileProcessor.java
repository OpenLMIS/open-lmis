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


  public void process(Message message) throws IOException, NoSuchFieldException, IllegalAccessException {
    File shipmentFile = (File) message.getPayload();
    logger.debug("processing Shipment File " + shipmentFile.getName());

    ShipmentFileTemplate shipmentFileTemplate = shipmentFileTemplateService.get();

    boolean errorInFile = true;

    Set<Long> orderIds = new HashSet<>();

    try (ICsvListReader listReader = new CsvListReader(new FileReader(shipmentFile), STANDARD_PREFERENCE)) {

      ignoreFirstLineIfHeadersArePresent(shipmentFileTemplate, listReader);

      orderIds = processShipmentLineItem(listReader, shipmentFileTemplate);

      errorInFile = false;
      logger.debug("Successfully processed file " + shipmentFile.getName());

    } catch (Exception e) {
      logger.warn("Error processing file " + shipmentFile.getName() + " with error " + e.getMessage());
      throw e;

    } finally {
      shipmentFilePostProcessHandler.process(orderIds, shipmentFile, errorInFile);
    }
  }

  private Set<Long> processShipmentLineItem(ICsvListReader listReader, ShipmentFileTemplate shipmentFileTemplate)
    throws IOException, NoSuchFieldException, IllegalAccessException {

    List<ShipmentFileColumn> shipmentFileColumns = shipmentFileTemplate.getShipmentFileColumns();


    Collection<ShipmentFileColumn> includedColumns = filterIncludedColumns(shipmentFileColumns);

    String packedDateFormat = getFormatForField("packedDate", shipmentFileColumns);
    String shippedDateFormat = getFormatForField("shippedDate", shipmentFileColumns);

    int maxPosition = findMaximumPosition(includedColumns);
    List<String> fieldsInOneRow;
    Set<Long> orderIds = new HashSet<>();
    while ((fieldsInOneRow = listReader.read()) != null) {

      if (fieldsInOneRow.size() < maxPosition) {
        logger.warn("Shipment file should contain at least " + maxPosition + " columns");
        throw new DataException("mandatory.data.missing");

      } else {

        ShipmentLineItem lineItem = generateLineItem(fieldsInOneRow, includedColumns, packedDateFormat, shippedDateFormat);
        shipmentService.insertShippedLineItem(lineItem);

        orderIds.add(lineItem.getOrderId());
      }

    }

    return orderIds;
  }

  private ShipmentLineItem generateLineItem(List<String> fieldsInOneRow,
                                            Collection<ShipmentFileColumn> includedColumns,
                                            String packedDateFormat,
                                            String shippedDateFormat) throws NoSuchFieldException, IllegalAccessException {

    ShipmentLineItemDTO dto = populateDTO(fieldsInOneRow, includedColumns);

    ShipmentLineItem lineItem = transformer.transform(dto, packedDateFormat, shippedDateFormat);

    return lineItem;
  }


  private Collection<ShipmentFileColumn> filterIncludedColumns(List<ShipmentFileColumn> shipmentFileColumns) {
    return select(shipmentFileColumns, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        return ((ShipmentFileColumn) o).getInclude();
      }
    });
  }

  private ShipmentLineItemDTO populateDTO(List<String> fieldsInOneRow, Collection<ShipmentFileColumn> shipmentFileColumns)
    throws NoSuchFieldException, IllegalAccessException {

    ShipmentLineItemDTO dto = new ShipmentLineItemDTO();

    for (ShipmentFileColumn shipmentFileColumn : shipmentFileColumns) {
      Integer position = shipmentFileColumn.getPosition();
      String name = shipmentFileColumn.getName();
      Field field = ShipmentLineItemDTO.class.getDeclaredField(name);
      field.setAccessible(true);
      field.set(dto, fieldsInOneRow.get(position - 1));
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
