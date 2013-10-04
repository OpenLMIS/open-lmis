/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
    Order order = orderService.getOrder(id);
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
