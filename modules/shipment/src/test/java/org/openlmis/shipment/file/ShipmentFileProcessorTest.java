/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.shipment.file;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.db.service.DbService;
import org.openlmis.order.dto.ShipmentLineItemDTO;
import org.openlmis.shipment.ShipmentLineItemTransformer;
import org.openlmis.shipment.domain.ShipmentConfiguration;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.openlmis.shipment.domain.ShipmentFileTemplate;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.file.csv.handler.ShipmentFilePostProcessHandler;
import org.openlmis.shipment.service.ShipmentFileTemplateService;
import org.openlmis.shipment.service.ShipmentService;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.parser.CSVParser;
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
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static org.openlmis.shipment.builder.ShipmentFileColumnBuilder.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.supercsv.prefs.CsvPreference.STANDARD_PREFERENCE;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({ShipmentFileProcessor.class, MessageBuilder.class})
public class ShipmentFileProcessorTest {
  @Mock
  private CSVParser csvParser;
  @Mock
  private RecordHandler shipmentRecordHandler;
  @Mock
  private ShipmentFilePostProcessHandler shipmentFilePostProcessHandler;
  @Mock
  private DbService dbService;
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

  @Rule
  public ExpectedException expectException = ExpectedException.none();

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

    expectException.expect(DataException.class);
    expectException.expectMessage("mandatory.data.missing");

    shipmentFileProcessor.process(message);

    verify(shipmentService, times(0)).insertShippedLineItem(any(ShipmentLineItem.class));
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

    when(mockedCsvListReader.read()).thenReturn(asList("", "field2")).thenReturn(null);

    shipmentFileProcessor.process(message);

    verify(mockedCsvListReader, times(0)).getHeader(true);
    verify(shipmentService, times(1)).insertShippedLineItem(any(ShipmentLineItem.class));
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
    when(shipmentLineItemTransformer.transform(shipmentLineItemDTO, "MM/yy", "dd/MM/yyyy")).thenReturn(shipmentLineItem);

    shipmentFileProcessor.process(message);

    verify(shipmentService).insertShippedLineItem(shipmentLineItem);

  }
}
