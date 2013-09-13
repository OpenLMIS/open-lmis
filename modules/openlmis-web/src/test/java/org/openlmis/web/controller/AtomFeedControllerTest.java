/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.ict4h.atomfeed.server.service.EventFeedService;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
public class AtomFeedControllerTest {

  @Mock
  EventFeedService eventFeedService;

  @Mock
  VendorEventFeedService vendorEventFeedService;

  @InjectMocks
  AtomFeedController controller;

  @Mock
  HttpServletRequest request;

  @Before
  public void setUp() throws Exception {
    when(request.getServletPath()).thenReturn("/path");
  }

  @Test
  public void shouldGetRecentFeeds() throws Exception {
    String category = "category1";
    String vendor = "vendor1";
    String baseUrl = "baseUrl";

    when(vendorEventFeedService.getRecentFeed("baseUrl/path", vendor, category)).thenReturn("xml atom feed content");
    String recentFeeds = controller.getRecentFeeds(category, vendor, baseUrl, request);

    assertThat(recentFeeds, is("xml atom feed content"));
  }

  @Test
  public void shouldFeedById() throws Exception {


    String category = "category1";
    String vendor = "vendor1";
    String baseUrl = "baseUrl";
    int feedNumber = 1;

    when(vendorEventFeedService.getEventFeed("baseUrl/path", vendor, category, feedNumber)).thenReturn("xml atom feed content");

    String feed = controller.getFeed(category, vendor, feedNumber, baseUrl, request);

    assertThat(feed, is("xml atom feed content"));
  }
}
