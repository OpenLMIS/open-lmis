/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityApprovedProductService;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.openlmis.web.response.OpenLmisResponse.success;

@Controller
@NoArgsConstructor
public class FacilityApprovedProductController extends BaseController {

  public static final String NON_FULL_SUPPLY_PRODUCTS = "nonFullSupplyProducts";
  public static final String PRODUCTS = "products";

  private FacilityApprovedProductService facilityApprovedProductService;

  private ProgramProductService programProductService;


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

  @RequestMapping(value = "/facilityApprovedProducts/facility/{facilityId}/program/{programId}/all", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT_ALLOWED_FOR_FACILITY')")
  public ResponseEntity<OpenLmisResponse> getProductsCompleteListByFacilityAndProgram(@PathVariable("facilityId") Long facilityId,
                                                                                          @PathVariable("programId") Long programId) {
      return response(PRODUCTS, facilityApprovedProductService.getProductsCompleteListByFacilityAndProgram(facilityId, programId));
  }

  @RequestMapping(value = "/facilityApprovedProducts/facilityType/{facilityTypeId}/program/{programId}/all", method = RequestMethod.GET, headers = "Accept=application/json")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT_ALLOWED_FOR_FACILITY')")
  public ResponseEntity<OpenLmisResponse> getProductsCompleteListByFacilityTypeAndProgram(@PathVariable("facilityTypeId") Long facilityTypeId,
                                                                                      @PathVariable("programId") Long programId) {
      return response(PRODUCTS, facilityApprovedProductService.getProductsCompleteListByFacilityTypeAndProgram(facilityTypeId, programId));
  }


  @RequestMapping(value = "/facilityApprovedProducts/{facilityTypeId}/program/{programId}/programProductList", method = RequestMethod.GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT_ALLOWED_FOR_FACILITY')")
  public ResponseEntity<OpenLmisResponse> getProductsAlreadyApprovedListByFacilityTypeAndProgram(@PathVariable Long programId, @PathVariable Long facilityTypeId) {
      return response(PRODUCTS, facilityApprovedProductService.getProductsAlreadyApprovedListByFacilityTypeAndProgram(facilityTypeId, programId));
  }

  @RequestMapping(value="/facilityApprovedProducts/facilityType/{facilityTypeId}/program/{programId}/product/{productId}",method = RequestMethod.GET,headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT_ALLOWED_FOR_FACILITY')")
  public ResponseEntity<OpenLmisResponse> getDetailsForFacilityTypeApprovedProduct(@PathVariable("facilityTypeId") Long facilityTypeId,
                                                                              @PathVariable("programId") Long programId,
                                                                              @PathVariable("productId") Long productId){
      return response("facilityTypeApprovedProduct", facilityApprovedProductService.getFacilityApprovedProductByProgramProductAndFacilityTypeId(facilityTypeId,programId,productId));

  }


  @RequestMapping(value="/facilityApprovedProducts/insert.json",method = RequestMethod.POST,headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PRODUCT_ALLOWED_FOR_FACILITY')")
  public ResponseEntity<OpenLmisResponse> saveANewFacilityTypeApprovedProduct(@RequestBody FacilityTypeApprovedProduct facilityTypeApprovedProduct, HttpServletRequest request){
      ResponseEntity<OpenLmisResponse> successResponse;
      try{
          facilityApprovedProductService.save_ext(facilityTypeApprovedProduct);
      }
      catch (DataException e) {
        return error(e, HttpStatus.BAD_REQUEST);
      }

    successResponse = success(String.format("Save successful"));
    return successResponse;
  }

}
