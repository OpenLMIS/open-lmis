package org.openlmis.restapi.service;

import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.UserService;
import org.openlmis.order.service.OrderService;
import org.openlmis.pod.domain.POD;
import org.openlmis.pod.service.PODService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestPODService {
  @Autowired
  private PODService podService;

  @Autowired
  private OrderService orderService;

  public void updatePOD(POD pod, Long userId) {
    pod.setCreatedBy(userId);
    pod.setModifiedBy(userId);
    pod.validate();
    validateOrderForPOD(pod);
    podService.updatePOD(pod);
  }

  private void validateOrderForPOD(POD pod) {
    if(orderService.getOrder(pod.getOrderId()) == null) {
      throw new DataException("error.restapi.invalid.order");
    }
    if (podService.getPODByOrderId(pod.getOrderId()) != null) {
      throw new DataException("error.restapi.delivery.already.confirmed");
    }
  }
}
