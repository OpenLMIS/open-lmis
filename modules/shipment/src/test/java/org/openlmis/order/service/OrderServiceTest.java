/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.order.domain.Order;
import org.openlmis.order.repository.OrderRepository;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RequisitionService;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.openlmis.rnr.builder.RequisitionBuilder.*;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

  @Mock
  private OrderRepository orderRepository;
  @Mock
  private RequisitionService requisitionService;

  @SuppressWarnings("unuse")
  @InjectMocks
  private OrderService orderService;


  @Test
  public void shouldSaveOrder() throws Exception {
    Order order = new Order();
    orderService.save(order);
    verify(orderRepository).save(order);
  }

  @Test
  public void shouldConvertRequisitionsToOrder() throws Exception {
    List<Rnr> rnrList = new ArrayList<>();
    Rnr rnr = new Rnr();
    rnrList.add(rnr);
    Integer userId = 1;
    orderService.convertToOrder(rnrList, userId);
    Order order = new Order(rnr);
    whenNew(Order.class).withArguments(rnr).thenReturn(order);
    verify(requisitionService).releaseRequisitionsAsOrder(rnrList, userId);
    verify(orderRepository).save(order);
  }

  @Test
  public void shouldGetOrdersFilledWithRequisition() throws Exception {
    Rnr rnr1 = make(a(defaultRnr, with(id, 78)));
    final Order order1 = new Order();
    order1.setRnr(rnr1);

    Rnr rnr2 = make(a(defaultRnr, with(periodId, 2), with(id, 72)));

    final Order order2 = new Order();
    order2.setRnr(rnr2);

    List<Order> expectedOrders = new ArrayList<Order>() {{
      add(order1);
      add(order2);
    }};

    when(orderRepository.getOrders()).thenReturn(expectedOrders);
    when(requisitionService.getFullRequisitionById(rnr1.getId())).thenReturn(rnr1);
    when(requisitionService.getFullRequisitionById(rnr2.getId())).thenReturn(rnr2);

    List<Order> orders = orderService.getOrders();
    assertThat(orders, is(expectedOrders));
    verify(orderRepository).getOrders();
    verify(requisitionService).getFullRequisitionById(rnr1.getId());
    verify(requisitionService).getFullRequisitionById(rnr2.getId());
  }

  @Test
  public void shouldUpdateFulfilledAndShipmentIdForOrders() throws Exception {
    ArrayList<Integer> orders = new ArrayList<>();
    orders.add(1);
    orders.add(2);

    orderService.updateFulfilledAndShipmentIdForOrders(orders, true, 1);

    verify(orderRepository, times(2)).updateFulfilledAndShipmentIdForOrder(anyInt(), anyBoolean(), anyInt());
  }

  @Test
  public void shouldGetOrderById() {
    Integer orderId = 1;
    Integer rnrId = 1;
    Order order = new Order();
    Rnr rnr = new Rnr();
    rnr.setId(rnrId);
    order.setRnr(rnr);
    when(orderRepository.getById(orderId)).thenReturn(order);
    Order expectedOrder = orderService.getById(orderId);
    verify(orderRepository).getById(orderId);
    verify(requisitionService).getFullRequisitionById(rnr.getId());
    assertThat(expectedOrder, is(order));
  }
}
