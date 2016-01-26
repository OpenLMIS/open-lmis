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