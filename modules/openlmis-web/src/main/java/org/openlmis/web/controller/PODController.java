/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.web.controller;

import org.openlmis.core.exception.DataException;
import org.openlmis.order.dto.OrderPODDTO;
import org.openlmis.order.service.OrderService;
import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.pod.service.PODService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

import static org.openlmis.web.response.OpenLmisResponse.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
public class PODController extends BaseController {

  public static final String ORDER_POD = "orderPOD";
  public static final String ORDER = "order";

  @Autowired
  private PODService service;

  @Autowired
  private OrderService orderService;

  @RequestMapping(value = "/pods", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'MANAGE_POD')")
  public ResponseEntity<OpenLmisResponse> createPOD(@RequestParam Long orderId,
                                                    HttpServletRequest request) throws ParseException {
    OrderPODDTO orderPODDTO = OrderPODDTO.getOrderDetailsForPOD(orderService.getOrder(orderId));
    OrderPOD existingPOD = service.getPODByOrderId(orderId);
    ResponseEntity<OpenLmisResponse> response;
    if (existingPOD != null) {
      response = response(ORDER_POD, existingPOD);
    } else {
      OrderPOD orderPOD = new OrderPOD(orderId, loggedInUserId(request));
      OrderPOD createdPOD = service.createPOD(orderPOD);
      response = response(ORDER_POD, createdPOD, HttpStatus.CREATED);
    }
    response.getBody().addData(ORDER, orderPODDTO);
    return response;
  }

  @RequestMapping(value = "/pods/{id}", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'MANAGE_POD')")
  public ResponseEntity<OpenLmisResponse> getPOD(@PathVariable("id") Long podId) throws ParseException {
    OrderPOD orderPOD = service.getPodById(podId);
    OrderPODDTO orderPODDTO = OrderPODDTO.getOrderDetailsForPOD(orderService.getOrder(orderPOD.getOrderId()));
    ResponseEntity<OpenLmisResponse> response = response(ORDER_POD, orderPOD);
    response.getBody().addData(ORDER, orderPODDTO);
    return response;
  }

  @RequestMapping(value = "/pods/{id}", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'MANAGE_POD')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody OrderPOD orderPOD, @PathVariable Long id, HttpServletRequest request) {
    try {
      orderPOD.setModifiedBy(loggedInUserId(request));
      orderPOD.setId(id);
      service.save(orderPOD);
      return success("msg.pod.save.success");
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
  }
}
