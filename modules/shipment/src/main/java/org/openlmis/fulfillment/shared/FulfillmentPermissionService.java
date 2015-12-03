/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.fulfillment.shared;

import org.openlmis.core.domain.Right;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.core.utils.RightUtil;
import org.openlmis.order.domain.Order;
import org.openlmis.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.Iterables.any;

/**
 * This class is responsible for checking if the user has the given right on a warehouse.
 */

@Service
public class FulfillmentPermissionService {


  @Autowired
  private RoleRightsService roleRightsService;

  @Autowired
  private OrderService orderService;


  public Boolean hasPermission(Long userId, Long orderId, String rightName) {
    Order order = orderService.getOrder(orderId);
    List<Right> userRights = roleRightsService.getRightsForUserAndWarehouse(userId, order.getSupplyingFacility().getId());
    return any(userRights, RightUtil.with(rightName));
  }

  public Boolean hasPermissionOnWarehouse(Long userId, Long warehouseId, String rightName) {
    List<Right> userRights = roleRightsService.getRightsForUserAndWarehouse(userId, warehouseId);
    return any(userRights, RightUtil.with(rightName));
  }
}
