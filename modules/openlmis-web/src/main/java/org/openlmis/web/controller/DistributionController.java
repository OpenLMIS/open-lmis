/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.domain.Distribution;
import org.openlmis.service.DistributionService;
import org.openlmis.web.controller.BaseController;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.web.response.OpenLmisResponse.error;
import static org.openlmis.web.response.OpenLmisResponse.success;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
@NoArgsConstructor
public class DistributionController extends BaseController {

  @Autowired
  DistributionService distributionService;

  @RequestMapping(value = "/distributions", method = POST, headers = "Accept=application/json")
  public ResponseEntity<OpenLmisResponse> create(@RequestBody Distribution distribution, HttpServletRequest request) {
    distribution.setCreatedBy(loggedInUserId(request));
    distribution.setModifiedBy(loggedInUserId(request));

    distributionService.create(distribution);

    ResponseEntity<OpenLmisResponse> successResponse = success(messageService.message("message.distribution.created.success"));
    successResponse.getBody().addData("distribution", distribution);
    return successResponse;
  }


}
