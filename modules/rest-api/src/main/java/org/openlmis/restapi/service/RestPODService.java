package org.openlmis.restapi.service;

import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProductService;
import org.openlmis.order.domain.Order;
import org.openlmis.order.service.OrderService;
import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.pod.domain.OrderPODLineItem;
import org.openlmis.pod.service.PODService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * This service exposes methods for to update proof of delivery details.
 */

@Service
public class RestPODService {

  @Autowired
  private PODService podService;

  @Autowired
  private OrderService orderService;

  @Autowired
  private ProductService productService;

  @Autowired
  private RequisitionService requisitionService;

  @Transactional
  public void updatePOD(OrderPOD orderPod, Long userId) {
    orderPod.setCreatedBy(userId);
    orderPod.setModifiedBy(userId);
    Order order = orderService.getByOrderNumber(orderPod.getOrderNumber());
    if(order == null )
      throw new DataException("error.restapi.invalid.order");
    orderPod.setOrderId(order.getId());
    orderPod.validate();
    validateOrderForPOD(orderPod);
    podService.checkPermissions(orderPod);
    insertPOD(orderPod);
  }

  private void insertPOD(OrderPOD orderPod) {
    insertOrderPOD(orderPod);

    List<OrderPODLineItem> orderPodLineItems = orderPod.getPodLineItems();
    validatePODLineItems(orderPodLineItems);

    insertLineItems(orderPod);
    podService.updateOrderStatus(orderPod);
  }

  private void insertLineItems(OrderPOD orderPod) {
    for (OrderPODLineItem orderPodLineItem : orderPod.getPodLineItems()) {
      orderPodLineItem.setPodId(orderPod.getId());
      orderPodLineItem.setCreatedBy(orderPod.getCreatedBy());
      orderPodLineItem.setModifiedBy(orderPod.getModifiedBy());
      podService.insertPODLineItem(orderPodLineItem);
    }
  }

  private void validatePODLineItems(List<OrderPODLineItem> orderPodLineItems) {
    List<String> invalidProductCodes = getInvalidProductCodes(orderPodLineItems);
    if (invalidProductCodes.size() > 0) {
      throw new DataException("error.invalid.product.code", invalidProductCodes.toString());
    }
  }

  private void validateOrderForPOD(OrderPOD orderPod) {
    if (podService.getPODByOrderId(orderPod.getOrderId()) != null) {
      throw new DataException("error.restapi.delivery.already.confirmed");
    }
  }

  private List<String> getInvalidProductCodes(List<OrderPODLineItem> orderPodLineItems) {
    List<String> invalidProductCodes = new ArrayList<>();
    for (OrderPODLineItem orderPodLineItem : orderPodLineItems) {
      if (productService.getByCode(orderPodLineItem.getProductCode()) == null) {
        invalidProductCodes.add(orderPodLineItem.getProductCode());
      }
    }
    return invalidProductCodes;
  }

  private void insertOrderPOD(OrderPOD orderPod) {
    Rnr requisition = requisitionService.getLWById(orderPod.getOrderId());
    orderPod.fillPOD(requisition);
    podService.insertPOD(orderPod);
  }

}
