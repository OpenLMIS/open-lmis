/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.order.domain.Order;
import org.openlmis.order.repository.mapper.OrderMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderRepositoryTest {

  @Mock
  private OrderMapper orderMapper;
  @InjectMocks
  private OrderRepository orderRepository;

  @Test
  public void shouldSaveOrder() throws Exception {

    Order order = new Order();
    orderRepository.save(order);
    verify(orderMapper).insert(order);
  }

  @Test
  public void shouldGetOrders() {
    List<Order> expectedOrders = new ArrayList<>();
    when(orderMapper.getAll()).thenReturn(expectedOrders);

    List<Order> orders = orderRepository.getOrders();

    verify(orderMapper).getAll();
    assertThat(orders, is(expectedOrders));
  }

  @Test
  public void shouldUpdateFulfilledAndShipmentIdForOrder() throws Exception {
    doNothing().when(orderMapper).updateFullfilledFlagAndShipmentId("{1}",false,1);
    orderRepository.updateFulfilledAndShipmentIdForOrder("{1}", false, 1);

    verify(orderMapper).updateFullfilledFlagAndShipmentId("{1}", false, 1);

  }

  @Test
  public void shouldGetOrderById() throws Exception {
    Order expectedOrder = new Order();
    when(orderMapper.getById(1)).thenReturn(expectedOrder);
    Order savedOrder = orderRepository.getById(1);
    verify(orderMapper).getById(1);
    assertThat(savedOrder, is(expectedOrder));
  }
}
