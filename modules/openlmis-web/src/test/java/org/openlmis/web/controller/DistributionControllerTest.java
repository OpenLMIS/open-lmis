/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.service.DistributionService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.mockito.Mockito.*;
import static org.openlmis.builder.DistributionBuilder.defaultDistribution;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DistributionControllerTest {

  @Mock
  DistributionService distributionService;

  public static final Long userId = 1L;

  @Mock
  MessageService messageService;

  private MockHttpSession session;

  private MockHttpServletRequest httpServletRequest;


  @InjectMocks
  DistributionController controller;

  @Before
  public void setUp() throws Exception {
    httpServletRequest = new MockHttpServletRequest();
    session = new MockHttpSession();
    httpServletRequest.setSession(session);

  }

  @Test
  public void shouldCreateDistribution() throws Exception {
    String username = "User";
    session.setAttribute(UserAuthenticationSuccessHandler.USER, username);
    Distribution distribution = make(a(defaultDistribution));

    doNothing().when(distributionService).create(distribution);
    when(messageService.message("message.distribution.created.success")).thenReturn("Distribution created successfully");

    controller.create(distribution, httpServletRequest);

    verify(distributionService).create(distribution);

  }


}
