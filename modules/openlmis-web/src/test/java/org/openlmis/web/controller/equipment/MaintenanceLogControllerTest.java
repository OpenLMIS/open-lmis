/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.web.controller.equipment;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.equipment.domain.MaintenanceLog;
import org.openlmis.equipment.service.MaintenanceLogService;
import org.openlmis.core.web.OpenLmisResponse;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;


@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class MaintenanceLogControllerTest {
  @Mock
  MaintenanceLogService service;

  @InjectMocks
  MaintenanceLogController controller;

  private MockHttpServletRequest request;

  @Before
  public void setUp() {
    initMocks(this);
    request = new MockHttpServletRequest(USER, USER);
    request.getSession().setAttribute(USER_ID, 1L);
  }

  @Test
  public void shouldGetAll() throws Exception {
    List<MaintenanceLog> logs = new ArrayList<>();
    when(service.getAll()).thenReturn(logs);

    ResponseEntity<OpenLmisResponse> response = controller.getAll();

    assertThat(logs, is(response.getBody().getData().get("logs")));
  }

  @Test
  public void shouldGetById() throws Exception {
    MaintenanceLog log = new MaintenanceLog();
    when(service.getById(1L)).thenReturn(log);

    ResponseEntity<OpenLmisResponse> response = controller.getById(1L);
    assertThat(log, is(response.getBody().getData().get("log")));
  }

  @Test
  public void shouldGetByFacilityId() throws Exception {
    List<MaintenanceLog> logs = new ArrayList<>();
    when(service.getAllForFacility(1L)).thenReturn(logs);

    ResponseEntity<OpenLmisResponse> response = controller.getByFacilityId(1L);
    assertThat(logs, is(response.getBody().getData().get("logs")));
  }

  @Test
  public void shouldGetByVendorId() throws Exception {
    List<MaintenanceLog> logs = new ArrayList<>();
    when(service.getAllForVendor(1L)).thenReturn(logs);

    ResponseEntity<OpenLmisResponse> response = controller.getByVendorId(1L);
    assertThat(logs, is(response.getBody().getData().get("logs")));
  }

}