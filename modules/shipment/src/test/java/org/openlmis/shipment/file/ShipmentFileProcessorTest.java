/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.shipment.file;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.db.service.DbService;
import org.openlmis.shipment.domain.ShipmentConfiguration;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.openlmis.shipment.domain.ShipmentFileTemplate;
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
import static org.openlmis.shipment.builder.ShipmentFileColumnBuilder.columnPosition;
import static org.openlmis.shipment.builder.ShipmentFileColumnBuilder.mandatoryShipmentFileColumn;
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

  @InjectMocks
  private ShipmentFileProcessor shipmentFileProcessor;


  @Test
  public void shouldThrowErrorIfNotEnoughFieldsInShipmentFile() throws Exception {
    Message message = mock(Message.class);
    File shipmentFile = mock(File.class);
    FileInputStream shipmentInputStream = mock(FileInputStream.class);

    when(message.getPayload()).thenReturn(shipmentFile);
    whenNew(FileInputStream.class).withArguments(shipmentFile).thenReturn(shipmentInputStream);
    ShipmentConfiguration shipmentConfiguration = new ShipmentConfiguration();

    List<ShipmentFileColumn> shipmentFileColumnList = asList(
      make(a(mandatoryShipmentFileColumn, with(columnPosition, 1))),
      make(a(mandatoryShipmentFileColumn, with(columnPosition, 2))),
      make(a(mandatoryShipmentFileColumn, with(columnPosition, 3)))
    );

    ShipmentFileTemplate shipmentFileTemplate = new ShipmentFileTemplate(shipmentConfiguration, shipmentFileColumnList);

    when(shipmentFileTemplateService.get()).thenReturn(shipmentFileTemplate);

    CsvListReader mockedCsvListReader = mock(CsvListReader.class);
    when(mockedCsvListReader.read()).thenReturn(new ArrayList<String>()).thenReturn(null);
    when(mockedCsvListReader.length()).thenReturn(2);
    FileReader mockedFileReader = mock(FileReader.class);

    whenNew(FileReader.class).withArguments(shipmentFile).thenReturn(mockedFileReader);
    whenNew(CsvListReader.class).withArguments(mockedFileReader, STANDARD_PREFERENCE).thenReturn(mockedCsvListReader);

    shipmentFileProcessor.process(message);

    verify(shipmentService, times(0)).insertShippedLineItem(anyList());


  }
}
