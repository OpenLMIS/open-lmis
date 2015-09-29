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
import org.openlmis.vaccine.domain.inventory.Lot;
import org.openlmis.vaccine.domain.inventory.StockCardEntryType;
import org.openlmis.vaccine.dto.VaccineInventoryTransactionDTO;
import org.openlmis.vaccine.service.Inventory.VaccineInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static org.openlmis.core.web.OpenLmisResponse.response;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
@RequestMapping(value = "/vaccine/inventory/")
public class VaccineInventoryController extends BaseController {

    private static final String PROGRAM_PRODUCT_LIST = "programProductList";
    @Autowired
    VaccineInventoryService service;
    @Autowired
    ProgramService programService;
    @Autowired
    ProgramProductService programProductService;

    @RequestMapping(value = "programProducts/programId/{programId}", method = GET, headers = ACCEPT_JSON)
    //TODO @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_PROGRAM_PRODUCT')")
    public ResponseEntity<OpenLmisResponse> getProgramProductsByProgram(@PathVariable Long programId) {
        List<ProgramProduct> programProductsByProgram = programProductService.getByProgram(new Program(programId));
        return response(PROGRAM_PRODUCT_LIST, programProductsByProgram);
    }

    @RequestMapping(value = "programs")
//TODO:  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_VACCINE_INVENTORY')")
    public ResponseEntity<OpenLmisResponse> getProgramsForConfiguration() {
        return OpenLmisResponse.response("programs", programService.getAllIvdPrograms());
    }

    @RequestMapping(value = "stock/adjustment", method = PUT)
// TODO:   @PreAuthorize("@permissionEvaluator.hasPermission(principal,'ADJUST STOCK')")
    @Transactional
    public ResponseEntity<OpenLmisResponse> adjustment(@RequestBody VaccineInventoryTransactionDTO dto, HttpServletRequest request) {
        Long userId = loggedInUserId(request);
        service.saveTransaction(dto, StockCardEntryType.ADJUSTMENT, userId);
        return OpenLmisResponse.response("success", "Adjustment was successful!");
    }

    @RequestMapping(value = "stock/credit", method = PUT)
//TODO:    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREDIT_STOCK')")
    @Transactional
    public ResponseEntity<OpenLmisResponse> credit(@RequestBody VaccineInventoryTransactionDTO dto, HttpServletRequest request) {
        Long userId = loggedInUserId(request);
        service.saveTransaction(dto, StockCardEntryType.CREDIT, userId);
        return OpenLmisResponse.response("success", "Receiving was successful!");
    }

    @RequestMapping(value = "stock/debit", method = PUT)
//TODO:   @PreAuthorize("@permissionEvaluator.hasPermission(principal,'DEBIT_STOCK')")
    @Transactional
    public ResponseEntity<OpenLmisResponse> debit(@RequestBody VaccineInventoryTransactionDTO dto, HttpServletRequest request) {
        Long userId = loggedInUserId(request);
        service.saveTransaction(dto, StockCardEntryType.DEBIT, userId);
        return OpenLmisResponse.response("success", "Issue was successful!");
    }

    @RequestMapping(value = "lot/create", method = PUT, headers = ACCEPT_JSON)
//TODO:   @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_LOT')")
    public ResponseEntity saveLot(@RequestBody Lot lot) {

        Lot existing = service.getLotByCode(lot.getLotCode());
        if (existing == null) {
            service.insertLot(lot);
            return OpenLmisResponse.response("lotId", lot.getId());
        } else {
            return OpenLmisResponse.response("lotId", existing.getId());
        }

    }

    @RequestMapping(value = "lots/byProduct/{productId}", method = GET, headers = ACCEPT_JSON)
//TODO:   @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_LOT')")
    public ResponseEntity getLotsByProductId(@PathVariable Long productId) {

        return OpenLmisResponse.response("lots", service.getLotsByProductId(productId));
    }

}
