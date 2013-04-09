/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.restapi.controller;

import lombok.NoArgsConstructor;
import org.openlmis.restapi.domain.Report;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestService;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
public class RestController {

  public static final String ACCEPT_JSON = "Accept=application/json";

  @Autowired
  private RestService restService;

  @RequestMapping(value = "/commtrack/requisitions", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity submitRequisition(@RequestBody Report report) {
    Rnr requisition;
    try {
      requisition = restService.submitReport(report);
    } catch (DataException e) {
      return RestResponse.error(e, HttpStatus.BAD_REQUEST);
    }
    return RestResponse.response("R&R", requisition.getId());
  }
}
