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
package org.openlmis.restapi.controller;


import com.wordnik.swagger.annotations.*;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.vaccine.domain.inventory.Lot;
import org.openlmis.vaccine.domain.inventory.StockCardEntryType;
import org.openlmis.vaccine.dto.VaccineInventoryTransactionDTO;
import org.openlmis.vaccine.service.inventory.VaccineInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
@Api(value = "Stock Inventory", description = "Make inventory operations")
@RequestMapping(value = "/rest-api/inventory/")
public class RestVaccineInventoryController extends BaseController {

    @Autowired
    VaccineInventoryService service;

    @RequestMapping(value = "lot/create", method = PUT, headers = ACCEPT_JSON)
    @ApiOperation(value = "Create a new Lot Entry.",
            notes = "Create New Lot if does not exist and return the lotId.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful create a Lot", response = Lot.class),
            @ApiResponse(code = 404, message = "Lot doesn't Exist"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
//TODO:   @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREATE_LOT')")
    public ResponseEntity saveLot(@ApiParam(name = "lot", value = "{'productId':x,['product':{'id':x}],'lotCode':'ABC123','manufacturerName':'Name','manufactureDate':'yyyy-MM-dd','expirationDate':'yyyy-MM-dd'}", required = true)
                                  @RequestBody Lot lot) {

        Lot existing = service.getLotByCode(lot.getLotCode());
        if (existing == null) {
            service.insertLot(lot);
            return OpenLmisResponse.response("lotId", lot.getId());
        } else {
            return OpenLmisResponse.response("lotId", existing.getId());
        }

    }

    @RequestMapping(value = "stock/credit", method = PUT)
    @ApiOperation(value = "Create a new CREDIT transaction to a facility Stock Card.",
            notes = "This operation create a new CREDIT transaction (receiving a stock) to a facility stock")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful Credit stock", response = Lot.class),
            @ApiResponse(code = 404, message = "Stock card doest exist"),
            @ApiResponse(code = 500, message = "Internal server error")}
    )
//TODO:    @PreAuthorize("@permissionEvaluator.hasPermission(principal,'CREDIT_STOCK')")
    @Transactional
    public ResponseEntity<OpenLmisResponse> credit(@RequestBody VaccineInventoryTransactionDTO dto, HttpServletRequest request) {
        Long userId = loggedInUserId(request);
        service.saveTransaction(dto, StockCardEntryType.CREDIT, userId);
        return OpenLmisResponse.response("success", "Credit was successful!");
    }

}
