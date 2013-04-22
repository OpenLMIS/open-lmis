/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.openlmis.order.service.OrderService;
import org.openlmis.web.form.RequisitionList;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class OrderController extends BaseController {

  public static final String ORDERS = "orders";

  @Autowired
  private OrderService orderService;

  @RequestMapping(value = "/orders", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CONVERT_TO_ORDER')")
  public void convertToOrder(@RequestBody RequisitionList rnrList, HttpServletRequest request) {
    orderService.convertToOrder(rnrList, loggedInUserId(request));
  }

  @RequestMapping(value = "/orders", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'VIEW_ORDER, CONVERT_TO_ORDER')")
  public ResponseEntity<OpenLmisResponse> getOrders() {
    return OpenLmisResponse.response(ORDERS, orderService.getOrders());
  }

}
