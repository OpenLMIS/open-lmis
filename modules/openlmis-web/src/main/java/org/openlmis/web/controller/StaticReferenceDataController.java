/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import lombok.NoArgsConstructor;
import org.openlmis.web.configurationReader.StaticReferenceDataReader;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@NoArgsConstructor
@PropertySource("classpath:${environmentName}/app.properties")
public class StaticReferenceDataController extends BaseController {

  public static final String CURRENCY = "currency";
  private StaticReferenceDataReader staticReferenceDataReader;
  private Environment environment;

  @Autowired
  public StaticReferenceDataController(StaticReferenceDataReader staticReferenceDataReader, Environment environment) {
    this.staticReferenceDataReader = staticReferenceDataReader;
    this.environment = environment;
  }

  @RequestMapping(value = "/reference-data/currency", method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getCurrency() {
    OpenLmisResponse response = new OpenLmisResponse(CURRENCY, staticReferenceDataReader.getCurrency());
    return new ResponseEntity(response, HttpStatus.OK);
  }

  @RequestMapping(value = "/reference-data/lineitem/pagesize", method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getPageSize() {
    OpenLmisResponse response = new OpenLmisResponse("pageSize", environment.getProperty("rnr.lineitem.page.size"));
    return new ResponseEntity(response, HttpStatus.OK);
  }

}
