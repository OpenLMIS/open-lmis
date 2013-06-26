/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.openlmis.core.service.AllocationPermissionService;
import org.openlmis.web.response.AllocationResponse;
import org.openlmis.core.service.DeliveryZoneProgramScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class DeliveryZoneProgramScheduleController extends BaseController {

  public static final String PERIODS = "periods";

  @Autowired
  DeliveryZoneProgramScheduleService scheduleService;

  @Autowired
  AllocationPermissionService permissionService;

  @RequestMapping(value = "deliveryZones/{zoneId}/programs/{programId}/periods", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<AllocationResponse> getPeriodsForProgramInDeliveryZone(HttpServletRequest request, @PathVariable long zoneId,
                                                                               @PathVariable long programId) {
    if (permissionService.hasPermissionOnZone(loggedInUserId(request), zoneId)) {
      return AllocationResponse.response(PERIODS, scheduleService.getPeriodsForDeliveryZoneAndProgram(zoneId, programId));
    } else {
      return AllocationResponse.error(FORBIDDEN_EXCEPTION, HttpStatus.UNAUTHORIZED);
    }
  }
}
