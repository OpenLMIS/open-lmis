/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
import org.openlmis.core.domain.User;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.UserService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.service.DistributionService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.Date;
import java.util.Map;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.builder.DistributionBuilder.*;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DistributionControllerTest {

  @Mock
  DistributionService service;

  @Mock
  UserService userService;

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

    Distribution expectedDistribution = new Distribution();
    when(service.create(distribution)).thenReturn(expectedDistribution);
    when(service.get(distribution)).thenReturn(null);
    when(messageService.message("message.distribution.created.success", null, null, null)
    ).thenReturn("Distribution created successfully");

    ResponseEntity<OpenLmisResponse> response = controller.create(distribution, httpServletRequest);

    assertThat((Distribution) response.getBody().getData().get("distribution"), is(expectedDistribution));
    assertThat((String) response.getBody().getData().get("success"), is("Distribution created successfully"));
    assertThat(response.getStatusCode(), is(CREATED));
    verify(service).get(distribution);
    verify(service).create(distribution);

  }

  @Test
  public void itShouldReturnExistingDistributionWithWarningIfAlreadyExist() throws Exception {
    Long createdById = 10L;
    Date creationTimeStamp = new Date();
    Distribution distribution = make(a(defaultDistribution));

    Distribution existingDistribution = make(a(defaultDistribution,
      with(createdBy, createdById),
      with(createdDate, creationTimeStamp)));

    when(service.get(distribution)).thenReturn(existingDistribution);

    User user = make(a(defaultUser));
    when(userService.getById(createdById)).thenReturn(user);

    when(messageService.message("message.distribution.already.exists", user.getUserName(), creationTimeStamp)).
      thenReturn("Distribution already initiated by XYZ at 2013-05-03 12:10");

    ResponseEntity<OpenLmisResponse> response = controller.create(distribution, httpServletRequest);
    assertThat(response.getStatusCode(), is(OK));
    Map<String, Object> responseData = response.getBody().getData();

    assertThat((String) responseData.get("success"),
      is("Distribution already initiated by XYZ at 2013-05-03 12:10"));
    assertThat((Distribution) responseData.get("distribution"), is(existingDistribution));


  }
}
