package org.openlmis.restapi.service;

import org.openlmis.core.exception.DataException;
import org.openlmis.order.service.OrderService;
import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.pod.service.PODService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestPODService {

  @Autowired
  private PODService podService;

  @Autowired
  private OrderService orderService;

  public void updatePOD(OrderPOD orderPod, Long userId) {
    orderPod.setCreatedBy(userId);
    orderPod.setModifiedBy(userId);
    orderPod.validate();
    validateOrderForPOD(orderPod);
    podService.updatePOD(orderPod);
  }

  private void validateOrderForPOD(OrderPOD orderPod) {
    if (orderService.getOrder(orderPod.getOrderId()) == null) {
      throw new DataException("error.restapi.invalid.order");
    }
    if (podService.getPODByOrderId(orderPod.getOrderId()) != null) {
      throw new DataException("error.restapi.delivery.already.confirmed");
    }
  }
}
