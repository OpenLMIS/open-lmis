/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.order.controller;

import org.openlmis.order.service.OrderService;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class OrderController {

  @Autowired
  private OrderService orderService;

  public static final String ACCEPT_JSON = "Accept=application/json";
  public static final String USER_ID = "USER_ID";

  @RequestMapping(value = "/requisitionOrder111", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CONVERT_TO_ORDER')")
  public void convertToOrder(@RequestBody List<Rnr> rnrList, HttpServletRequest request) {
    orderService.convertToOrder(rnrList, loggedInUserId(request));
  }

  private Integer loggedInUserId(HttpServletRequest request) {
    return (Integer) request.getSession().getAttribute(USER_ID);
  }
}
