/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.shipment.file;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.dto.ShipmentLineItemDTO;
import org.openlmis.shipment.ShipmentLineItemTransformer;
import org.openlmis.shipment.domain.ShipmentConfiguration;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.openlmis.shipment.domain.ShipmentFileTemplate;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.file.csv.handler.ShipmentFilePostProcessHandler;
import org.openlmis.shipment.service.ShipmentFileTemplateService;
import org.openlmis.shipment.service.ShipmentService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;
import org.supercsv.io.CsvListReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.shipment.builder.ShipmentFileColumnBuilder.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.supercsv.prefs.CsvPreference.STANDARD_PREFERENCE;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({ShipmentFileProcessor.class, MessageBuilder.class})
public class ShipmentFileProcessorTest {

  @Mock
  private ShipmentFilePostProcessHandler shipmentFilePostProcessHandler;

  @Mock
  MessageChannel ftpArchiveOutputChannel;

  @Mock
  ShipmentFileTemplateService shipmentFileTemplateService;

  @Mock
  ShipmentService shipmentService;

  @Mock
  Message message;

  @Mock
  File shipmentFile;

  @Mock
  FileInputStream shipmentInputStream;

  @Mock
  ShipmentLineItemTransformer shipmentLineItemTransformer;

  @Mock
  FileReader mockedFileReader;

  @Mock
  CsvListReader mockedCsvListReader;

  @InjectMocks
  private ShipmentFileProcessor shipmentFileProcessor;

  ShipmentConfiguration shipmentConfiguration;

  @Before
  public void setUp() throws Exception {

    when(message.getPayload()).thenReturn(shipmentFile);
    whenNew(FileInputStream.class).withArguments(shipmentFile).thenReturn(shipmentInputStream);
    whenNew(FileReader.class).withArguments(shipmentFile).thenReturn(mockedFileReader);
    whenNew(CsvListReader.class).withArguments(mockedFileReader, STANDARD_PREFERENCE).thenReturn(mockedCsvListReader);

    shipmentConfiguration = new ShipmentConfiguration(false);
  }

  @Test
  public void shouldThrowErrorIfNotEnoughFieldsInShipmentFile() throws Exception {

    List<ShipmentFileColumn> shipmentFileColumnList = asList(
      make(a(mandatoryShipmentFileColumn, with(columnPosition, 1))),
      make(a(mandatoryShipmentFileColumn, with(columnPosition, 2))),
      make(a(mandatoryShipmentFileColumn, with(columnPosition, 3)))
    );

    ShipmentFileTemplate shipmentFileTemplate = new ShipmentFileTemplate(shipmentConfiguration, shipmentFileColumnList);

    when(shipmentFileTemplateService.get()).thenReturn(shipmentFileTemplate);

    when(mockedCsvListReader.read()).thenReturn(asList("field1", "field2")).thenReturn(null);

    try {
      shipmentFileProcessor.process(message);
    } catch (Exception e) {
      assertThat(e.getMessage(), is("mandatory.data.missing"));
    }

    verify(shipmentService, times(0)).insertShippedLineItem(any(ShipmentLineItem.class));

    verify(shipmentFilePostProcessHandler).process(new HashSet<Long>(), shipmentFile, true);
  }

  @Test
  public void shouldInsertLineItemsIfAllIncludedFieldsArePresent() throws Exception {

    List<ShipmentFileColumn> shipmentFileColumnList = new ArrayList<ShipmentFileColumn>() {{
      add(make(a(mandatoryShipmentFileColumn, with(fieldName, "orderId"), with(columnPosition, 2))));
      add(make(a(defaultShipmentFileColumn, with(columnPosition, 4), with(includeInShipmentFile, false))));
      add(make(a(defaultShipmentFileColumn, with(columnPosition, 6), with(includeInShipmentFile, false))));
    }};

    ShipmentFileTemplate shipmentFileTemplate = new ShipmentFileTemplate(shipmentConfiguration, shipmentFileColumnList);

    when(shipmentFileTemplateService.get()).thenReturn(shipmentFileTemplate);

    when(mockedCsvListReader.read()).thenReturn(asList("", "232")).thenReturn(null);
    ShipmentLineItem lineItem = new ShipmentLineItem();
    lineItem.setOrderId(232L);
    when(shipmentLineItemTransformer.transform(any(ShipmentLineItemDTO.class), anyString(), anyString())).thenReturn(lineItem);

    shipmentFileProcessor.process(message);

    verify(mockedCsvListReader, times(0)).getHeader(true);
    verify(shipmentService, times(1)).insertShippedLineItem(any(ShipmentLineItem.class));

    Set<Long> orderIds = new HashSet<>();
    orderIds.add(232L);
    verify(shipmentFilePostProcessHandler).process(orderIds, shipmentFile, false);
  }

  @Test
  public void shouldRemoveHeadersIfPresentInCsv() throws Exception {

    List<ShipmentFileColumn> shipmentFileColumnList = asList(
      make(a(mandatoryShipmentFileColumn, with(columnPosition, 2)))
    );

    boolean headerInFile = true;
    ShipmentFileTemplate shipmentFileTemplate = new ShipmentFileTemplate(new ShipmentConfiguration(headerInFile), shipmentFileColumnList);

    when(shipmentFileTemplateService.get()).thenReturn(shipmentFileTemplate);

    when(mockedCsvListReader.read()).thenReturn(null);

    shipmentFileProcessor.process(message);

    verify(mockedCsvListReader).getHeader(true);
    verify(shipmentFilePostProcessHandler).process(new HashSet<Long>(), shipmentFile, false);


  }

  @Test
  public void shouldCreateDTOIfDateFieldsArePresent() throws Exception {
    List<ShipmentFileColumn> shipmentFileColumnList = asList(
      make(a(mandatoryShipmentFileColumn,
        with(columnPosition, 1),
        with(fieldName, "packedDate"),
        with(dateFormat, "MM/yy")
      )),
      make(a(defaultShipmentFileColumn,
        with(columnPosition, 2),
        with(fieldName, "shippedDate"),
        with(dateFormat, "dd/MM/yyyy")
      )));

    ShipmentFileTemplate shipmentFileTemplate = new ShipmentFileTemplate(shipmentConfiguration, shipmentFileColumnList);

    when(shipmentFileTemplateService.get()).thenReturn(shipmentFileTemplate);

    when(mockedCsvListReader.read()).thenReturn(asList("11/13", "11/11/2011")).thenReturn(null);

    ShipmentLineItemDTO shipmentLineItemDTO = new ShipmentLineItemDTO();
    shipmentLineItemDTO.setPackedDate("11/13");
    shipmentLineItemDTO.setShippedDate("11/11/2011");
    ShipmentLineItem shipmentLineItem = mock(ShipmentLineItem.class);
    when(shipmentLineItem.getOrderId()).thenReturn(111L);
    when(shipmentLineItemTransformer.transform(shipmentLineItemDTO, "MM/yy", "dd/MM/yyyy")).thenReturn(shipmentLineItem);

    shipmentFileProcessor.process(message);

    verify(shipmentService).insertShippedLineItem(shipmentLineItem);

    Set<Long> orderIds = new HashSet<>();
    orderIds.add(111L);
    verify(shipmentFilePostProcessHandler).process(orderIds, shipmentFile, false);
  }
}
