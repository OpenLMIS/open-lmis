/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.restapi.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Vendor;
import org.openlmis.core.exception.DataException;
import org.openlmis.restapi.domain.Report;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestService;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

import static org.openlmis.restapi.response.RestResponse.error;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
public class RestController {

  public static final String ACCEPT_JSON = "Accept=application/json";
  public static final String UNEXPECTED_EXCEPTION = "unexpected.exception";
  public static final String FORBIDDEN_EXCEPTION = "forbidden.exception";

  @Autowired
  private RestService restService;

  @RequestMapping(value = "/rest-api/requisitions", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity submitRequisition(@RequestBody Report report, Principal principal) {
    report.setVendor(new Vendor(principal.getName()));
    Rnr requisition;
    try {
      requisition = restService.submitReport(report);
    } catch (DataException e) {
      return RestResponse.error(e, HttpStatus.BAD_REQUEST);
    }
    return RestResponse.response("R&R", requisition.getId());
  }


  @ExceptionHandler(Exception.class)
  public ResponseEntity<RestResponse> handleException(Exception ex) {
    if (ex instanceof AccessDeniedException) {
      return error(FORBIDDEN_EXCEPTION, HttpStatus.FORBIDDEN);
    }
    return error(UNEXPECTED_EXCEPTION, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @RequestMapping(value = "/rest-api/requisitions/{id}/approve", method = POST, headers = ACCEPT_JSON)
  public Rnr approve(@PathVariable Integer id,@RequestBody Report report, Principal principal) {
    report.setRnrId(id);
    report.setVendor(new Vendor(principal.getName()));
    return restService.approve(report);
  }
}
