/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.web.controller.equipment;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.MaintenanceRequest;
import org.openlmis.equipment.dto.Log;
import org.openlmis.equipment.service.MaintenanceRequestService;
import org.openlmis.core.web.OpenLmisResponse;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;


@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class MaintenanceRequestControllerTest {

  @Mock
  MaintenanceRequestService service;

  @Mock
  MessageService messageService;

  @InjectMocks
  MaintenanceRequestController controller;

  private MockHttpServletRequest request;

  @Before
  public void setUp() {
    initMocks(this);
    request = new MockHttpServletRequest(USER, USER);
    request.getSession().setAttribute(USER_ID, 1L);
    when(messageService.message(anyString())).thenReturn("the message");
  }


  @Test
  public void shouldGetAll() throws Exception {
    List<MaintenanceRequest> requests = new ArrayList<>();
    when(service.getAll()).thenReturn(requests);
    ResponseEntity<OpenLmisResponse> response = controller.getAll();
    assertThat(requests, is(response.getBody().getData().get("logs")));
  }

  @Test
  public void shouldGetById() throws Exception {
    MaintenanceRequest request = new MaintenanceRequest();
    when(service.getById(2L)).thenReturn(request);
    ResponseEntity<OpenLmisResponse> response = controller.getById(2L);
    assertThat(request, is(response.getBody().getData().get("log")));
  }

  @Test
  public void shouldGetByFacilityId() throws Exception {
    List<MaintenanceRequest> requests = new ArrayList<>();
    when(service.getAllForFacility(2L)).thenReturn(requests);

    ResponseEntity<OpenLmisResponse> response = controller.getByFacilityId(2L);
    assertThat(requests, is(response.getBody().getData().get("logs")));
  }

  @Test
  public void shouldGetByVendorId() throws Exception {
    List<MaintenanceRequest> requests = new ArrayList<>();
    when(service.getAllForVendor(2L)).thenReturn(requests);

    ResponseEntity<OpenLmisResponse> response = controller.getByVendorId(2L);
    assertThat(requests, is(response.getBody().getData().get("logs")));
  }

  @Test
  public void shouldGetOutstandingByVendorId() throws Exception {
    List<MaintenanceRequest> requests = new ArrayList<>();
    when(service.getOutstandingForVendor(2L)).thenReturn(requests);

    ResponseEntity<OpenLmisResponse> response = controller.getOutstandingByVendorId(2L);
    assertThat(requests, is(response.getBody().getData().get("logs")));
  }

  @Test
  public void shouldGetOutstandingByUser() throws Exception {
    List<MaintenanceRequest> requests = new ArrayList<>();
    when(service.getOutstandingForUser(1L)).thenReturn(requests);

    ResponseEntity<OpenLmisResponse> response = controller.getOutstandingByUserId(request);
    assertThat(requests, is(response.getBody().getData().get("logs")));
  }

  @Test
  public void shouldGetFullHistoryId() throws Exception {
    List<Log> requests = new ArrayList<>();
    when(service.getFullHistory(1L)).thenReturn(requests);

    ResponseEntity<OpenLmisResponse> response = controller.getFullHistoryId(1L);
    assertThat(requests, is(response.getBody().getData().get("logs")));
  }

  @Test
  public void shouldSave() throws Exception {
    MaintenanceRequest maintenanceRequest = new MaintenanceRequest();
    ResponseEntity<OpenLmisResponse> response = controller.save(maintenanceRequest, request);
    assertThat(maintenanceRequest, is(response.getBody().getData().get("log")));
  }
}