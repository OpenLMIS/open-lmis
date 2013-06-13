/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.allocation.controller;

import org.openlmis.allocation.domain.AllocationProgramProduct;
import org.openlmis.allocation.response.AllocationResponse;
import org.openlmis.allocation.service.AllocationProgramProductService;
import org.openlmis.allocation.domain.ProgramProductISA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class ProgramProductController extends BaseController {

  @Autowired
  private AllocationProgramProductService programProductISAService;

  public static final String PROGRAM_PRODUCT_LIST = "PROGRAM_PRODUCT_LIST";

  @RequestMapping(value = "/programProducts/programId/{programId}", method = GET, headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
  public ResponseEntity<AllocationResponse> getProgramProductsByProgram(@PathVariable Long programId) {
    List<AllocationProgramProduct> programProductsByProgram = programProductISAService.getProgramProductsWithISAByProgram(programId);
    return AllocationResponse.response(PROGRAM_PRODUCT_LIST, programProductsByProgram);
  }

  @RequestMapping(value = "/programProducts/programProductISA/{programProductId}", method = POST , headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
  public void saveProgramProductISA(@PathVariable Long programProductId, @RequestBody ProgramProductISA programProductISA) {
    programProductISAService.saveProgramProductISA(programProductId, programProductISA);
  }
}

