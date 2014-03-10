/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.order.service;

import lombok.NoArgsConstructor;
import org.apache.commons.collections.Transformer;
import org.openlmis.core.domain.FulfillmentRoleAssignment;
import org.openlmis.core.domain.OrderConfiguration;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.repository.OrderConfigurationRepository;
import org.openlmis.core.service.RoleAssignmentService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static org.apache.commons.collections.CollectionUtils.collect;
import static org.apache.commons.lang.ArrayUtils.contains;
import static org.openlmis.order.domain.OrderStatus.*;

/**
 * Exposes the services for handling Order entity.
 */

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

  @Autowired
  private OrderEventService orderEventService;

  @Autowired
  RoleAssignmentService roleAssignmentService;

  public static String SUPPLY_LINE_MISSING_COMMENT = "order.ftpComment.supplyline.missing";

  private int pageSize;

  @Autowired
  public void setPageSize(@Value("${order.page.size}") String pageSize) {
    this.pageSize = Integer.parseInt(pageSize);
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
      if (order.getSupplyLine() == null) {
        status = TRANSFER_FAILED;
        order.setFtpComment(SUPPLY_LINE_MISSING_COMMENT);
      } else {
        status = order.getSupplyLine().getExportOrders() ? IN_ROUTE : READY_TO_PACK;
      }
      order.setStatus(status);
      orderRepository.save(order);
      order.setRnr(requisitionService.getFullRequisitionById(order.getRnr().getId()));
      orderEventService.notifyForStatusChange(order);
    }
  }

  public List<Order> getOrdersForPage(int page, Long userId, Right right) {
    List<Order> orders = orderRepository.getOrdersForPage(page, pageSize, userId, right);
    return fillOrders(orders);
  }

  public Order getOrder(Long id) {
    Order order = orderRepository.getById(id);
    if (order == null) {
      return null;
    }
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

  public void updateStatusAndShipmentIdForOrders(Set<Long> orderIds, ShipmentFileInfo shipmentFileInfo) {
    for (Long orderId : orderIds) {
      OrderStatus status = (shipmentFileInfo.isProcessingError()) ? RELEASED : PACKED;
      orderRepository.updateStatusAndShipmentIdForOrder(orderId, status, shipmentFileInfo.getId());
      Order order = orderRepository.getById(orderId);
      order.setRnr(requisitionService.getFullRequisitionById(order.getRnr().getId()));
      orderEventService.notifyForStatusChange(order);
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
    order.setRnr(requisitionService.getFullRequisitionById(order.getId()));
    orderEventService.notifyForStatusChange(order);
  }

  public boolean isShippable(Long orderId) {
    return hasStatus(orderId, RELEASED);
  }

  public Integer getNumberOfPages() {
    return orderRepository.getNumberOfPages(pageSize);
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public List<Order> searchByStatusAndRight(Long userId, Right right, List<OrderStatus> statuses) {
    List<FulfillmentRoleAssignment> fulfilmentRolesWithRight = roleAssignmentService.getFulfilmentRolesWithRight(userId, right);

    List<Order> orders = orderRepository.searchByWarehousesAndStatuses((List<Long>) collect(fulfilmentRolesWithRight, new Transformer() {
      @Override
      public Object transform(Object o) {
        return ((FulfillmentRoleAssignment) o).getFacilityId();
      }
    }), statuses);

    orders = fillOrders(orders);
    sort(orders);

    return orders;
  }

  private List<Order> fillOrders(List<Order> orders) {
    Rnr rnr;
    for (Order order : orders) {
      rnr = requisitionService.getFullRequisitionById(order.getRnr().getId());
      removeUnorderedProducts(rnr);
      order.setRnr(rnr);
    }
    return orders;
  }

  private List<RnrLineItem> getLineItemsForOrder(List<RnrLineItem> rnrLineItems) {
    List<RnrLineItem> lineItemsForOrder = new ArrayList<>();
    for (RnrLineItem rnrLineItem : rnrLineItems) {
      if (rnrLineItem.getPacksToShip() != null && rnrLineItem.getPacksToShip() > 0) {
        lineItemsForOrder.add(rnrLineItem);
      }
    }
    return lineItemsForOrder;
  }

  public boolean hasStatus(Long orderId, OrderStatus... statuses) {
    return contains(statuses, orderRepository.getStatus(orderId));
  }
}
