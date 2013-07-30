/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.restapi.controller;

import lombok.NoArgsConstructor;
import org.openlmis.report.service.ReportLookupService;
import org.openlmis.restapi.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

    @RequestMapping(value = "/rest-api/lookup/programs", method = RequestMethod.POST, headers = ACCEPT_JSON)
    public ResponseEntity getPrograms( Principal principal) {
        return RestResponse.response("programs", lookupService.getAllPrograms());
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
