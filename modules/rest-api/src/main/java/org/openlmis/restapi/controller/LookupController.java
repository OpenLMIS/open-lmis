/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.restapi.controller;

import lombok.NoArgsConstructor;
import org.openlmis.report.service.lookup.ReportLookupService;
import org.openlmis.restapi.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

import static org.openlmis.restapi.response.RestResponse.error;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Controller
@NoArgsConstructor
public class LookupController {

    public static final String ACCEPT_JSON = "Accept=application/json";
    public static final String UNEXPECTED_EXCEPTION = "unexpected.exception";
    public static final String FORBIDDEN_EXCEPTION = "forbidden.exception";

    @Autowired
    private ReportLookupService lookupService;


    @RequestMapping(value = "/rest-api/lookup/product-categories", method = RequestMethod.POST, headers = ACCEPT_JSON)
    public ResponseEntity getProductCategories( Principal principal) {
        return RestResponse.response("product-categories", lookupService.getAllProductCategories());
    }

    @RequestMapping(value = "/rest-api/lookup/products", method = RequestMethod.POST, headers = ACCEPT_JSON)
    public ResponseEntity getProducts( Principal principal) {
    return RestResponse.response("products", lookupService.getFullProductList());
    }

    @RequestMapping(value = "/rest-api/lookup/product-by-code", method = RequestMethod.POST, headers = ACCEPT_JSON)
    public ResponseEntity getProductByCode( Principal principal ,@RequestBody String code) {
        return RestResponse.response("product", lookupService.getProductByCode(code));
    }


    @RequestMapping(value = "/rest-api/lookup/dosage-units", method = RequestMethod.POST, headers = ACCEPT_JSON)
     public ResponseEntity getDosageUnits( Principal principal) {
        return RestResponse.response("dosage-units", lookupService.getDosageUnits());
    }

    @RequestMapping(value = "/rest-api/lookup/facility-types", method = RequestMethod.POST, headers = ACCEPT_JSON)
    public ResponseEntity getFacilityTypes( Principal principal) {
        return RestResponse.response("facility-types", lookupService.getFacilityTypes());
    }

    @RequestMapping(value = "/rest-api/lookup/facilities", method = RequestMethod.POST, headers = ACCEPT_JSON)
    public ResponseEntity getFacilities( Principal principal) {
        return RestResponse.response("facilities", lookupService.getAllFacilities());
    }

    @RequestMapping(value = "/rest-api/lookup/facility-by-code", method = RequestMethod.POST, headers = ACCEPT_JSON)
    public ResponseEntity getFacilityByCode( Principal principal,@RequestBody String code) {
        return RestResponse.response("facility", lookupService.getFacilityByCode(code));
    }

    @RequestMapping(value = "/rest-api/lookup/programs", method = RequestMethod.POST, headers = ACCEPT_JSON)
    public ResponseEntity getPrograms( Principal principal) {
        return RestResponse.response("programs", lookupService.getAllPrograms());
    }

    @RequestMapping(value = "/rest-api/lookup/program-products", method = RequestMethod.POST, headers = ACCEPT_JSON)
    public ResponseEntity getProgramProducts( Principal principal) {
        return RestResponse.response("program-products", lookupService.getAllProgramProducts());
    }


    @RequestMapping(value = "/rest-api/lookup/facility-approved-products", method = RequestMethod.POST, headers = ACCEPT_JSON)
    public ResponseEntity getFacilityApprovedProducts( Principal principal) {
        return RestResponse.response("facility-approved-products", lookupService.getAllFacilityTypeApprovedProducts());
    }

    @RequestMapping(value = "/rest-api/lookup/program-by-code", method = RequestMethod.POST, headers = ACCEPT_JSON)
    public ResponseEntity getProgramByCode( Principal principal ,@RequestBody String code) {
        return RestResponse.response("program", lookupService.getProgramByCode(code));
    }

    @RequestMapping(value = "/rest-api/lookup/losses-adjustments-types", method = RequestMethod.POST, headers = ACCEPT_JSON)
    public ResponseEntity getLossesAdjustmentsTypes( Principal principal) {
        return RestResponse.response("losses-adjustments-types", lookupService.getAllAdjustmentTypes());
    }

    @RequestMapping(value = "/rest-api/lookup/processing-periods", method = RequestMethod.POST, headers = ACCEPT_JSON)
    public ResponseEntity getProcessingPeriods( Principal principal) {
        return RestResponse.response("processing-periods", lookupService.getAllProcessingPeriods());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse> handleException(Exception ex) {
        if (ex instanceof AccessDeniedException) {
            return error(FORBIDDEN_EXCEPTION, FORBIDDEN);
        }
        return error(UNEXPECTED_EXCEPTION, INTERNAL_SERVER_ERROR);
    }

}
