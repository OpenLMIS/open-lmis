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

import org.openlmis.core.exception.DataException;
import org.openlmis.equipment.domain.Donor;
import org.openlmis.equipment.service.DonorService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.openlmis.core.web.OpenLmisResponse.*;

@Controller
@RequestMapping(value="/donor/")
public class DonorController extends BaseController {

  public static final String DONOR = "donor";
  public static final String DONORS = "donors";
  
  @Autowired
  private DonorService donorService;

  @RequestMapping(value="list",method= GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_DONOR, MANAGE_EQUIPMENT_INVENTORY')")
  public ResponseEntity<OpenLmisResponse> getAll(){
    return OpenLmisResponse.response(DONORS,donorService.getAllWithDetails());
  }

  @RequestMapping(value="insert.json",method=POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_DONOR')")
  public ResponseEntity<OpenLmisResponse> save(@RequestBody Donor donor, HttpServletRequest request){
    ResponseEntity<OpenLmisResponse> successResponse;
    donor.setModifiedBy(loggedInUserId(request));
    try {
      donorService.save(donor);
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
    successResponse = success(String.format("Donor '%s' has been successfully saved", donor.getShortName()));
    successResponse.getBody().addData(DONOR, donor);
    return successResponse;
  }

  @RequestMapping(value="getDetails/{id}",method = GET,headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_DONOR')")
  public ResponseEntity<OpenLmisResponse> getDetailsForDonor(@PathVariable(value="id") Long id){
    return OpenLmisResponse.response(DONOR,donorService.getById(id));
  }

  @RequestMapping(value="remove/{id}",method = GET,headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_DONOR')")
  public ResponseEntity<OpenLmisResponse> remove(@PathVariable(value="id") Long donorId, HttpServletRequest request){
    ResponseEntity<OpenLmisResponse> successResponse;
    try {
      donorService.removeDonor(donorId);
    } catch (DataException e) {
      return error(e, HttpStatus.BAD_REQUEST);
    }
    successResponse = success(String.format("Donor has been successfully removed"));
    return successResponse;
  }

}
