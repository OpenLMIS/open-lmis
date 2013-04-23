/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 20.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.file.csv.handler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.service.ShipmentService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ShipmentFilePostProcessHandler.class)
public class ShipmentFilePostProcessHandlerTest {
  @Mock
  private ShipmentService shipmentService;
  @Mock
  private MessageChannel ftpOutputChannel;
  @Mock
  private ShipmentFileReader shipmentFileReader;

  @InjectMocks
  private ShipmentFilePostProcessHandler shipmentFilePostProcessHandler;

  @Test
  public void shouldAddShipmentFileInfo() throws Exception {
    Set<Integer> orderIds = new HashSet<>();
    boolean processingError = false;
    File shipmentFile = mock(File.class);
    String fileName = "FileName";
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();

    when(shipmentFile.getName()).thenReturn(fileName);
    whenNew(ShipmentFileInfo.class).withArguments(fileName, processingError).thenReturn(shipmentFileInfo);

    when(shipmentFileReader.getOrderIds(shipmentFile)).thenReturn(orderIds);

    shipmentFilePostProcessHandler.process(shipmentFile, processingError);

    verify(shipmentService).insertShipmentFileInfo(shipmentFileInfo);
    verify(shipmentService).updateFulfilledFlagAndShipmentIdForOrders(new ArrayList(orderIds), shipmentFileInfo);
    verify(ftpOutputChannel, never()).send(any(Message.class));
  }

  @Test
  public void shouldAddShipmentFileInfoAndSendFileToFtpOutputChannelWhenFileHasError() throws Exception {
    Set<Integer> orderIds = new HashSet<>();
    boolean processingError = true;
    File shipmentFile = mock(File.class);
    String fileName = "FileName";
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();

    when(shipmentFile.getName()).thenReturn(fileName);
    whenNew(ShipmentFileInfo.class).withArguments(fileName, processingError).thenReturn(shipmentFileInfo);
    when(shipmentFileReader.getOrderIds(shipmentFile)).thenReturn(orderIds);

    shipmentFilePostProcessHandler.process(shipmentFile, processingError);

    verify(shipmentService).insertShipmentFileInfo(shipmentFileInfo);
    verify(shipmentService).updateFulfilledFlagAndShipmentIdForOrders(new ArrayList(orderIds), shipmentFileInfo);
    verify(ftpOutputChannel).send(any(Message.class));
  }
}
