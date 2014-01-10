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

import org.openlmis.pod.service.PODService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class PODController extends BaseController {

  public static final String ORDER_POD = "orderPOD";
  @Autowired
  private PODService service;

  @RequestMapping(value = "/update-pod-orders/{orderId}", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'MANAGE_POD')")
  public ResponseEntity<OpenLmisResponse> updatePOD(HttpServletRequest request, @PathVariable("orderId") Long orderId) {
    ResponseEntity<OpenLmisResponse> response = response(ORDER_POD, service.getPOD(orderId, loggedInUserId(request)));
    return response;
  }
}
