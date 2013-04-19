/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.service;

import lombok.NoArgsConstructor;
import org.openlmis.order.domain.Order;
import org.openlmis.order.repository.OrderRepository;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RequisitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class OrderService {

  @Autowired
  private OrderRepository orderRepository;
  private RequisitionService requisitionService;

  public void save(Order order) {
    orderRepository.save(order);
  }

  public void convertToOrder(List<Rnr> rnrList, Integer userId) {
    requisitionService.releaseRequisitionsAsOrder(rnrList, userId);
    Order order;
    for(Rnr rnr : rnrList) {
      order = new Order(rnr);
      orderRepository.save(order);
    }
  }
}
