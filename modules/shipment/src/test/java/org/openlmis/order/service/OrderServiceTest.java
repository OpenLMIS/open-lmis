/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.order.domain.Order;
import org.openlmis.order.repository.OrderRepository;
import org.openlmis.order.service.OrderService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.service.RequisitionService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

  @Mock
  private OrderRepository orderRepository;
  @Mock
  private RequisitionService requisitionService;

  @InjectMocks
  private OrderService orderService;

  @Test
  public void shouldSaveOrder() throws Exception {
    Order order = new Order();
    orderService.save(order);
    verify(orderRepository).save(order);
  }

  @Test
  public void shouldConvertRequisitionsToOrder() throws Exception {
    List<Rnr> rnrList = new ArrayList<>();
    Rnr requisition = new Rnr();
    rnrList.add(requisition);
    Integer userId = 1;
    orderService.convertToOrder(rnrList, userId);
    Order order = new Order(requisition);
    whenNew(Order.class).withArguments(requisition).thenReturn(order);
    verify(requisitionService).releaseRequisitionsAsOrder(rnrList, userId);
    verify(orderRepository).save(order);
  }
}
