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

import org.openlmis.core.domain.Pagination;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.domain.ProgramProductISA;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.lang.Integer.parseInt;
import static org.openlmis.core.web.OpenLmisResponse.response;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * This controller handles endpoint related to listing products for a given program.
 */
@RequestMapping(value = "/programProducts")
@Controller
public class ProgramProductController extends BaseController {

  @Autowired
  private ProgramProductService service;

  private static final String PROGRAM_PRODUCT_LIST = "programProductList";

  @RequestMapping(value = "/programId/{programId}", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
  public ResponseEntity<OpenLmisResponse> getProgramProductsByProgram(@PathVariable Long programId) {
    List<ProgramProduct> programProductsByProgram = service.getByProgram(new Program(programId));
    return response(PROGRAM_PRODUCT_LIST, programProductsByProgram);
  }

  @RequestMapping(value = "/filter/programId/{programId}/facilityTypeId/{facilityTypeId}", method = GET, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_FACILITY_APPROVED_PRODUCT')")
  public List<ProgramProduct> getUnapprovedProgramProducts(@PathVariable(value = "facilityTypeId") Long facilityTypeId,
                                                           @PathVariable(value = "programId") Long programId) {
    return service.getUnapprovedProgramProducts(facilityTypeId, programId);
  }

  @RequestMapping(value = "/search", method = GET, headers = ACCEPT_JSON)
  public ResponseEntity<OpenLmisResponse> getByProgram(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                                       @RequestParam(value = "searchParam", required = true) String searchParam,
                                                       @RequestParam(value = "column", required = true, defaultValue = "product") String column,
                                                       @Value("${search.page.size}") String limit) {
    Pagination pagination = new Pagination(page, parseInt(limit));
    pagination.setTotalRecords(service.getTotalSearchResultCount(searchParam, column));
    List<ProgramProduct> programProductList = service.search(searchParam, pagination, column);
    ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response(PROGRAM_PRODUCT_LIST, programProductList);
    response.getBody().addData("pagination", pagination);
    return response;
  }


  @RequestMapping(value = "/{programProductId}/isa", method = POST, headers = ACCEPT_JSON)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
  public void insertIsa(@PathVariable Long programProductId,
                        @RequestBody ProgramProductISA programProductISA,
                        HttpServletRequest request) {
    programProductISA.setCreatedBy(loggedInUserId(request));
    programProductISA.setModifiedBy(loggedInUserId(request));
    programProductISA.setProgramProductId(programProductId);
    service.insertISA(programProductISA);
  }

    @RequestMapping(value = "/{programProductId}/isa/{isaId}", method = PUT, headers = ACCEPT_JSON)
    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
    public void updateIsa(@PathVariable Long isaId,
            @PathVariable Long programProductId,
            @RequestBody ProgramProductISA programProductISA,
            HttpServletRequest request) {
      programProductISA.getIsa().setId(isaId);
      programProductISA.setProgramProductId(programProductId);
      programProductISA.setModifiedBy(loggedInUserId(request));
      service.updateISA(programProductISA);
    }
}

