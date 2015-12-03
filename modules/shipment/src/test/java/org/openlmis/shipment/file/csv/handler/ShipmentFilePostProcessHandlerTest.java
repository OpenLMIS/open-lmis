/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.file.csv.handler;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.service.OrderService;
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

  @Mock(name = "ftpErrorChannel")
  private MessageChannel ftpErrorChannel;

  @Mock(name = "ftpArchiveOutputChannel")
  private MessageChannel ftpArchiveOutputChannel;

  @Mock
  private OrderService orderService;

  @InjectMocks
  private ShipmentFilePostProcessHandler shipmentFilePostProcessHandler;


  @Before
  public void setUp() throws Exception {
    initMocks(this);
  }

  @Test
  public void shouldAddShipmentFileInfo() throws Exception {
    Set<String> orderNumbers = new HashSet<>();
    boolean success = true;
    Boolean failure = false;
    File shipmentFile = mock(File.class);
    String fileName = "FileName";
    ShipmentFileInfo shipmentFileInfo = mock(ShipmentFileInfo.class);

    when(shipmentFile.getName()).thenReturn(fileName);
    whenNew(ShipmentFileInfo.class).withArguments(fileName, failure).thenReturn(shipmentFileInfo);

    shipmentFilePostProcessHandler.process(orderNumbers, shipmentFile, success);

    verify(shipmentService).insertShipmentFileInfo(shipmentFileInfo);
    verify(orderService).updateStatusAndShipmentIdForOrders(orderNumbers, shipmentFileInfo);
    verify(ftpErrorChannel, never()).send(any(Message.class));
    verify(ftpArchiveOutputChannel).send(any(Message.class));
  }

  @Test
  public void shouldAddShipmentFileInfoAndSendFileToFtpOutputChannelWhenFileHasError() throws Exception {
    Set<String> orderNumbers = new HashSet<>();
    boolean failure = true;
    boolean success = false;
    File shipmentFile = mock(File.class);
    String fileName = "FileName";
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();

    when(shipmentFile.getName()).thenReturn(fileName);
    whenNew(ShipmentFileInfo.class).withArguments(fileName, failure).thenReturn(shipmentFileInfo);

    shipmentFilePostProcessHandler.process(orderNumbers, shipmentFile, success);

    verify(shipmentService).insertShipmentFileInfo(shipmentFileInfo);
    verify(orderService).updateStatusAndShipmentIdForOrders(orderNumbers, shipmentFileInfo);
    verify(ftpErrorChannel).send(any(Message.class));
    verify(ftpArchiveOutputChannel, never()).send(any(Message.class));
  }
}
