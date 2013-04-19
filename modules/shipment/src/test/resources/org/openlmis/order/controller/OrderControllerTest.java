/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.order.service.OrderService;
import org.openlmis.rnr.domain.Rnr;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {

  @Mock
  private OrderService orderService;
  @InjectMocks
  private OrderController orderController;

  @Test
  public void shouldConvertRequisitionsToOrder() throws Exception {
    List<Rnr> rnrList = new ArrayList<>();
    Integer userId = 1;
    orderController.convertToOrder(rnrList, userId);
    verify(orderService).convertToOrder(rnrList, userId);
  }
}
