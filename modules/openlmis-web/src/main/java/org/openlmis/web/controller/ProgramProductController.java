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

import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.openlmis.web.response.OpenLmisResponse.response;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller handles endpoint related to listing products for a given program.
 */

@Controller
public class ProgramProductController extends BaseController {

  @Autowired
  private ProgramProductService service;

  private static final String PROGRAM_PRODUCT_LIST = "programProductList";

  @RequestMapping(value = "/programProducts/programId/{programId}", method = GET, headers = BaseController.ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
  public ResponseEntity<OpenLmisResponse> getProgramProductsByProgram(@PathVariable Long programId) {
    List<ProgramProduct> programProductsByProgram = service.getByProgram(new Program(programId));
    return response(PROGRAM_PRODUCT_LIST, programProductsByProgram);
  }
}
