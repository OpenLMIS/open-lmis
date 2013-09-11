/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.repository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderFileColumn;
import org.openlmis.order.domain.OrderStatus;
import org.openlmis.order.repository.mapper.OrderMapper;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.*;
import static org.openlmis.order.domain.OrderStatus.RELEASED;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class OrderRepositoryTest {

  @Rule
  public ExpectedException exception = none();

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
  public void shouldUpdateStatusAndShipmentIdForOrder() throws Exception {
    Long orderId = 123L;
    Long shipmentId = 456L;
    OrderStatus status = RELEASED;

    orderRepository.updateStatusAndShipmentIdForOrder(orderId, status, shipmentId);

    verify(orderMapper).updateShipmentAndStatus(orderId, status, shipmentId);
  }

  @Test
  public void shouldGetOrderById() throws Exception {
    Order expectedOrder = new Order();
    when(orderMapper.getById(1L)).thenReturn(expectedOrder);
    Order savedOrder = orderRepository.getById(1L);
    verify(orderMapper).getById(1L);
    assertThat(savedOrder, is(expectedOrder));
  }

  @Test
  public void shouldGetOrderFileColumns() {
    OrderFileColumn orderFileColumn = new OrderFileColumn();
    orderFileColumn.setDataFieldLabel("facility.code");
    orderFileColumn.setColumnLabel("Facility code");
    orderFileColumn.setPosition(1);
    orderFileColumn.setIncludeInOrderFile(false);
    List<OrderFileColumn> orderFileColumns = asList(orderFileColumn);
    when(orderMapper.getOrderFileColumns()).thenReturn(orderFileColumns);
    assertThat(orderRepository.getOrderFileTemplate(), is(orderFileColumns));
    verify(orderMapper).getOrderFileColumns();
  }

  @Test
  public void shouldSaveOrderFileColumns() throws Exception {
    OrderFileColumn orderFileColumn = new OrderFileColumn();
    List<OrderFileColumn> orderFileColumns = asList(orderFileColumn);
    Long userId = 1L;
    orderRepository.saveOrderFileColumns(orderFileColumns, userId);
    verify(orderMapper).deleteOrderFileColumns();
    verify(orderMapper, times(1)).insertOrderFileColumn(orderFileColumn);
  }

  @Test
  public void shouldThrowDataExceptionForDuplicateRnrToOrderConversion() {
    Order order = new Order();

    doThrow(new DuplicateKeyException("Error updating database.")).when(orderMapper).insert(order);

    exception.expect(DataException.class);
    exception.expectMessage("msg.rnr.already.converted.to.order");

    orderRepository.save(order);

    verify(orderMapper).insert(order);
  }

  @Test
  public void shouldUpdateOrderStatusAndFtpComment() throws Exception {
    Order order = new Order();

    orderRepository.updateOrderStatus(order);

    verify(orderMapper).updateOrderStatus(order);
  }

}
