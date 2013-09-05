/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package org.openlmis.order.service;

import org.openlmis.order.domain.Order;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderFTPService {

  @ServiceActivator(inputChannel = "orderInputChannel")
  public void processOrder(List<Order> orders) {

  }
}
