/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
import static org.openlmis.core.domain.RightName.VIEW_ORDER;
import static org.openlmis.order.domain.OrderStatus.READY_TO_PACK;
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
  public void shouldGetOrdersGivenPageNumberAndSize() {
    List<Order> expectedOrders = new ArrayList<>();
    when(orderMapper.getOrders(3, 3, 1l, VIEW_ORDER)).thenReturn(expectedOrders);

    List<Order> orders = orderRepository.getOrdersForPage(2, 3, 1l, VIEW_ORDER);

    verify(orderMapper).getOrders(3, 3, 1l, VIEW_ORDER);
    assertThat(orders, is(expectedOrders));
  }

  @Test
  public void shouldUpdateStatusAndShipmentIdForOrder() throws Exception {
    String orderNumber = "OYELL_FVR00000123R";
    Long shipmentId = 456L;
    OrderStatus status = RELEASED;

    orderRepository.updateStatusAndShipmentIdForOrder(orderNumber, status, shipmentId);

    verify(orderMapper).updateShipmentAndStatus(orderNumber, status, shipmentId);
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

  @Test
  public void shouldGetTotalNumberOfPages() throws Exception {
    when(orderMapper.getNumberOfPages(4)).thenReturn(4);

    Integer numberOfPages = orderRepository.getNumberOfPages(4);

    assertThat(numberOfPages, is(4));
  }

  @Test
  public void shouldSearchOrdersByWarehouseIdsAndStatuses() throws Exception {
    List<Order> expectedOrders = asList(new Order());
    when(orderMapper.getByWarehouseIdsAndStatuses("{3, 6}", "{RELEASED, READY_TO_PACK}")).thenReturn(expectedOrders);

    List<Order> actualOrders = orderRepository.searchByWarehousesAndStatuses(asList(3l, 6l), asList(RELEASED, READY_TO_PACK));

    verify(orderMapper).getByWarehouseIdsAndStatuses("{3, 6}", "{RELEASED, READY_TO_PACK}");
    assertThat(actualOrders, is(expectedOrders));
  }
}
