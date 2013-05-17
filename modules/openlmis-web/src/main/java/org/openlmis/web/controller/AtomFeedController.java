/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.apache.log4j.Logger;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.ict4h.atomfeed.server.service.EventFeedService;
import org.ict4h.atomfeed.server.service.helper.EventFeedServiceHelper;
import org.openlmis.core.atomfeed.OpenLmisEventRecordJdbcImpl;
import org.openlmis.core.exception.DataException;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
public class AtomFeedController extends BaseController {

  private Logger logger = Logger.getLogger(AtomFeedController.class);

  @Autowired
  EventFeedService eventFeedService;

  @Resource(name="allEventRecords")
  AllEventRecords allEventRecords;

  @RequestMapping(method = RequestMethod.GET, value = "/feeds/{category}/recent", produces = "application/atom+xml")
  @ResponseBody
  public String getRecentFeeds(@PathVariable(value = "category") String category, HttpServletRequest request, @RequestParam(value = "vendor", required = false) String vendor) {
    setCategoryInEventRecord(category);
    return VendorEventFeedServiceHelper.getRecentFeed(eventFeedService, request.getRequestURL().toString(), logger,vendor, category);
  }

  private void setCategoryInEventRecord(String category) {
    OpenLmisEventRecordJdbcImpl eventRecord = null;
    try {
      eventRecord = (OpenLmisEventRecordJdbcImpl) ((Advised)allEventRecords).getTargetSource().getTarget();
    } catch (Exception e) {
      throw new DataException(e.getMessage());
    }
    eventRecord.setCategory(category);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/feeds/{category}/{id}", produces = "application/atom+xml")
  @ResponseBody
  public String getFeed(@PathVariable(value = "category") String category, HttpServletRequest request, @PathVariable Integer id) {
    setCategoryInEventRecord(category);
    return EventFeedServiceHelper.getEventFeed(eventFeedService, request.getRequestURL().toString(), id, logger);
  }
}
