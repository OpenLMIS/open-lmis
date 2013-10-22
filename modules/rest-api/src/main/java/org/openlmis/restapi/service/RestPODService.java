package org.openlmis.restapi.service;

import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.UserService;
import org.openlmis.pod.domain.POD;
import org.openlmis.pod.service.PODService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestPODService {
  @Autowired
  private UserService userService;

  @Autowired
  private PODService podService;


  public void updatePOD(POD pod, String userName) {
    pod.setCreatedBy(userService.getByUserName(userName).getId());
    pod.setModifiedBy(userService.getByUserName(userName).getId());
    pod.validate();
    validateDuplicatePOD(pod);
    podService.updatePOD(pod);
  }

  private void validateDuplicatePOD(POD pod) {
   if(podService.getPODByOrderId(pod.getOrderId()) != null) {
     throw new DataException("error.restapi.delivery.already.confirmed");
   }
  }
}
