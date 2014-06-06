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
import org.openlmis.core.domain.FacilityTypeApprovedProduct;
import org.openlmis.core.domain.Pagination;
import org.openlmis.core.service.FacilityApprovedProductService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static java.lang.Integer.parseInt;
import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller handles endpoint related to get non full supply products for a given facility, program combination.
 */

@Controller
@NoArgsConstructor
public class FacilityApprovedProductController extends BaseController {

  public static final String NON_FULL_SUPPLY_PRODUCTS = "nonFullSupplyProducts";
  public static final String FACILITY_APPROVED_PRODUCTS = "facilityApprovedProducts";
  public static final String PAGINATION = "pagination";

  @Autowired
  private FacilityApprovedProductService service;

  @RequestMapping(value = "/facilityApprovedProducts/facility/{facilityId}/program/{programId}/nonFullSupply", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_REQUISITION, AUTHORIZE_REQUISITION')")
  public ResponseEntity<OpenLmisResponse> getAllNonFullSupplyProductsByFacilityAndProgram(@PathVariable("facilityId") Long facilityId,
                                                                                          @PathVariable("programId") Long programId) {
    return response(NON_FULL_SUPPLY_PRODUCTS, service.getNonFullSupplyFacilityApprovedProductByFacilityAndProgram(facilityId, programId));
  }

  @RequestMapping(value = "/facilityApprovedProducts", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getAllBy(@RequestParam("facilityTypeId") Long facilityTypeId,
                                                   @RequestParam("programId") Long programId,
                                                   @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                   @Value("${search.page.size}") String limit) {
    Pagination pagination = new Pagination(page, parseInt(limit));
    pagination.setTotalRecords(service.getTotalSearchResultCount(facilityTypeId, programId));
    List<FacilityTypeApprovedProduct> facilityTypeApprovedProducts = service.getAllBy(facilityTypeId, programId, pagination);
    ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response(FACILITY_APPROVED_PRODUCTS, facilityTypeApprovedProducts);
    response.getBody().addData(PAGINATION, pagination);
    return response;
  }
}
