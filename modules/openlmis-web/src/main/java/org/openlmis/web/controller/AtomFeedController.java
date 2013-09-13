/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.ict4h.atomfeed.server.service.EventFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.web.controller.VendorEventFeedServiceHelper.getEventFeed;
import static org.openlmis.web.controller.VendorEventFeedServiceHelper.getRecentFeed;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class AtomFeedController extends BaseController {

  @Autowired
  EventFeedService eventFeedService;

  @Value("${app.url}")
  String baseUrl;


  @RequestMapping(method = GET, value = "feeds/{category}/recent", produces = "application/atom+xml")
  @ResponseBody
  public String getRecentFeeds(@PathVariable(value = "category") String category,
                               @RequestParam(value = "vendor", required = false) String vendor,
                               HttpServletRequest request) {

    return getRecentFeed(eventFeedService, baseUrl + request.getServletPath(), vendor, category);
  }

  @RequestMapping(method = GET, value = "feeds/{category}/{feedNumber}", produces = "application/atom+xml")
  @ResponseBody
  public String getFeed(@PathVariable(value = "category") String category,
                        @PathVariable Integer feedNumber,
                        @RequestParam(value = "vendor", required = false) String vendor,
                        HttpServletRequest request) {

    return getEventFeed(eventFeedService, baseUrl + request.getServletPath(), feedNumber, vendor, category);
  }
}
