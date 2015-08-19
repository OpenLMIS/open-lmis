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

import org.openlmis.core.web.controller.BaseController;
import org.openlmis.web.service.VendorEventFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller handles endpoint related to get atom feeds which are recent or by a particular feed number.
 */

@Controller
public class AtomFeedController extends BaseController {

  @Autowired
  private VendorEventFeedService vendorEventFeedService;

  @RequestMapping(method = GET, value = "feeds/{category}/recent", produces = "application/atom+xml")
  @ResponseBody
  public String getRecentFeeds(@PathVariable(value = "category") String category,
                               @RequestParam(value = "vendor", required = false) String vendor,
                               @Value("${app.url}") String baseUrl,
                               HttpServletRequest request) {

    return vendorEventFeedService.getRecentFeed(baseUrl + request.getServletPath(), vendor, category);
  }

  @RequestMapping(method = GET, value = "feeds/{category}/{feedNumber}", produces = "application/atom+xml")
  @ResponseBody
  public String getFeed(@PathVariable(value = "category") String category,
                        @RequestParam(value = "vendor", required = false) String vendor,
                        @PathVariable Integer feedNumber,
                        @Value("${app.url}") String baseUrl,
                        HttpServletRequest request) {

    return vendorEventFeedService.getEventFeed(baseUrl + request.getServletPath(), vendor, category, feedNumber);
  }
}
