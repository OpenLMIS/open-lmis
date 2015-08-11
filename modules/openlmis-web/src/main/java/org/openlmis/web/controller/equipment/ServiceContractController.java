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

import org.openlmis.equipment.domain.ServiceContract;
import org.openlmis.equipment.domain.ServiceType;
import org.openlmis.equipment.service.ServiceContractService;
import org.openlmis.equipment.service.ServiceTypeService;
import org.openlmis.web.controller.BaseController;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value="/equipment/service-contracts/")
public class ServiceContractController extends BaseController {

  @Autowired
  private ServiceContractService service;

  @RequestMapping(method = GET, value = "list")
  public ResponseEntity<OpenLmisResponse> getAll(){
    return  OpenLmisResponse.response("contracts", service.getAll());
  }

  @RequestMapping(method = GET, value = "id")
  public ResponseEntity<OpenLmisResponse> getById( @RequestParam("id") Long id){
    ServiceContract contract = service.getById(id);
    contract.setId(id);
    return  OpenLmisResponse.response("contract", contract);
  }

  @RequestMapping(method = GET, value = "for-facility")
  public ResponseEntity<OpenLmisResponse> getByFacilityId( @RequestParam("id") Long id){
    return  OpenLmisResponse.response("contracts", service.getAllForFacility(id));
  }

  @RequestMapping(method = GET, value = "for-vendor")
  public ResponseEntity<OpenLmisResponse> getByVendorId( @RequestParam("id") Long id){
    return  OpenLmisResponse.response("contracts", service.getAllForVendor(id));
  }

  @RequestMapping(value = "save", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> save(@RequestBody ServiceContract contract){
    service.save(contract);
    ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.success("message.equipment.service.contract.saved");
    response.getBody().addData("contract", contract);
    return response;
  }
}
