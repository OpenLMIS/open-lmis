/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.service;


import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProductService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.service.OrderService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RequisitionService;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.openlmis.shipment.repository.ShipmentRepository;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.lang.Boolean.FALSE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.shipment.builder.ShipmentLineItemBuilder.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ShipmentServiceTest {

  @Mock
  private ShipmentRepository shipmentRepository;
  @Mock
  private OrderService orderService;
  @Mock
  private RequisitionService requisitionService;
  @Mock
  private ProductService productService;
  @InjectMocks
  private ShipmentService shipmentService;

  @Rule
  public ExpectedException exException = ExpectedException.none();

  @Test
  public void shouldInsertShipment() throws Exception {
    ShipmentLineItem shipmentLineItem = make(a(defaultShipmentLineItem,
      with(productCode, "P10"),
      with(orderId, 1L),
      with(quantityShipped, 500)));


    when(requisitionService.getLWById(1l)).thenReturn(new Rnr());
    when(productService.getIdForCode("P10")).thenReturn(1l);

    shipmentService.insertShippedLineItem(shipmentLineItem);

    verify(requisitionService).getLWById(1l);
    verify(productService).getIdForCode("P10");
    verify(shipmentRepository).insertShippedLineItem(shipmentLineItem);
  }

  @Test
  public void shouldNotInsertShipmentIfOrderIdIsNotValid() throws Exception {
    ShipmentLineItem shipmentLineItem = make(a(defaultShipmentLineItem,
      with(productCode, "P10"),
      with(orderId, 1L),
      with(quantityShipped, 500)));

    when(requisitionService.getLWById(1l)).thenReturn(null);
    when(productService.getIdForCode("P10")).thenReturn(1l);


    exException.expect(DataException.class);
    exException.expectMessage("error.unknown.order");

    shipmentService.insertShippedLineItem(shipmentLineItem);
  }

  @Test
  public void shouldNotInsertShipmentIfProductCodeIsNotValid() throws Exception {

    ShipmentLineItem shipmentLineItem = make(a(defaultShipmentLineItem,
      with(productCode, "P10"),
      with(orderId, 1L),
      with(quantityShipped, 500)));

    when(requisitionService.getLWById(1l)).thenReturn(new Rnr());
    when(productService.getIdForCode("P10")).thenReturn(null);


    exException.expect(DataException.class);
    exException.expectMessage("error.unknown.product");

    shipmentService.insertShippedLineItem(shipmentLineItem);
  }

  @Test
  public void shouldNotInsertShipmentIfQuantityNegative() throws Exception {
    ShipmentLineItem shipmentLineItem = make(a(defaultShipmentLineItem,
      with(productCode, "P10"),
      with(orderId, 1L),
      with(quantityShipped, -1)));

    when(productService.getIdForCode("P10")).thenReturn(1l);
    exException.expect(DataException.class);
    exException.expectMessage("error.negative.shipped.quantity");

    shipmentService.insertShippedLineItem(shipmentLineItem);
  }

  @Test
  public void shouldInsertShipmentInfo() throws Exception {
    ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();
    shipmentService.insertShipmentFileInfo(shipmentFileInfo);
    verify(shipmentRepository).insertShipmentFileInfo(shipmentFileInfo);
  }

  @Test
  public void shouldUpdateOrders() throws Exception {
    final ShipmentFileInfo shipmentFileInfo = new ShipmentFileInfo();
    shipmentFileInfo.setId(1L);
    shipmentFileInfo.setProcessingError(FALSE);
    Set<Long> orderIds = new HashSet<>();
    orderIds.add(1L);

    shipmentService.updateStatusAndShipmentIdForOrders(orderIds, shipmentFileInfo);

    final ArgumentMatcher<List<Order>> argumentMatcher = new ArgumentMatcher<List<Order>>() {
      @Override
      public boolean matches(Object argument) {
        List<Order> orders = (List<Order>) argument;
        Order order = orders.get(0);
        return order.getShipmentFileInfo().equals(shipmentFileInfo) && order.getRnr().getId().equals(1L);
      }
    };
    verify(orderService).updateFulfilledAndShipmentIdForOrders(argThat(argumentMatcher));
  }


  @Test
  public void shouldGetProcessedTimeStampByOrderId() throws Exception {
    ShipmentLineItem shipmentLineItem = new ShipmentLineItem();
    shipmentLineItem.setOrderId(1L);
    Date expectedTimestamp = new Date();
    when(shipmentRepository.getProcessedTimeStamp(shipmentLineItem)).thenReturn(expectedTimestamp);

    Date processTimeStamp = shipmentService.getProcessedTimeStamp(shipmentLineItem);

    assertThat(processTimeStamp, is(expectedTimestamp));
    verify(shipmentRepository).getProcessedTimeStamp(shipmentLineItem);
  }
}
