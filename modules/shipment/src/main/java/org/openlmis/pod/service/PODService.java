/*
 *
 *  * This program is part of the OpenLMIS logistics management information system platform software.
 *  * Copyright © 2013 VillageReach
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  *  
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 *
 */

package org.openlmis.pod.service;

import org.openlmis.core.exception.DataException;
import org.openlmis.fulfillment.shared.FulfillmentPermissionService;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderStatus;
import org.openlmis.order.service.OrderService;
import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.pod.domain.OrderPODLineItem;
import org.openlmis.pod.repository.PODRepository;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.openlmis.core.domain.Right.MANAGE_POD;

@Service
public class PODService {

  @Autowired
  private PODRepository podRepository;

  @Autowired
  private OrderService orderService;

  @Autowired
  RequisitionService requisitionService;

  @Autowired
  private FulfillmentPermissionService fulfillmentPermissionService;

  @Transactional
  public void updatePOD(OrderPOD orderPod) {
    checkPermissions(orderPod);
    insertOrderPOD(orderPod);
    List<OrderPODLineItem> orderPodLineItems = orderPod.getPodLineItems();
    insertLineItems(orderPod, orderPodLineItems);
    updateOrderStatus(orderPod);
  }

  private void insertOrderPOD(OrderPOD orderPod) {
    Rnr requisition = requisitionService.getFullRequisitionById(orderPod.getOrderId());
    orderPod.fillPOD(requisition);
    fillPodLineItems(orderPod, requisition);
    insertPOD(orderPod);
  }

  private void fillPodLineItems(OrderPOD orderPod, Rnr requisition) {

  }

  public void updateOrderStatus(OrderPOD orderPod) {
    Order order = new Order(orderPod.getOrderId());
    order.setStatus(OrderStatus.RECEIVED);
    orderService.updateOrderStatus(order);
  }

  public void insertLineItems(OrderPOD orderPod, List<OrderPODLineItem> orderPodLineItems) {
    for (OrderPODLineItem orderPodLineItem : orderPodLineItems) {
      orderPodLineItem.setPodId(orderPod.getId());
      podRepository.insertPODLineItem(orderPodLineItem);
    }
  }

  public void checkPermissions(OrderPOD orderPod) {
    if (!fulfillmentPermissionService.hasPermission(orderPod.getCreatedBy(), getWarehouseForOrder(orderPod.getOrderId()), MANAGE_POD)) {
      throw new DataException("error.permission.denied");
    }
  }

  public OrderPOD getPODByOrderId(Long orderId) {
    return podRepository.getPODByOrderId(orderId);
  }

  public List<OrderPODLineItem> getNPodLineItems(String productCode, Rnr requisition, int n, Date startDate) {
    return podRepository.getNPodLineItems(productCode, requisition, n, startDate);
  }

  private Long getWarehouseForOrder(Long orderId) {
    Order order = orderService.getOrder(orderId);
    return order.getSupplyLine().getSupplyingFacility().getId();
  }

  public void insertPOD(OrderPOD orderPod) {
    podRepository.insertPOD(orderPod);
  }
}
