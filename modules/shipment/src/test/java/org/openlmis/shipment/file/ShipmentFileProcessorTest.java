/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.file;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.EDIConfiguration;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.core.domain.EDIFileTemplate;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.service.OrderService;
import org.openlmis.shipment.ShipmentLineItemTransformer;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.dto.ShipmentLineItemDTO;
import org.openlmis.shipment.handler.ShipmentFilePostProcessHandler;
import org.openlmis.shipment.handler.ShipmentFileProcessor;
import org.openlmis.shipment.service.ShipmentFileTemplateService;
import org.openlmis.shipment.service.ShipmentService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
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
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
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

  EDIConfiguration shipmentConfiguration;

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

    shipmentConfiguration = new EDIConfiguration(false);
  }

  @Test
  public void shouldThrowErrorIfNotEnoughFieldsInShipmentFile() throws Exception {

    List<EDIFileColumn> shipmentFileColumnList = new ArrayList<>();
    shipmentFileColumnList.add(make(a(mandatoryShipmentFileColumn, with(columnPosition, 1))));
    shipmentFileColumnList.add(make(a(mandatoryShipmentFileColumn, with(columnPosition, 2))));
    shipmentFileColumnList.add(make(a(mandatoryShipmentFileColumn, with(columnPosition, 3))));

    EDIFileTemplate shipmentFileTemplate = new EDIFileTemplate(shipmentConfiguration, shipmentFileColumnList);

    when(shipmentFileTemplateService.get()).thenReturn(shipmentFileTemplate);

    when(mockedCsvListReader.read()).thenReturn(asList("field1", "field2")).thenReturn(null);

    try {
      shipmentFileProcessor.process(message);
    } catch (Exception e) {
      assertThat(e.getMessage(), is("shipment.file.error"));
    }

    verify(shipmentService, times(0)).save(any(ShipmentLineItem.class));

    verify(shipmentFilePostProcessHandler).process(new HashSet<String>(), shipmentFile, false);
  }

  @Test
  public void shouldInsertLineItemsIfAllIncludedFieldsArePresent() throws Exception {

    List<EDIFileColumn> shipmentFileColumnList = new ArrayList<EDIFileColumn>() {{
      add(make(a(mandatoryShipmentFileColumn, with(fieldName, "orderNumber"), with(columnPosition, 2))));
      add(make(a(defaultShipmentFileColumn, with(columnPosition, 4), with(includeInShipmentFile, false))));
      add(make(a(defaultShipmentFileColumn, with(columnPosition, 6), with(includeInShipmentFile, false))));
    }};

    EDIFileTemplate shipmentFileTemplate = new EDIFileTemplate(shipmentConfiguration, shipmentFileColumnList);

    when(shipmentFileTemplateService.get()).thenReturn(shipmentFileTemplate);

    when(mockedCsvListReader.read()).thenReturn(asList("", "OYELL_FVR00000123R")).thenReturn(null);
    ShipmentLineItem lineItem = new ShipmentLineItem();
    when(shipmentLineItemTransformer.transform(any(ShipmentLineItemDTO.class), anyString(), anyString(), any(Date.class))).thenReturn(lineItem);
    when(applicationContext.getBean(ShipmentFileProcessor.class)).thenReturn(shipmentFileProcessor);
    when(orderService.isShippable("OYELL_FVR00000123R")).thenReturn(true);
    Order order = new Order(123L);
    when(orderService.getByOrderNumber("OYELL_FVR00000123R")).thenReturn(order);

    shipmentFileProcessor.process(message);

    verify(mockedCsvListReader, times(0)).getHeader(true);
    verify(shipmentService, times(1)).save(any(ShipmentLineItem.class));

    Set<String> orderNumbers = new HashSet<>();
    orderNumbers.add("OYELL_FVR00000123R");
    verify(shipmentFilePostProcessHandler).process(orderNumbers, shipmentFile, true);
  }

  @Test
  public void shouldRemoveHeadersIfPresentInCsv() throws Exception {

    List<EDIFileColumn> shipmentFileColumnList = new ArrayList<EDIFileColumn>() {{
      add(make(a(mandatoryShipmentFileColumn, with(columnPosition, 2))));
    }};

    boolean headerInFile = true;
    EDIFileTemplate shipmentFileTemplate = new EDIFileTemplate(new EDIConfiguration(headerInFile), shipmentFileColumnList);

    when(shipmentFileTemplateService.get()).thenReturn(shipmentFileTemplate);
    when(applicationContext.getBean(ShipmentFileProcessor.class)).thenReturn(shipmentFileProcessor);

    when(mockedCsvListReader.read()).thenReturn(null);

    shipmentFileProcessor.process(message);

    verify(mockedCsvListReader).getHeader(true);
    verify(shipmentFilePostProcessHandler).process(new HashSet<String>(), shipmentFile, false);
  }

  @Test
  public void shouldCreateDTOIfDateFieldsArePresent() throws Exception {
    List<EDIFileColumn> shipmentFileColumnList = new ArrayList<EDIFileColumn>() {{
      add(make(a(mandatoryShipmentFileColumn,
        with(columnPosition, 1),
        with(fieldName, "orderNumber")
      )));
      add(make(a(mandatoryShipmentFileColumn,
        with(columnPosition, 2),
        with(fieldName, "packedDate"),
        with(dateFormat, "MM/yy")
      )));
      add(make(a(defaultShipmentFileColumn,
        with(columnPosition, 3),
        with(fieldName, "shippedDate"),
        with(dateFormat, "dd/MM/yyyy")
      )));
    }};

    EDIFileTemplate shipmentFileTemplate = new EDIFileTemplate(shipmentConfiguration, shipmentFileColumnList);

    when(shipmentFileTemplateService.get()).thenReturn(shipmentFileTemplate);

    when(mockedCsvListReader.read()).thenReturn(asList("OYELL_FVR00000123R", "11/13", "11/11/2011")).thenReturn(null);

    ShipmentLineItemDTO shipmentLineItemDTO = new ShipmentLineItemDTO();
    shipmentLineItemDTO.setOrderNumber("OYELL_FVR00000123R");
    shipmentLineItemDTO.setPackedDate("11/13");
    shipmentLineItemDTO.setShippedDate("11/11/2011");
    shipmentLineItemDTO.setOrderId(123L);
    ShipmentLineItem shipmentLineItem = mock(ShipmentLineItem.class);
    when(orderService.isShippable("OYELL_FVR00000123R")).thenReturn(true);

    when(shipmentLineItemTransformer.transform(shipmentLineItemDTO, "MM/yy", "dd/MM/yyyy", creationDate)).thenReturn(shipmentLineItem);
    when(applicationContext.getBean(ShipmentFileProcessor.class)).thenReturn(shipmentFileProcessor);
    Order order = new Order(123L);
    when(orderService.getByOrderNumber("OYELL_FVR00000123R")).thenReturn(order);

    shipmentFileProcessor.process(message);

    verify(shipmentService).save(shipmentLineItem);

    HashSet<String> orderNumbers = new HashSet<>();
    orderNumbers.add("OYELL_FVR00000123R");
    verify(shipmentFilePostProcessHandler).process(orderNumbers, shipmentFile, true);
  }
}
