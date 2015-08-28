/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.web.controller.equipment;

import org.openlmis.equipment.domain.MaintenanceRequest;
import org.openlmis.equipment.service.MaintenanceRequestService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.core.web.OpenLmisResponse;
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

  @RequestMapping(method = RequestMethod.GET, value = "outstanding-for-user")
  public ResponseEntity<OpenLmisResponse> getOutstandingByUserId( HttpServletRequest request){
    return  OpenLmisResponse.response("logs", service.getOutstandingForUser(loggedInUserId(request)));
  }

  @RequestMapping(method = RequestMethod.GET, value = "full-history")
  public ResponseEntity<OpenLmisResponse> getFullHistoryId( @RequestParam("id") Long inventoryId){
    return  OpenLmisResponse.response("logs", service.getFullHistory(inventoryId));
  }

  @RequestMapping(value = "save", method = RequestMethod.POST, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> save(@RequestBody MaintenanceRequest maintenanceRequest, HttpServletRequest request){
    if(maintenanceRequest.getId() == null) {
      maintenanceRequest.setCreatedBy(loggedInUserId(request));
      maintenanceRequest.setUserId(loggedInUserId(request));
      maintenanceRequest.setResolved(false);
      maintenanceRequest.setRequestDate(new Date());
    }

    maintenanceRequest.setModifiedBy(loggedInUserId(request));
    maintenanceRequest.setModifiedDate(new Date());
    service.save(maintenanceRequest);
    ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success(messageService.message("message.maintenance.request.saved"));
    response.getBody().addData("log", maintenanceRequest);
    return response;
  }

}
