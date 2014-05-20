/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.web.controller.equipment;

import org.openlmis.equipment.domain.MaintenanceRequest;
import org.openlmis.equipment.service.MaintenanceRequestService;
import org.openlmis.web.controller.BaseController;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
@RequestMapping(value="/equipment/maintenance-request/")
public class MaintenanceRequestController extends BaseController {
  @Autowired
  private MaintenanceRequestService service;

  @RequestMapping(method = RequestMethod.GET, value = "list")
  public ResponseEntity<OpenLmisResponse> getAll(){
    return  OpenLmisResponse.response("logs", service.getAll());
  }

  @RequestMapping(method = RequestMethod.GET, value = "id")
  public ResponseEntity<OpenLmisResponse> getById( @RequestParam("id") Long id){
    return  OpenLmisResponse.response("log", service.getById(id));
  }

  @RequestMapping(method = RequestMethod.GET, value = "for-facility")
  public ResponseEntity<OpenLmisResponse> getByFacilityId( @RequestParam("id") Long id){
    return  OpenLmisResponse.response("logs", service.getAllForFacility(id));
  }

  @RequestMapping(method = RequestMethod.GET, value = "for-vendor")
  public ResponseEntity<OpenLmisResponse> getByVendorId( @RequestParam("id") Long id){
    return  OpenLmisResponse.response("logs", service.getAllForVendor(id));
  }

  @RequestMapping(method = RequestMethod.GET, value = "outstanding-for-vendor")
  public ResponseEntity<OpenLmisResponse> getOutstandingByVendorId( @RequestParam("id") Long id){
    return  OpenLmisResponse.response("logs", service.getOutstandingForVendor(id));
  }

  @RequestMapping(value = "save", method = RequestMethod.POST, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> save(@RequestBody MaintenanceRequest maintenanceRequest, HttpServletRequest request){
    maintenanceRequest.setCreatedBy(loggedInUserId(request));
    maintenanceRequest.setUserId(loggedInUserId(request));
    maintenanceRequest.setResolved(false);
    maintenanceRequest.setRequestDate(new Date());
    service.save(maintenanceRequest);
    return OpenLmisResponse.response("status","success");
  }
}
