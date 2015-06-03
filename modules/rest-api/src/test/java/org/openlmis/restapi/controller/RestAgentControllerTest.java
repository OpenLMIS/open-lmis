/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.restapi.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.Agent;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestAgentService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest(RestResponse.class)
public class RestAgentControllerTest {

  @Mock
  RestAgentService restAgentService;

  @InjectMocks
  RestAgentController restAgentController;

  Principal principal;

  @Before
  public void setUp() throws Exception {
    principal = mock(Principal.class);
    when(principal.getName()).thenReturn("1");
    mockStatic(RestResponse.class);
  }

  @Test
  public void shouldCreateCHW() throws Exception {
    Agent agent = mock(Agent.class);
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(), HttpStatus.OK);
    when(RestResponse.success("message.success.agent.created")).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = restAgentController.createCHW(agent, principal);

    verify(restAgentService).create(agent, 1L);
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

    verify(restAgentService).update(agent, 1L);
    assertThat(response, is(expectResponse));
  }
}
