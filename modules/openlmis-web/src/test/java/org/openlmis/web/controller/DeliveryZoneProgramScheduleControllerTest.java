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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.web.response.OpenLmisResponse;
import org.openlmis.core.service.AllocationPermissionService;
import org.openlmis.core.service.DeliveryZoneProgramScheduleService;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.db.categories.UnitTests;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.web.controller.BaseController.FORBIDDEN_EXCEPTION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DeliveryZoneProgramScheduleControllerTest {

  public static final long ZONE_ID = 1l;
  @InjectMocks
  DeliveryZoneProgramScheduleController controller;

  @Mock
  DeliveryZoneProgramScheduleService scheduleService;

  @Mock
  AllocationPermissionService permissionService;

  MockHttpServletRequest request;

  private static final String USER = "user";
  private static final Long USER_ID = 1l;

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);
    when(permissionService.hasPermissionOnZone(USER_ID, ZONE_ID)).thenReturn(true);

    request.setSession(session);
  }

  @Test
  public void shouldFetchPeriodForAProgramInADeliveryZone() throws Exception {
    List<ProcessingPeriod> expectedPeriods = new ArrayList<>();
    when(scheduleService.getPeriodsForDeliveryZoneAndProgram(1l, 3l)).thenReturn(expectedPeriods);

    ResponseEntity<OpenLmisResponse> response = controller.getPeriodsForProgramInDeliveryZone(request, ZONE_ID, 3l);

    assertThat(expectedPeriods, is(response.getBody().getData().get("periods")));
    verify(scheduleService).getPeriodsForDeliveryZoneAndProgram(1l, 3l);
  }

  @Test
  public void shouldReturnErrorResponse() throws Exception {
    List<ProcessingPeriod> expectedPeriods = new ArrayList<>();
    when(scheduleService.getPeriodsForDeliveryZoneAndProgram(1l, 3l)).thenReturn(expectedPeriods);
    Long invalidZoneId = 67l;
    when(permissionService.hasPermissionOnZone(USER_ID, invalidZoneId)).thenReturn(false);

    ResponseEntity<OpenLmisResponse> response = controller.getPeriodsForProgramInDeliveryZone(request, invalidZoneId, 3l);

    assertThat(response.getStatusCode(), is(UNAUTHORIZED));
    assertThat(response.getBody().getErrorMsg(), is(FORBIDDEN_EXCEPTION));
  }
}
