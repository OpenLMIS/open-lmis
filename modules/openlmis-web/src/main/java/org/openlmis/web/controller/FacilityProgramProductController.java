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

import com.wordnik.swagger.annotations.Api;
import org.openlmis.core.domain.FacilityProgramProduct;
import org.openlmis.core.domain.ProgramProductISA;
import org.openlmis.core.service.FacilityProgramProductService;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * This controller allows for the management (creation and retrieval) of FacilityProgramProducts. It also allows
 * for the management of ISA (Ideal Stock Amount) values associated with a given FacilityProgramProduct. Although
 * ISAs are specified at the ProgramProduct level, this controller allows them to be overridden at the more specific
 * FacilityProgramProduct level.
 */

@Controller
@Api(value = "facility-program-products", description = "Provides operations related to ProgramProducts at a specific facility")
public class FacilityProgramProductController extends BaseController {

  @Autowired
  private FacilityProgramProductService service;

  public static final String PROGRAM_PRODUCT_LIST = "programProductList";

  @RequestMapping(value = "/facility/{facilityId}/program/{programId}", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
  public ResponseEntity<OpenLmisResponse> getProgramProductsByProgramAndFacility(@PathVariable Long programId,
                                                                                 @PathVariable Long facilityId) {
    List<FacilityProgramProduct> programProductsByProgram = service.getActiveProductsForProgramAndFacility(programId, facilityId);
    return OpenLmisResponse.response(PROGRAM_PRODUCT_LIST, programProductsByProgram);
  }


  @RequestMapping(value = "/facility/{facilityId}/program/{programId}", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public void saveFacilityProgramProducts(@PathVariable Long facilityId, @RequestBody List<FacilityProgramProduct> products)
  {
    service.save(facilityId, products);
  }


  @RequestMapping(value = "/facility/{facilityId}/programProducts/{programProductId}/isa", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
  public void insertIsa(@PathVariable Long facilityId,
                        @PathVariable Long programProductId,
                        @RequestBody ProgramProductISA programProductISA,
                        HttpServletRequest request) {
    programProductISA.setCreatedBy(loggedInUserId(request));
    programProductISA.setModifiedBy(loggedInUserId(request));
    programProductISA.setProgramProductId(programProductId);
    service.insertISA(facilityId, programProductISA);
  }

  @RequestMapping(value = "/facility/{facilityId}/programProducts/{programProductId}/isa", method = DELETE, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
  public void deleteIsa(@PathVariable Long facilityId, @PathVariable Long programProductId, HttpServletRequest request)
  {
    service.deleteISA(facilityId, programProductId);
  }


}

