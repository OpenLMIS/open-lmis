/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController extends BaseController {


  @RequestMapping(value = "", method = RequestMethod.GET)
  public String homeDefault() {
    return homePageUrl();
  }

  @RequestMapping(value = "/locales", method = RequestMethod.GET)
  public ResponseEntity<OpenLmisResponse> getLocales(HttpServletRequest request) {
    messageService.setCurrentLocale(RequestContextUtils.getLocale(request));
    return OpenLmisResponse.response("locales", messageService.getLocales());
  }

  @RequestMapping(value = "/changeLocale", method = RequestMethod.PUT, headers = ACCEPT_JSON)
  public void changeLocale(HttpServletRequest request) {
    messageService.setCurrentLocale(RequestContextUtils.getLocale(request));
  }

}
