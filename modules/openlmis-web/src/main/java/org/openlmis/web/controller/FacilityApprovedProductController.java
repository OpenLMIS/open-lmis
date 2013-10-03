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

import lombok.NoArgsConstructor;
import org.openlmis.core.service.FacilityApprovedProductService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.openlmis.web.response.OpenLmisResponse.response;

@Controller
@NoArgsConstructor
public class FacilityApprovedProductController extends BaseController {

  public static final String NON_FULL_SUPPLY_PRODUCTS = "nonFullSupplyProducts";

  private FacilityApprovedProductService facilityApprovedProductService;

  @Autowired
  public FacilityApprovedProductController(FacilityApprovedProductService facilityApprovedProductService) {
    this.facilityApprovedProductService = facilityApprovedProductService;
  }

  @RequestMapping(value = "/facilityApprovedProducts/facility/{facilityId}/program/{programId}/nonFullSupply", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> getAllNonFullSupplyProductsByFacilityAndProgram(@PathVariable("facilityId") Long facilityId,
                                                                                          @PathVariable("programId") Long programId) {
    return response(NON_FULL_SUPPLY_PRODUCTS, facilityApprovedProductService.getNonFullSupplyFacilityApprovedProductByFacilityAndProgram(facilityId, programId));
  }
}
