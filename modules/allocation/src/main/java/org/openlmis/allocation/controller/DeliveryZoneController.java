package org.openlmis.allocation.controller;


import org.openlmis.allocation.response.AllocationResponse;
import org.openlmis.allocation.service.DeliveryZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.allocation.response.AllocationResponse.response;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.openlmis.core.domain.Right.PLAN_DISTRIBUTION;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class DeliveryZoneController {

  static final String DELIVERY_ZONES = "deliveryZones";

  @Autowired
  DeliveryZoneService service;

  @RequestMapping(value = "/user/delivery-zones", method = GET)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal, 'PLAN_DISTRIBUTION')")
  public ResponseEntity<AllocationResponse> getDeliveryZonesForInitiatingAllocation(HttpServletRequest request) {
    return response(DELIVERY_ZONES, service.getByUserForRight((Long) request.getSession().getAttribute(USER_ID), PLAN_DISTRIBUTION));
  }
}
