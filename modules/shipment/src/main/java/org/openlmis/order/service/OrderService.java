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
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.OrderConfigurationRepository;
import org.openlmis.core.service.*;
import org.openlmis.fulfillment.shared.FulfillmentPermissionService;
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
import org.springframework.security.access.AccessDeniedException;
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
import static org.openlmis.core.domain.RightName.FACILITY_FILL_SHIPMENT;
import static org.openlmis.core.domain.RightName.MANAGE_POD;
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

  @Autowired
  FulfillmentPermissionService fulfillmentPermissionService;

  @Autowired
  private ProgramService programService;

  @Autowired
  private StatusChangeEventService statusChangeEventService;

  @Autowired
  private UserService userService;

  public static String SUPPLY_LINE_MISSING_COMMENT = "order.ftpComment.supplyline.missing";

  private Integer pageSize;

  @Autowired
  public void setPageSize(@Value("${order.page.size}") String pageSize) {
    this.pageSize = Integer.parseInt(pageSize);
  }

  @Transactional
  public void convertToOrder(List<Rnr> rnrList, Long userId) {
    requisitionService.releaseRequisitionsAsOrder(rnrList, userId);
    Order order;
    for (Rnr rnr : rnrList) {
      Long depotId = rnr.getSupplyingDepotId();
      rnr = requisitionService.getLWById(rnr.getId());
      rnr.setModifiedBy(userId);
      order = new Order(rnr);
      SupplyLine supplyLine = supplyLineService.getSupplyLineBy(new SupervisoryNode(rnr.getSupervisoryNodeId()), rnr.getProgram());
      if(depotId != null && supplyLine.getSupplyingFacility().getId() != depotId){
        supplyLine = supplyLineService.getByFacilityProgram(depotId, rnr.getProgram().getId());
      }
      order.setSupplyLine(supplyLine);
      if (!fulfillmentPermissionService.hasPermissionOnWarehouse(userId, supplyLine.getSupplyingFacility().getId(), RightName.CONVERT_TO_ORDER)) {
        throw new AccessDeniedException("user.not.authorized");
      }
      OrderStatus status;
      if (order.getSupplyLine() == null) {
        status = TRANSFER_FAILED;
        order.setFtpComment(SUPPLY_LINE_MISSING_COMMENT);
      } else {
        status = order.getSupplyLine().getExportOrders() ? IN_ROUTE : READY_TO_PACK;
      }
      order.setStatus(status);
      order.setOrderNumber(getOrderNumberConfiguration().getOrderNumberFor(rnr.getId(), programService.getById(rnr.getProgram().getId()), rnr.isEmergency()));
      orderRepository.save(order);
      order.setRnr(requisitionService.getFullRequisitionById(order.getRnr().getId()));

      sendOrderStatusChangeMail(order);
      orderEventService.notifyForStatusChange(order);
    }
  }

  private void sendOrderStatusChangeMail(Order order) {
    Rnr requisition = order.getRnr();
    List<User> usersWithRight = new ArrayList<>();
    Facility supplyingFacility = order.getSupplyLine().getSupplyingFacility();

    if (order.getStatus().equals(READY_TO_PACK)) {
      usersWithRight = userService.getUsersWithRightOnWarehouse(supplyingFacility.getId(), FACILITY_FILL_SHIPMENT);
    } else if (order.getStatus().equals(PACKED)) {
      usersWithRight = userService.getUsersWithRightOnWarehouse(supplyingFacility.getId(), MANAGE_POD);
    }

    ArrayList<User> activeUsersWithRight = userService.filterForActiveUsers(usersWithRight);
    statusChangeEventService.notifyUsers(activeUsersWithRight, null, requisition.getFacility(),
      requisition.getProgram(), requisition.getPeriod(), order.getStatus().toString());
  }

  public List<Order> getOrdersForPage(int page, Long userId, String rightName) {
    List<Order> orders = orderRepository.getOrdersForPage(page, pageSize, userId, rightName);
    return fillOrders(orders);
  }

  public List<Order> getOrdersForPage(int page, Long userId, String right, Long supplyDepot, Long program, Long period) {
    List<Order> orders = orderRepository.getOrdersForPage(page, pageSize, userId, right, supplyDepot, program, period);
    return fillOrders(orders);
  }

  public Order getOrder(Long id) {
    Order order = orderRepository.getById(id);
    if (order == null) {
      return null;
    }
    Rnr requisition = requisitionService.getFullRequisitionById(order.getRnr().getId());
    requisition.setProgram(programService.getById(requisition.getProgram().getId()));
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

  public void updateStatusAndShipmentIdForOrders(Set<String> orderNumbers, ShipmentFileInfo shipmentFileInfo) {
    for (String orderNumber : orderNumbers) {
      OrderStatus status = (shipmentFileInfo.isProcessingError()) ? RELEASED : PACKED;
      orderRepository.updateStatusAndShipmentIdForOrder(orderNumber, status, shipmentFileInfo.getId());
      Order order = orderRepository.getByOrderNumber(orderNumber);
      order.setRnr(requisitionService.getFullRequisitionById(order.getRnr().getId()));

      sendOrderStatusChangeMail(order);
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

  public OrderNumberConfiguration getOrderNumberConfiguration() {
    return orderConfigurationRepository.getOrderNumberConfiguration();
  }

  @Transactional
  public void updateOrderNumberConfiguration(OrderNumberConfiguration orderNumberConfiguration) {
    orderNumberConfiguration.validate();
    orderConfigurationRepository.updateOrderNumberConfiguration(orderNumberConfiguration);
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

  public boolean isShippable(String orderNumber) {
    return hasStatus(orderNumber, RELEASED);
  }

  public Integer getNumberOfPages() {
    return orderRepository.getNumberOfPages(pageSize);
  }

  public Integer getNumberOfPages(Long supplyDepot, Long program, Long period) {
    return orderRepository.getNumberOfPages(pageSize, supplyDepot, program, period);
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public List<Order> searchByStatusAndRight(Long userId, String rightName, List<OrderStatus> statuses, Long programId, Long facilityId) {
    List<FulfillmentRoleAssignment> fulfilmentRolesWithRight = roleAssignmentService.getFulfilmentRolesWithRight(userId, rightName);

    List<Order> orders = orderRepository.searchByWarehousesAndStatuses((List<Long>) collect(fulfilmentRolesWithRight, new Transformer() {
      @Override
      public Object transform(Object o) {
        return ((FulfillmentRoleAssignment) o).getFacilityId();
      }
    }), statuses, programId, facilityId);

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

  public boolean hasStatus(String orderNumber, OrderStatus... statuses) {
    return contains(statuses, orderRepository.getStatus(orderNumber));
  }

  public Order getByOrderNumber(String orderNumber) {
    return orderRepository.getByOrderNumber(orderNumber);
  }
}
