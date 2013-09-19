/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.service.StaticReferenceDataService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@NoArgsConstructor
public class StaticReferenceDataController extends BaseController {

  public static final String PAGE_SIZE = "pageSize";
  public static final String RNR_LINEITEM_PAGE_SIZE = "rnr.lineitem.page.size";

  @Autowired
  StaticReferenceDataService service;

  @RequestMapping(value = "/reference-data/lineitem/pagesize", method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getPageSize() {
    OpenLmisResponse response = new OpenLmisResponse(PAGE_SIZE, service.getPropertyValue(RNR_LINEITEM_PAGE_SIZE));
    return new ResponseEntity(response, HttpStatus.OK);
  }

}
