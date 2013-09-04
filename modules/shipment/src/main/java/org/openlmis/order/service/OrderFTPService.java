/*
 *
 *  * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *  *
 *  * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package org.openlmis.order.service;

import org.openlmis.core.domain.FacilityFtpDetails;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.service.FacilityFtpDetailsService;
import org.openlmis.core.service.SupplyLineService;
import org.openlmis.order.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderFTPService {

  @Autowired
  private SupplyLineService supplyLineService;

  @Autowired
  private FacilityFtpDetailsService facilityFtpDetailsService;

  @ServiceActivator(inputChannel = "orderInputChannel")
  public void processOrder(List<Order> orders) {
    for (Order order : orders) {
      SupplyLine supplyLine = supplyLineService.getById(order.getSupplyLine().getId());
      FacilityFtpDetails supplyingFacility = facilityFtpDetailsService.getByFacilityId(supplyLine.getSupplyingFacility());
    }
  }
}
