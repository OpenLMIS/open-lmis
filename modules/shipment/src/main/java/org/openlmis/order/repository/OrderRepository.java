/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package org.openlmis.order.repository;

import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderFileColumn;
import org.openlmis.order.domain.OrderStatus;
import org.openlmis.order.repository.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository class for Order related database operations.
 */

@Repository
public class OrderRepository {

  @Autowired
  private OrderMapper orderMapper;

  @Autowired
  CommaSeparator commaSeparator;

  public void save(Order order) {
    try {
      orderMapper.insert(order);
    } catch (DuplicateKeyException dke) {
      throw new DataException("msg.rnr.already.converted.to.order");
    }
  }

  public List<Order> getOrdersForPage(int page, int pageSize, Long userId, String rightName) {
    return orderMapper.getOrders(pageSize, (page - 1) * pageSize, userId, rightName);
  }

  public List<Order> getOrdersForPage(int page, int pageSize, Long userId, String right, Long supplyDepot, Long program, Long period) {
    return orderMapper.getOrdersByDepot(pageSize, (page - 1) * pageSize, userId, right, supplyDepot, program, period);
  }

  public Order getById(Long id) {
    return orderMapper.getById(id);
  }

  public void updateStatusAndShipmentIdForOrder(String orderNumber, OrderStatus status, Long shipmentId) {
    orderMapper.updateShipmentAndStatus(orderNumber, status, shipmentId);
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

  public OrderStatus getStatus(String orderNumber) {
    return orderMapper.getStatus(orderNumber);
  }

  public Integer getNumberOfPages(int pageSize) {
    return orderMapper.getNumberOfPages(pageSize);
  }

  public Integer getNumberOfPages(int pageSize, Long supplyDepot, Long program, Long period) {
    return orderMapper.getNumberOfPagesByDepot(pageSize, supplyDepot, program, period);
  }

  public List<Order> searchByWarehousesAndStatuses(List<Long> facilityIds, List<OrderStatus> orderStatuses, Long programId, Long facilityId) {
    if (facilityId != 0 && facilityId != null) {
      return orderMapper.getByWarehouseIdsAndStatusesByFacility(format(facilityIds.toString()), format(orderStatuses.toString()), programId, facilityId);
    }
    return orderMapper.getByWarehouseIdsAndStatuses(format(facilityIds.toString()), format(orderStatuses.toString()), programId);
  }

  private String format(String listString) {
    return listString.replace('[', '{').replace(']', '}');
  }

  public Order getByOrderNumber(String orderNumber) {
    return orderMapper.getByOrderNumber(orderNumber);
  }
}
