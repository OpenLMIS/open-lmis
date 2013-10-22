package org.openlmis.pod.repository;

import org.openlmis.pod.domain.POD;
import org.openlmis.pod.domain.PODLineItem;
import org.openlmis.pod.repository.mapper.PODMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PODRepository {

  @Autowired
  PODMapper podMapper;

  public void insertPODLineItem(PODLineItem podLineItem) {
    podMapper.insertPODLineItem(podLineItem);
  }

  public void insertPOD(POD pod) {
    podMapper.insertPOD(pod);
  }

  public POD getPODByOrderId(Long orderId) {
    return podMapper.getPODByOrderId(orderId);
  }
}
