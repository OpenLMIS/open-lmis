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
import org.openlmis.order.service.OrderService;
import org.openlmis.shipment.ShipmentLineItemTransformer;
import org.openlmis.shipment.domain.ShipmentConfiguration;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.openlmis.shipment.domain.ShipmentFileTemplate;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.handler.ShipmentFilePostProcessHandler;
import org.openlmis.shipment.handler.ShipmentFileProcessor;
import org.openlmis.shipment.service.ShipmentFileTemplateService;
import org.openlmis.shipment.service.ShipmentService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.Message;
import org.springframework.integration.support.MessageBuilder;
import org.supercsv.io.CsvListReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.shipment.builder.ShipmentFileColumnBuilder.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.supercsv.prefs.CsvPreference.STANDARD_PREFERENCE;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({ShipmentFileProcessor.class, MessageBuilder.class})
public class ShipmentFileProcessorTest {

  @Mock
  private ShipmentFilePostProcessHandler shipmentFilePostProcessHandler;

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

  @Mock
  private OrderService orderService;

  @Mock
  ApplicationContext applicationContext;

  @InjectMocks
  private ShipmentFileProcessor shipmentFileProcessor;

  ShipmentConfiguration shipmentConfiguration;

  Date creationDate;

  @Before
  public void setUp() throws Exception {

    when(message.getPayload()).thenReturn(shipmentFile);
    whenNew(FileInputStream.class).withArguments(shipmentFile).thenReturn(shipmentInputStream);
    whenNew(FileReader.class).withArguments(shipmentFile).thenReturn(mockedFileReader);
    whenNew(CsvListReader.class).withArguments(mockedFileReader, STANDARD_PREFERENCE).thenReturn(mockedCsvListReader);

    mockStatic(Paths.class);
    Path path = mock(Path.class);
    String shipmentFilePath = "testPath";
    when(shipmentFile.getPath()).thenReturn(shipmentFilePath);
    when(Paths.get(shipmentFilePath)).thenReturn(path);
    mockStatic(Files.class);
    BasicFileAttributes attributes = mock(BasicFileAttributes.class);
    when(Files.readAttributes(path, BasicFileAttributes.class)).thenReturn(attributes);
    FileTime fileTime = FileTime.from(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    when(attributes.creationTime()).thenReturn(fileTime);
    creationDate = new Date();
    whenNew(Date.class).withArguments(fileTime.toMillis()).thenReturn(creationDate);

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
      assertThat(e.getMessage(), is("shipment.file.error"));
    }

    verify(shipmentService, times(0)).insertOrUpdate(any(ShipmentLineItem.class));

    verify(shipmentFilePostProcessHandler).process(new HashSet<Long>(), shipmentFile, false);
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
    when(shipmentLineItemTransformer.transform(any(ShipmentLineItemDTO.class), anyString(), anyString(), any(Date.class))).thenReturn(lineItem);
    when(applicationContext.getBean(ShipmentFileProcessor.class)).thenReturn(shipmentFileProcessor);
    when(orderService.isShippable(232L)).thenReturn(true);

    shipmentFileProcessor.process(message);

    verify(mockedCsvListReader, times(0)).getHeader(true);
    verify(shipmentService, times(1)).insertOrUpdate(any(ShipmentLineItem.class));

    Set<Long> orderIds = new HashSet<>();
    orderIds.add(232L);
    verify(shipmentFilePostProcessHandler).process(orderIds, shipmentFile, true);
  }

  @Test
  public void shouldRemoveHeadersIfPresentInCsv() throws Exception {

    List<ShipmentFileColumn> shipmentFileColumnList = asList(
      make(a(mandatoryShipmentFileColumn, with(columnPosition, 2)))
    );

    boolean headerInFile = true;
    ShipmentFileTemplate shipmentFileTemplate = new ShipmentFileTemplate(new ShipmentConfiguration(headerInFile), shipmentFileColumnList);

    when(shipmentFileTemplateService.get()).thenReturn(shipmentFileTemplate);
    when(applicationContext.getBean(ShipmentFileProcessor.class)).thenReturn(shipmentFileProcessor);

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
        with(fieldName, "orderId")
      )),
      make(a(mandatoryShipmentFileColumn,
        with(columnPosition, 2),
        with(fieldName, "packedDate"),
        with(dateFormat, "MM/yy")
      )),
      make(a(defaultShipmentFileColumn,
        with(columnPosition, 3),
        with(fieldName, "shippedDate"),
        with(dateFormat, "dd/MM/yyyy")
      )));

    ShipmentFileTemplate shipmentFileTemplate = new ShipmentFileTemplate(shipmentConfiguration, shipmentFileColumnList);

    when(shipmentFileTemplateService.get()).thenReturn(shipmentFileTemplate);

    when(mockedCsvListReader.read()).thenReturn(asList("333", "11/13", "11/11/2011")).thenReturn(null);

    ShipmentLineItemDTO shipmentLineItemDTO = new ShipmentLineItemDTO();
    shipmentLineItemDTO.setOrderId("333");
    shipmentLineItemDTO.setPackedDate("11/13");
    shipmentLineItemDTO.setShippedDate("11/11/2011");
    ShipmentLineItem shipmentLineItem = mock(ShipmentLineItem.class);
    when(shipmentLineItem.getOrderId()).thenReturn(333L);
    when(orderService.isShippable(333L)).thenReturn(true);

    when(shipmentLineItemTransformer.transform(shipmentLineItemDTO, "MM/yy", "dd/MM/yyyy", creationDate)).thenReturn(shipmentLineItem);
    when(applicationContext.getBean(ShipmentFileProcessor.class)).thenReturn(shipmentFileProcessor);

    shipmentFileProcessor.process(message);

    verify(shipmentService).insertOrUpdate(shipmentLineItem);

    HashSet<Long> orderIds = new HashSet<>();
    orderIds.add(333L);
    verify(shipmentFilePostProcessHandler).process(orderIds, shipmentFile, true);
  }
}
