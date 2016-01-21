/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015 Clinton Health Access Initiative (CHAI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.web.controller.vaccine.inventory;


import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramProduct;
import org.openlmis.core.service.ProgramProductService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;

import org.openlmis.stockmanagement.domain.Lot;
import org.openlmis.vaccine.service.inventory.VaccineInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static org.openlmis.core.web.OpenLmisResponse.response;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
@RequestMapping(value = "/vaccine/inventory/")
public class VaccineInventoryController extends BaseController {

    private static final String PROGRAM_PRODUCT_LIST = "programProductList";

    @Autowired
    ProgramService programService;
    @Autowired
    ProgramProductService programProductService;

    @Autowired
    VaccineInventoryService service;

    @RequestMapping(value = "programProducts/programId/{programId}", method = GET, headers = ACCEPT_JSON)
    //@PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_STOCK','VIEW_STOCK_ON_HAND','')")
    public ResponseEntity<OpenLmisResponse> getProgramProductsByProgram(@PathVariable Long programId) {
        List<ProgramProduct> programProductsByProgram = programProductService.getByProgram(new Program(programId));
        return response(PROGRAM_PRODUCT_LIST, programProductsByProgram);
    }

    @RequestMapping(value = "programs")
    //  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_STOCK','VIEW_STOCK_ON_HAND')")
    public ResponseEntity<OpenLmisResponse> getProgramsForConfiguration() {
        return OpenLmisResponse.response("programs", programService.getAllIvdPrograms());
    }

    @RequestMapping(value = "lots/byProduct/{productId}", method = GET, headers = ACCEPT_JSON)
    // @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_STOCK')")
    public ResponseEntity getLotsByProductId(@PathVariable Long productId) {

        return OpenLmisResponse.response("lots", service.getLotsByProductId(productId));
    }

    @RequestMapping(value = "lot/create", method = PUT, headers = ACCEPT_JSON)
//TODO:   @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_STOCK')")
    public ResponseEntity saveLot(@RequestBody Lot lot) {
        return OpenLmisResponse.response("lot", service.insertLot(lot));
    }
}
