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

import org.ict4h.atomfeed.server.service.EventFeedService;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.service.VendorEventFeedService;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
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
