/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.openlmis.order.service.OrderService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.dto.RnrDTO;
import org.openlmis.web.form.RnrList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class OrderController extends BaseController {

  @Autowired
  private OrderService orderService;


  @RequestMapping(value = "/requisitionOrder", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CONVERT_TO_ORDER')")
  public void convertToOrder(@RequestBody RnrList rnrList, HttpServletRequest request) {
    orderService.convertToOrder(rnrList.getRnrList(), loggedInUserId(request));
  }
}
