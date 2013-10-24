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
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.ProductService;
import org.openlmis.fulfillment.shared.FulfillmentPermissionService;
import org.openlmis.order.domain.Order;
import org.openlmis.order.domain.OrderStatus;
import org.openlmis.order.service.OrderService;
import org.openlmis.pod.domain.POD;
import org.openlmis.pod.domain.PODLineItem;
import org.openlmis.pod.repository.PODRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.openlmis.core.domain.Right.MANAGE_POD;

@Service
public class PODService {

  @Autowired
  private PODRepository podRepository;

  @Autowired
  private ProductService productService;

  @Autowired
  private OrderService orderService;

  @Autowired
  private MessageService messageService;

  @Autowired
  private FulfillmentPermissionService fulfillmentPermissionService;

  @Transactional
  public void updatePOD(POD pod) {

    if (!fulfillmentPermissionService.hasPermission(pod.getCreatedBy(), getWarehouseForOrder(pod.getOrderId()), MANAGE_POD)) {
      throw new DataException("error.pod.permission.denied");
    }
    podRepository.insertPOD(pod);
    if (pod.getPodLineItems() == null) return;
    List<String> invalidProductCodes = getInvalidProductCodes(pod.getPodLineItems());
    if (invalidProductCodes.size() > 0) {
      throw new DataException(messageService.message("error.invalid.product.code", invalidProductCodes.toString()));
    }
    for (PODLineItem podLineItem : pod.getPodLineItems()) {
      podLineItem.setPodId(pod.getId());
      podRepository.insertPODLineItem(podLineItem);
    }
    Order order = new Order(pod.getOrderId());
    order.setStatus(OrderStatus.RECEIVED);
    orderService.updateOrderStatus(order);
  }

  public POD getPODByOrderId(Long orderId) {
    return podRepository.getPODByOrderId(orderId);
  }

  private Long getWarehouseForOrder(Long orderId) {
    Order order = orderService.getOrder(orderId);
    return order.getSupplyLine().getSupplyingFacility().getId();
  }

  private List<String> getInvalidProductCodes(List<PODLineItem> podLineItems) {
    List<String> invalidProductCodes = new ArrayList<>();
    for (PODLineItem podLineItem : podLineItems) {
      if (productService.getByCode(podLineItem.getProductCode()) == null) {
        invalidProductCodes.add(podLineItem.getProductCode());
      }
    }
    return invalidProductCodes;
  }
}
