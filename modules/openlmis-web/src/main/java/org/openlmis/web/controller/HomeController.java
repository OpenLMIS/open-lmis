/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.openlmis.core.service.LocaleService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController extends BaseController {

  @Autowired
  LocaleService localeService;

  @RequestMapping(value = "", method = RequestMethod.GET)
  public String homeDefault() {
    return homePageUrl();
  }

  @RequestMapping(value = "/locales", method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getLocales() {
    return OpenLmisResponse.response("locales", localeService.getLocales());
  }

  @RequestMapping(value = "/changeLocale", method = RequestMethod.PUT, headers = ACCEPT_JSON)
  public void changeLocale(@RequestParam("locale") String locale, HttpServletRequest request) {
    localeService.changeLocale(request);
  }

}
