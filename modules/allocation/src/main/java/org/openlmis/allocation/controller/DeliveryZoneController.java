/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.allocation.controller;


import org.apache.commons.collections.Predicate;
import org.openlmis.allocation.domain.DeliveryZone;
import org.openlmis.allocation.response.AllocationResponse;
import org.openlmis.allocation.service.DeliveryZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.exists;
import static org.openlmis.allocation.response.AllocationResponse.error;
import static org.openlmis.allocation.response.AllocationResponse.response;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.openlmis.core.domain.Right.PLAN_DISTRIBUTION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class DeliveryZoneController {

  static final String DELIVERY_ZONES = "deliveryZones";
  public static final String DELIVERY_ZONE_PROGRAMS = "deliveryZonePrograms";

  @Autowired
  DeliveryZoneService service;

  @RequestMapping(value = "user/delivery-zones", method = GET)
  public ResponseEntity<AllocationResponse> getDeliveryZonesForInitiatingAllocation(HttpServletRequest request) {
    return response(DELIVERY_ZONES, service.getByUserForRight(loggedInUserId(request), PLAN_DISTRIBUTION));
  }

  private Long loggedInUserId(HttpServletRequest request) {
    return (Long) request.getSession().getAttribute(USER_ID);
  }


  @RequestMapping(value = "delivery-zones/{zoneId}/programs", method = GET)
  public ResponseEntity<AllocationResponse> getProgramsForDeliveryZone(HttpServletRequest request, @PathVariable long zoneId) {
    if (hasPermissionOnZone(request, zoneId)) {
      return response(DELIVERY_ZONE_PROGRAMS, service.getProgramsForDeliveryZone(1l));
    } else {
      return error("unauthorized", UNAUTHORIZED);
    }
  }

  private boolean hasPermissionOnZone(HttpServletRequest request, final long zoneId) {
    List<DeliveryZone> zones = service.getByUserForRight(loggedInUserId(request), PLAN_DISTRIBUTION);

    return exists(zones, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        DeliveryZone zone = (DeliveryZone) o;
        return zone.getId() == zoneId;
      }
    });
  }
}
