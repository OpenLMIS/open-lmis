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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.service.AllocationPermissionService;
import org.openlmis.core.service.DeliveryZoneProgramScheduleService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.distribution.service.DistributionService;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller handles endpoint to get periods for a delivery zone, program combination.
 */

@Controller
public class DeliveryZoneProgramScheduleController extends BaseController {

  public static final String PERIODS = "periods";

  @Autowired
  private DeliveryZoneProgramScheduleService scheduleService;

  @Autowired
  private AllocationPermissionService permissionService;

  @Autowired
  private DistributionService distributionService;

  @RequestMapping(value = "deliveryZones/{zoneId}/programs/{programId}/periods", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getPeriodsForProgramInDeliveryZone(HttpServletRequest request, @PathVariable long zoneId,
                                                                             @PathVariable final long programId) {
    if (permissionService.hasPermissionOnZone(loggedInUserId(request), zoneId)) {
      List<ProcessingPeriod> periodsForDeliveryZoneAndProgram = scheduleService.getPeriodsForDeliveryZoneAndProgram(zoneId, programId);
      final List<Long> syncedPeriods = distributionService.getSyncedPeriodsForDeliveryZoneAndProgram(zoneId, programId);
      Collection unsyncedPeriodsForZoneAndProgram = CollectionUtils.select(periodsForDeliveryZoneAndProgram, new Predicate() {
        @Override
        public boolean evaluate(Object o) {
          ProcessingPeriod period = (ProcessingPeriod) o;
          return !syncedPeriods.contains(period.getId());
        }
      });
      return OpenLmisResponse.response(PERIODS, unsyncedPeriodsForZoneAndProgram);
    } else {
      return OpenLmisResponse.error(FORBIDDEN_EXCEPTION, HttpStatus.UNAUTHORIZED);
    }
  }
}
