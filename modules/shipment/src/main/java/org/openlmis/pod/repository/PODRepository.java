/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.pod.repository;

import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.pod.domain.OrderPODLineItem;
import org.openlmis.pod.repository.mapper.PODMapper;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Repository class for OrderPOD related database operations.
 */

@Repository
public class PODRepository {

  @Autowired
  PODMapper mapper;

  public void insertPODLineItem(OrderPODLineItem orderPodLineItem) {
    mapper.insertPODLineItem(orderPodLineItem);
  }

  public void insertPOD(OrderPOD orderPod) {
    mapper.insertPOD(orderPod);
  }

  public OrderPOD getPODByOrderId(Long orderId) {
    return mapper.getPODByOrderId(orderId);
  }

  public List<OrderPODLineItem> getNPodLineItems(String productCode, Rnr requisition, Integer n, Date startDate) {
    return mapper.getNPodLineItems(productCode, requisition, n, startDate);
  }

  public OrderPOD getPOD(Long podId) {
    return mapper.getPODById(podId);
  }

  public OrderPOD insert(OrderPOD orderPOD) {
    mapper.insertPOD(orderPOD);
    for (OrderPODLineItem orderPodLineItem : orderPOD.getPodLineItems()) {
      orderPodLineItem.setPodId(orderPOD.getId());
      mapper.insertPODLineItem(orderPodLineItem);
    }

    return orderPOD;
  }

  public OrderPOD update(OrderPOD orderPOD) {
    mapper.update(orderPOD);
    for (OrderPODLineItem lineItem : orderPOD.getPodLineItems()) {
      mapper.updateLineItem(lineItem);
    }
    return orderPOD;
  }
}
