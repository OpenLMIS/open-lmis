/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.openlmis.core.domain.FacilityProgramProduct;
import org.openlmis.core.domain.ProgramProductISA;
import org.openlmis.core.service.FacilityProgramProductService;
import org.openlmis.web.form.FacilityProgramProductList;
import org.openlmis.web.response.OpenLmisResponse;
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

@Controller
public class FacilityProgramProductController extends BaseController {

  @Autowired
  private FacilityProgramProductService service;

  public static final String PROGRAM_PRODUCT_LIST = "programProductList";

  @RequestMapping(value = "/facility/{facilityId}/program/{programId}/isa", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
  public ResponseEntity<OpenLmisResponse> getProgramProductsByProgramAndFacility(@PathVariable Long programId, @PathVariable Long facilityId) {
    List<FacilityProgramProduct> programProductsByProgram = service.getForProgramAndFacility(programId, facilityId);
    return OpenLmisResponse.response(PROGRAM_PRODUCT_LIST, programProductsByProgram);
  }

  @RequestMapping(value = "/programProducts/{programProductId}/isa", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
  public void insertIsa(@PathVariable Long programProductId, @RequestBody ProgramProductISA programProductISA,HttpServletRequest request) {
    programProductISA.setCreatedBy(loggedInUserId(request));
    programProductISA.setModifiedBy(loggedInUserId(request));
    programProductISA.setProgramProductId(programProductId);
    service.insertISA(programProductISA);
  }


  @RequestMapping(value = "/programProducts/{programProductId}/isa/{isaId}", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
  public void updateIsa(@PathVariable Long isaId, @PathVariable Long programProductId, @RequestBody ProgramProductISA programProductISA,HttpServletRequest request) {
    programProductISA.setId(isaId);
    programProductISA.setProgramProductId(programProductId);
    programProductISA.setModifiedBy(loggedInUserId(request));
    service.updateISA(programProductISA);
  }

  @RequestMapping(value = "/facility/{facilityId}/program/{programId}/isa", method = PUT, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY')")
  public void overrideIsa(@PathVariable Long facilityId, @RequestBody FacilityProgramProductList products) {
    service.saveOverriddenIsa(facilityId, products);
  }

  @RequestMapping(value = "/facility/{facilityId}/program/{programId}/programProductList", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
  public ResponseEntity<OpenLmisResponse> getByFacilityAndProgram(@PathVariable Long programId, @PathVariable Long facilityId) {
      List<FacilityProgramProduct> programProductsByProgram = service.getByFacilityAndProgram(facilityId,programId);
      return OpenLmisResponse.response(PROGRAM_PRODUCT_LIST, programProductsByProgram);
  }

}

