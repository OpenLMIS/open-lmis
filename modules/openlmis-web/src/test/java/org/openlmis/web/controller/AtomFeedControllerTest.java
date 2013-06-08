/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.apache.log4j.Logger;
import org.ict4h.atomfeed.server.service.EventFeedService;
import org.ict4h.atomfeed.server.service.helper.EventFeedServiceHelper;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(EventFeedServiceHelper.class)
public class AtomFeedControllerTest {

  @Mock
  EventFeedService eventFeedService;

  @InjectMocks
  AtomFeedController controller;

  MockHttpServletRequest request;

  @Test
  public void shouldGetRecentFeeds() throws Exception {
    mockStatic(EventFeedServiceHelper.class);
    request = new MockHttpServletRequest();
    when(EventFeedServiceHelper.getRecentFeed(eq(eventFeedService), anyString(), anyString(), any(Logger.class))).thenReturn("feed");

    //String recentFeeds = controller.getRecentFeeds(request);

    //assertThat(recentFeeds, is("feed"));
  }

  @Test
  public void shouldFeedById() throws Exception {
    mockStatic(EventFeedServiceHelper.class);
    request = new MockHttpServletRequest();
    when(EventFeedServiceHelper.getEventFeed(eq(eventFeedService), anyString(), anyString(), eq(1), any(Logger.class))).thenReturn("feed");

    //String feed = controller.getFeed(request, 1);

    //assertThat(feed, is("feed"));
  }
}
