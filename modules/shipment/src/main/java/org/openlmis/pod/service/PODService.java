package org.openlmis.pod.service;

import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProductService;
import org.openlmis.pod.domain.POD;
import org.openlmis.pod.domain.PODLineItem;
import org.openlmis.pod.repository.PODRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PODService {

  @Autowired
  private PODRepository podRepository;

  @Autowired
  private ProductService productService;

  public void updatePOD(POD pod) {
    podRepository.insertPOD(pod);
    if (pod.getPodLineItems() == null) return;
    for (PODLineItem podLineItem : pod.getPodLineItems()) {
      if (productService.getIdForCode(podLineItem.getProductCode()) == null)
        throw new DataException("");
      podRepository.insertPODLineItem(podLineItem);

    }
  }

  public POD getPODByOrderId(Long orderId) {
    return podRepository.getPODByOrderId(orderId);
  }
}
