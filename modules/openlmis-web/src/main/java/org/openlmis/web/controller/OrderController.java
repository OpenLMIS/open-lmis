/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.openlmis.core.exception.DataException;
import org.openlmis.order.domain.DateFormat;
import org.openlmis.order.domain.Order;
import org.openlmis.order.dto.OrderDTO;
import org.openlmis.order.dto.OrderFileTemplateDTO;
import org.openlmis.order.service.OrderService;
import org.openlmis.web.form.RequisitionList;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class OrderController extends BaseController {

  public static final String ORDERS = "orders";
  public static final String ORDER = "order";
  public static final String ORDER_FILE_TEMPLATE = "orderFileTemplate";
  public static final String DATE_FORMATS = "dateFormats";

  @Autowired
  private OrderService orderService;

  @RequestMapping(value = "/orders", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> convertToOrder(@RequestBody RequisitionList rnrList,
                                                         HttpServletRequest request) {
    try {
      orderService.convertToOrder(rnrList, loggedInUserId(request));
    } catch (DataException de) {
      return error("msg.rnr.already.converted.to.order", CONFLICT);
    }
    return new ResponseEntity<>(CREATED);
  }

  @RequestMapping(value = "/orders", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'VIEW_ORDER')")
  public ResponseEntity<OpenLmisResponse> getOrders() {
    return response(ORDERS, OrderDTO.getOrdersForView(orderService.getOrders()));
  }

  @RequestMapping(value = "/orders/{id}/download.csv", method = GET, headers = ACCEPT_CSV)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'VIEW_ORDER')")
  public ModelAndView downloadOrderCsv(@PathVariable Long id) {
    ModelAndView modelAndView;
    Order order = orderService.getOrderForDownload(id);
    OrderFileTemplateDTO orderFileTemplate = orderService.getOrderFileTemplateDTO();
    modelAndView = new ModelAndView("orderCsvView");
    modelAndView.addObject(ORDER, order);
    modelAndView.addObject(ORDER_FILE_TEMPLATE, orderFileTemplate);
    return modelAndView;
  }

  @RequestMapping(value = "/order-file-template", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'CONFIGURE_EDI')")
  public ResponseEntity<OpenLmisResponse> getOrderFileTemplateDTO() {
    return response(ORDER_FILE_TEMPLATE, orderService.getOrderFileTemplateDTO());
  }

  @RequestMapping(value = "/order-file-template", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'CONFIGURE_EDI')")
  public ResponseEntity<OpenLmisResponse> saveOrderFileTemplateDTO(@RequestBody OrderFileTemplateDTO orderFileTemplateDTO, HttpServletRequest request) {
    orderService.saveOrderFileTemplate(orderFileTemplateDTO, loggedInUserId(request));
    return OpenLmisResponse.success("order.file.template.saved.success");
  }

  @RequestMapping(value = "/date-formats", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'CONFIGURE_EDI')")
  public ResponseEntity<OpenLmisResponse> getAllDateFormats() {
    Set<DateFormat> dateFormats = orderService.getAllDateFormats();
    return response(DATE_FORMATS, dateFormats);
  }
}
