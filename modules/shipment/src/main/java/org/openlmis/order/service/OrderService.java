/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.OrderConfiguration;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.repository.OrderConfigurationRepository;
import org.openlmis.core.service.SupplyLineService;
import org.openlmis.order.domain.DateFormat;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderStatus;
import org.openlmis.order.dto.OrderFileTemplateDTO;
import org.openlmis.order.repository.OrderRepository;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.service.RequisitionService;
import org.openlmis.shipment.domain.ShipmentFileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Arrays.asList;
import static org.openlmis.order.domain.OrderStatus.*;

@Service
@NoArgsConstructor
public class OrderService {

  @Autowired
  private OrderConfigurationRepository orderConfigurationRepository;
  @Autowired
  private OrderRepository orderRepository;
  @Autowired
  private RequisitionService requisitionService;
  @Autowired
  private SupplyLineService supplyLineService;

  public static String SUPPLY_LINE_MISSING_COMMENT = "order.ftpComment.supplyline.missing";

  public void save(Order order) {
    orderRepository.save(order);
  }

  @Transactional
  public void convertToOrder(List<Rnr> rnrList, Long userId) {
    requisitionService.releaseRequisitionsAsOrder(rnrList, userId);
    Order order;
    for (Rnr rnr : rnrList) {
      rnr = requisitionService.getLWById(rnr.getId());
      rnr.setModifiedBy(userId);
      order = new Order(rnr);
      order.setSupplyLine(supplyLineService.getSupplyLineBy(new SupervisoryNode(rnr.getSupervisoryNodeId()), rnr.getProgram()));
      OrderStatus status;
      if (order.getSupplyLine()==null) {
        status = TRANSFER_FAILED;
        order.setFtpComment(SUPPLY_LINE_MISSING_COMMENT);
      }
      else {
        status = order.getSupplyLine().getExportOrders() ? IN_ROUTE : READY_TO_PACK;
      }
      order.setStatus(status);
      orderRepository.save(order);
    }
  }

  public List<Order> getOrders() {
    List<Order> orders = orderRepository.getOrders();
    Rnr rnr;
    for (Order order : orders) {
      rnr = requisitionService.getFullRequisitionById(order.getRnr().getId());
      removeUnorderedProducts(rnr);
      order.setRnr(rnr);
    }
    return orders;
  }

  public Order getOrderForDownload(Long id) {
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
    for (RnrLineItem rnrLineItem : rnrLineItems) {
      if (rnrLineItem.getPacksToShip() > 0) {
        lineItemsForOrder.add(rnrLineItem);
      }
    }
    return lineItemsForOrder;
  }

  public void updateStatusAndShipmentIdForOrders(Set<Long> orderIds, ShipmentFileInfo shipmentFileInfo) {
    for (Long orderId : orderIds) {
      OrderStatus status = (shipmentFileInfo.isProcessingError()) ? RELEASED : PACKED;
      orderRepository.updateStatusAndShipmentIdForOrder(orderId, status, shipmentFileInfo.getId());
    }
  }

  public OrderFileTemplateDTO getOrderFileTemplateDTO() {
    return new OrderFileTemplateDTO(orderConfigurationRepository.getConfiguration(), orderRepository.getOrderFileTemplate());
  }

  @Transactional
  public void saveOrderFileTemplate(OrderFileTemplateDTO orderFileTemplateDTO, Long userId) {
    OrderConfiguration orderConfiguration = orderFileTemplateDTO.getOrderConfiguration();
    orderConfiguration.setModifiedBy(userId);
    orderConfigurationRepository.update(orderConfiguration);
    orderRepository.saveOrderFileColumns(orderFileTemplateDTO.getOrderFileColumns(), userId);
  }

  public Set<DateFormat> getAllDateFormats() {
    TreeSet<DateFormat> dateFormats = new TreeSet<>();
    dateFormats.addAll(asList(DateFormat.values()));
    return dateFormats;
  }

  public void updateOrderStatus(Order order) {
    orderRepository.updateOrderStatus(order);
  }

  public boolean isShippable(Long orderId) {
    List<OrderStatus> shippableOrderStatuses = asList(RELEASED);
    return shippableOrderStatuses.contains(orderRepository.getStatus(orderId));
  }
}
