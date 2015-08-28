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

import org.openlmis.core.service.DeliveryZoneService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.core.domain.RightName.MANAGE_DISTRIBUTION;
import static org.openlmis.core.web.OpenLmisResponse.response;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller handles endpoint to get programs, active programs for a deliveryZones and delivery zone information.
 */

@Controller
public class DeliveryZoneController extends BaseController {

  static final String DELIVERY_ZONES = "deliveryZones";
  public static final String DELIVERY_ZONE_PROGRAMS = "deliveryZonePrograms";

  @Autowired
  private DeliveryZoneService service;

  @RequestMapping(value = "user/deliveryZones", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_DISTRIBUTION')")
  public ResponseEntity<OpenLmisResponse> getDeliveryZonesForInitiatingAllocation(HttpServletRequest request) {
    return response(DELIVERY_ZONES, service.getByUserForRight(loggedInUserId(request), MANAGE_DISTRIBUTION));
  }

  @RequestMapping(value = "deliveryZones/{id}/activePrograms", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_DISTRIBUTION')")
  public ResponseEntity<OpenLmisResponse> getActiveProgramsForDeliveryZone(@PathVariable Long id) {
    return response(DELIVERY_ZONE_PROGRAMS, service.getActiveProgramsForDeliveryZone(id));
  }

  @RequestMapping(value = "deliveryZones/{id}/programs", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_USER')")
  public ResponseEntity<OpenLmisResponse> getAllProgramsForDeliveryZone(@PathVariable Long id) {
    return response(DELIVERY_ZONE_PROGRAMS, service.getAllProgramsForDeliveryZone(id));
  }

  @RequestMapping(value = "deliveryZones/{id}", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_DISTRIBUTION')")
  public ResponseEntity<OpenLmisResponse> get(@PathVariable Long id) {
    return response("zone", service.getById(id));
  }

  @RequestMapping(value = "deliveryZones", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_USER')")
  public ResponseEntity<OpenLmisResponse> getAll() {
    return response(DELIVERY_ZONES, service.getAll());
  }

}
