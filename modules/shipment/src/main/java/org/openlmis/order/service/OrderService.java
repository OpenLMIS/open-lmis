/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.service;

import lombok.NoArgsConstructor;
import org.openlmis.order.domain.Order;
import org.openlmis.order.repository.OrderRepository;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@NoArgsConstructor
public class OrderService {

  @Autowired
  private OrderRepository orderRepository;
  @Autowired
  private RequisitionService requisitionService;

  public void save(Order order) {
    orderRepository.save(order);
  }

  @Transactional
  public void convertToOrder(List<Rnr> rnrList, Integer userId) {
    requisitionService.releaseRequisitionsAsOrder(rnrList, userId);
    Order order;
    for (Rnr rnr : rnrList) {
      rnr.setModifiedBy(userId);
      order = new Order(rnr);
      orderRepository.save(order);
    }
  }

  public List<Order> getOrders() {
    List<Order> orders = orderRepository.getOrders();
    Rnr rnr;
    for (Order order : orders) {
      rnr = requisitionService.getFullRequisitionById(order.getRnr().getId());
      order.setRnr(rnr);
    }
    return orders;
  }

  public void updateFulfilledAndShipmentIdForOrders(List<Integer> orderIds, Boolean fulfilled, Integer shipmentId) {
    String orders = orderIds.toString().replace("[", "{").replace("]", "}");
    orderRepository.updateFulfilledAndShipmentIdForOrder(orders, fulfilled, shipmentId);
  }

  public Order getOrderForDownload(Integer id) {
    Order order = orderRepository.getById(id);
    Rnr requisition = requisitionService.getFullRequisitionById(order.getRnr().getId());
    removeUnorderedProducts(requisition);
    order.setRnr(requisition);
    return order;
  }

  private void removeUnorderedProducts(Rnr requisition) {
    List<RnrLineItem> fullSupplyLineItems = requisition.getFullSupplyLineItems();
    requisition.setFullSupplyLineItems(getLineItemsForOrder(fullSupplyLineItems));
    List<RnrLineItem> nonFullSupplyLineItems = requisition.getNonFullSupplyLineItems();
    requisition.setNonFullSupplyLineItems(getLineItemsForOrder(nonFullSupplyLineItems));
  }

  private List<RnrLineItem> getLineItemsForOrder(List<RnrLineItem> rnrLineItems) {
    List<RnrLineItem> lineItemsForOrder = new ArrayList<>();
    for(RnrLineItem rnrLineItem : rnrLineItems) {
      if(rnrLineItem.getPacksToShip() > 0){
        lineItemsForOrder.add(rnrLineItem);
      }
    }
    return lineItemsForOrder;
  }
}
