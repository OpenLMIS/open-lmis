/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.order.domain.Order;
import org.openlmis.order.repository.OrderRepository;
import org.openlmis.order.service.OrderService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.dto.RnrDTO;
import org.openlmis.rnr.service.RequisitionService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

  @Mock
  private OrderRepository orderRepository;
  @Mock
  private RequisitionService requisitionService;

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
  public void shouldGetOrders() throws Exception {
    List<Order> expectedOrders = new ArrayList<>();
    when(orderRepository.getOrders()).thenReturn(expectedOrders);

    List<Order> orders = orderService.getOrders();
    assertThat(orders, is(expectedOrders));
    verify(orderRepository).getOrders();
  }

  @Test
  public void shouldUpdateFulfilledAndShipmentIdForOrders() throws Exception {

    ArrayList<Integer> orders = new ArrayList<>();
    orders.add(1);
    orders.add(2);

    orderService.updateFulfilledAndShipmentIdForOrders(orders,true,1);

    verify(orderRepository,times(2)).updateFulfilledAndShipmentIdForOrder(anyInt(),anyBoolean(),anyInt());

  }
}
