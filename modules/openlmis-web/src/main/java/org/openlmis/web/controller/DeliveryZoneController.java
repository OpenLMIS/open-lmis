/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.web.controller;


import org.openlmis.core.service.AllocationPermissionService;
import org.openlmis.web.response.AllocationResponse;
import org.openlmis.core.service.DeliveryZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.web.response.AllocationResponse.error;
import static org.openlmis.web.response.AllocationResponse.response;
import static org.openlmis.core.domain.Right.MANAGE_DISTRIBUTION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class DeliveryZoneController extends BaseController {

  static final String DELIVERY_ZONES = "deliveryZones";
  public static final String DELIVERY_ZONE_PROGRAMS = "deliveryZonePrograms";

  @Autowired
  DeliveryZoneService service;

  @Autowired
  AllocationPermissionService permissionService;

  @RequestMapping(value = "user/deliveryZones", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<AllocationResponse> getDeliveryZonesForInitiatingAllocation(HttpServletRequest request) {
    return response(DELIVERY_ZONES, service.getByUserForRight(loggedInUserId(request), MANAGE_DISTRIBUTION));
  }

  @RequestMapping(value = "deliveryZones/{zoneId}/programs", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<AllocationResponse> getProgramsForDeliveryZone(HttpServletRequest request, @PathVariable long zoneId) {
    if (permissionService.hasPermissionOnZone(loggedInUserId(request), zoneId)) {
      return response(DELIVERY_ZONE_PROGRAMS, service.getProgramsForDeliveryZone(zoneId));
    } else {
      return error(FORBIDDEN_EXCEPTION, UNAUTHORIZED);
    }
  }

}
