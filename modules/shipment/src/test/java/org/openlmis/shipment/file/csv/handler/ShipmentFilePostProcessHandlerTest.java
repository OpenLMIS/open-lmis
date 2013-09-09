/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.file.csv.handler;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.handler.ShipmentFilePostProcessHandler;
import org.openlmis.shipment.service.ShipmentService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(ShipmentFilePostProcessHandler.class)
public class ShipmentFilePostProcessHandlerTest {

  @Mock
  private ShipmentService shipmentService;

  @Mock(name = "ftpOutputChannel")
  private MessageChannel ftpOutputChannel;

  @Mock(name = "ftpArchiveOutputChannel")
  private MessageChannel ftpArchiveOutputChannel;

  @InjectMocks
  private ShipmentFilePostProcessHandler shipmentFilePostProcessHandler;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
  }

  @Test
  public void shouldAddShipmentFileInfo() throws Exception {
    Set<Long> orderIds = new HashSet<>();
    boolean processingError = false;
    File shipmentFile = mock(File.class);
    String fileName = "FileName";
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();

    when(shipmentFile.getName()).thenReturn(fileName);
    whenNew(ShipmentFileInfo.class).withArguments(fileName, processingError).thenReturn(shipmentFileInfo);

    shipmentFilePostProcessHandler.process(orderIds, shipmentFile, processingError);

    verify(shipmentService).insertShipmentFileInfo(shipmentFileInfo);
    verify(shipmentService).updateStatusAndShipmentIdForOrders(orderIds, shipmentFileInfo);
    verify(ftpOutputChannel, never()).send(any(Message.class));
    verify(ftpArchiveOutputChannel).send(any(Message.class));
  }

  @Test
  public void shouldAddShipmentFileInfoAndSendFileToFtpOutputChannelWhenFileHasError() throws Exception {
    Set<Long> orderIds = new HashSet<>();
    boolean processingError = true;
    File shipmentFile = mock(File.class);
    String fileName = "FileName";
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();

    when(shipmentFile.getName()).thenReturn(fileName);
    whenNew(ShipmentFileInfo.class).withArguments(fileName, processingError).thenReturn(shipmentFileInfo);

    shipmentFilePostProcessHandler.process(orderIds, shipmentFile, processingError);

    verify(shipmentService).insertShipmentFileInfo(shipmentFileInfo);
    verify(shipmentService).updateStatusAndShipmentIdForOrders(orderIds, shipmentFileInfo);
    verify(ftpOutputChannel).send(any(Message.class));
    verify(ftpArchiveOutputChannel, never()).send(any(Message.class));
  }
}
