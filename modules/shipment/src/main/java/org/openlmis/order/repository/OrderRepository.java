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

import org.openlmis.core.exception.DataException;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderFileColumn;
import org.openlmis.order.domain.OrderStatus;
import org.openlmis.order.repository.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepository {

  @Autowired
  private OrderMapper orderMapper;

  public void save(Order order) {
    try {
      orderMapper.insert(order);
    } catch (DuplicateKeyException dke) {
      throw new DataException("msg.rnr.already.converted.to.order");
    }
  }

  public List<Order> getOrders() {
    return orderMapper.getAll();
  }

  public Order getById(Long id) {
    return orderMapper.getById(id);
  }

  public void updateStatusAndShipmentIdForOrder(Long orderId, OrderStatus status, Long shipmentId) {
    orderMapper.updateShipmentAndStatus(orderId, status, shipmentId);
  }

  public List<OrderFileColumn> getOrderFileTemplate() {
    return orderMapper.getOrderFileColumns();
  }

  //TODO update instead of delete and insert
  public void saveOrderFileColumns(List<OrderFileColumn> orderFileColumns, Long userId) {
    orderMapper.deleteOrderFileColumns();
    for (OrderFileColumn column : orderFileColumns) {
      column.setModifiedBy(userId);
      orderMapper.insertOrderFileColumn(column);
    }
  }

  public void updateOrderStatus(Order order) {
    orderMapper.updateOrderStatus(order);
  }

  public OrderStatus getStatus(long orderId) {
    return orderMapper.getStatus(orderId);
  }
}
