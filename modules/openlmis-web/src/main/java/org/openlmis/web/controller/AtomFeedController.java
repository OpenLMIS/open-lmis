/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import com.sun.syndication.feed.atom.Feed;
import org.apache.log4j.Logger;
import org.ict4h.atomfeed.server.service.EventFeedService;
import org.ict4h.atomfeed.server.service.helper.EventFeedServiceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class AtomFeedController extends BaseController {

  private Logger logger = Logger.getLogger(AtomFeedController.class);

  @Autowired
  EventFeedService eventFeedService;

  @RequestMapping(method = RequestMethod.GET, value = "/feeds/recent", produces = "application/atom+xml")
  @ResponseBody
  public String getRecentFeeds(HttpServletRequest request) {
    return EventFeedServiceHelper.getRecentFeed(eventFeedService, request.getRequestURL().toString(), logger);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/feeds/{id}", produces = "application/atom+xml")
  @ResponseBody
  public String getFeed(HttpServletRequest request, @PathVariable Integer id) {
    return EventFeedServiceHelper.getEventFeed(eventFeedService, request.getRequestURL().toString(), id, logger);
  }
}
