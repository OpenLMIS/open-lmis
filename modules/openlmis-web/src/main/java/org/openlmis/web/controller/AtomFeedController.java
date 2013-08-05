/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.apache.log4j.Logger;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.service.EventFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static org.openlmis.web.controller.VendorEventFeedServiceHelper.getEventFeed;
import static org.openlmis.web.controller.VendorEventFeedServiceHelper.getRecentFeed;

@Controller
public class AtomFeedController extends BaseController {

    private Logger logger = Logger.getLogger(AtomFeedController.class);

    @Autowired
    EventFeedService eventFeedService;

    @Autowired
    AllEventRecords allEventRecords;

    @RequestMapping(method = RequestMethod.GET, value = "/feeds/{category}/recent", produces = "application/atom+xml")
    @ResponseBody
    public String getRecentFeeds(@PathVariable(value = "category") String category,
                                 HttpServletRequest request,
                                 @RequestParam(value = "vendor", required = false) String vendor) {
        return getRecentFeed(eventFeedService, request.getRequestURL().toString(), logger, vendor, category);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/feeds/{category}/{id}", produces = "application/atom+xml")
    @ResponseBody
    public String getFeed(@PathVariable(value = "category") String category, HttpServletRequest request,
                          @PathVariable Integer id, @RequestParam(value = "vendor", required = false) String vendor) {
        return getEventFeed(eventFeedService, request.getRequestURL().toString(), id, logger, vendor, category);
    }
}
