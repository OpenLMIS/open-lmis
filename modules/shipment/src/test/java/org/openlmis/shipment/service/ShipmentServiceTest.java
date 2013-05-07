/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.order.domain.Order;
import org.openlmis.order.service.OrderService;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.domain.ShippedLineItem;
import org.openlmis.shipment.repository.ShipmentRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShipmentServiceTest {

  @Mock
  private ShipmentRepository shipmentRepository;
  @Mock
  private OrderService orderService;
  @InjectMocks
  private ShipmentService shipmentService;

  @Test
  public void shouldInsertShipment() throws Exception {
    ShippedLineItem shippedLineItem = new ShippedLineItem();
    shipmentService.insertShippedLineItem(shippedLineItem);
    verify(shipmentRepository).insertShippedLineItem(shippedLineItem);
  }

  @Test
  public void shouldInsertShipmentInfo() throws Exception {
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();
    shipmentService.insertShipmentFileInfo(shipmentFileInfo);
    verify(shipmentRepository).insertShipmentFileInfo(shipmentFileInfo);
  }

  @Test
  public void shouldUpdateOrders() throws Exception {
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();
    shipmentFileInfo.setId(1L);
    shipmentFileInfo.setProcessingError(FALSE);
    List<Long> orderIds = new ArrayList();
    orderIds.add(1L);

    shipmentService.updateStatusAndShipmentIdForOrders(orderIds, shipmentFileInfo);

    ArgumentMatcher<List<Order>> argumentMatcher = new ArgumentMatcher<List<Order>>() {
      @Override
      public boolean matches(Object argument) {
        List<Order> orders = (List<Order>) argument;
        return true;  //To change body of implemented methods use File | Settings | File Templates.
      }
    };
    verify(orderService).updateFulfilledAndShipmentIdForOrders(argThat(argumentMatcher));
  }


  @Test
  public void shouldGetProcessedTimeStampByOrderId() throws Exception {
    ShippedLineItem shippedLineItem = new ShippedLineItem();
    shippedLineItem.setRnrId(1L);
    Date expectedTimestamp = new Date();
    when(shipmentRepository.getProcessedTimeStamp(shippedLineItem)).thenReturn(expectedTimestamp);

    Date processTimeStamp = shipmentService.getProcessedTimeStamp(shippedLineItem);

    assertThat(processTimeStamp, is(expectedTimestamp));
    verify(shipmentRepository).getProcessedTimeStamp(shippedLineItem);
  }
}
