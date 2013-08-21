/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.repository;

import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderFileColumn;
import org.openlmis.order.repository.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepository {

  @Autowired
  private OrderMapper orderMapper;

  public void save(Order order) {
    orderMapper.insert(order);
  }

  public List<Order> getOrders() {
    return orderMapper.getAll();
  }

  public Order getById(Long id) {
    return orderMapper.getById(id);
  }

  public void updateStatusAndShipmentIdForOrder(List<Order> orders) {
    for (Order order : orders) {
      orderMapper.updateShipmentInfo(order);
    }
  }

  public List<OrderFileColumn> getOrderFileTemplate() {
    return orderMapper.getOrderFileColumns();
  }

  public void saveOrderFileColumns(List<OrderFileColumn> orderFileColumns) {
    orderMapper.deleteOrderFileColumns();
    for(OrderFileColumn column : orderFileColumns) {
      orderMapper.insertOrderFileColumn(column);
    }
  }
}
