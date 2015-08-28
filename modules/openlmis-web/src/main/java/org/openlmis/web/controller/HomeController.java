/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.core.web.OpenLmisResponse.response;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * This controller handles endpoint related list locales, also to change the current locale.
 */

@Controller
public class HomeController extends BaseController {

  @RequestMapping(value = "", method = GET)
  public String homeDefault() {
    return homePageUrl();
  }

  @RequestMapping(value = "/locales", method = GET)
  public ResponseEntity<OpenLmisResponse> getLocales(HttpServletRequest request) {
    messageService.setCurrentLocale(RequestContextUtils.getLocale(request));
    return response("locales", messageService.getLocales());
  }

  @RequestMapping(value = "/changeLocale", method = PUT, headers = ACCEPT_JSON)
  public void changeLocale(HttpServletRequest request) {
    messageService.setCurrentLocale(RequestContextUtils.getLocale(request));
  }
}
