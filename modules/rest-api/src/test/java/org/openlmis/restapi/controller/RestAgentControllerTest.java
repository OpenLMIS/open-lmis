/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.restapi.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.Agent;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestAgentService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RestResponse.class)
@Category(UnitTests.class)
public class RestAgentControllerTest {

  @Mock
  RestAgentService restAgentService;

  @InjectMocks
  RestAgentController restAgentController;

  Principal principal;

  @Before
  public void setUp() throws Exception {
    principal = mock(Principal.class);
    when(principal.getName()).thenReturn("vendor name");
    mockStatic(RestResponse.class);
  }

  @Test
  public void shouldCreateCHW() throws Exception {
    Agent agent = mock(Agent.class);
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.success("message.success.agent.created")).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = restAgentController.createCHW(agent, principal);

    verify(restAgentService).create(agent, principal.getName());
    assertThat(response, is(expectResponse));
  }

  @Test
  public void shouldUpdateCHW() throws Exception {
    Agent agent = mock(Agent.class);
    String agentCode = "A1";
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.success("message.success.agent.updated")).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = restAgentController.updateCHW(agent, agentCode, principal);
    verify(agent).setAgentCode(agentCode);

    verify(restAgentService).update(agent, principal.getName());
    assertThat(response, is(expectResponse));
  }

}
