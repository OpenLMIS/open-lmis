/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.distribution.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.distribution.response.AllocationResponse;
import org.openlmis.distribution.service.AllocationPermissionService;
import org.openlmis.distribution.service.DeliveryZoneProgramScheduleService;
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
import static org.openlmis.distribution.controller.BaseController.FORBIDDEN_EXCEPTION;
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

    ResponseEntity<AllocationResponse> response = controller.getPeriodsForProgramInDeliveryZone(request, ZONE_ID, 3l);

    assertThat(expectedPeriods, is(response.getBody().getData().get("periods")));
    verify(scheduleService).getPeriodsForDeliveryZoneAndProgram(1l, 3l);
  }

  @Test
  public void shouldReturnErrorResponse() throws Exception {
    List<ProcessingPeriod> expectedPeriods = new ArrayList<>();
    when(scheduleService.getPeriodsForDeliveryZoneAndProgram(1l, 3l)).thenReturn(expectedPeriods);
    Long invalidZoneId = 67l;
    when(permissionService.hasPermissionOnZone(USER_ID, invalidZoneId)).thenReturn(false);

    ResponseEntity<AllocationResponse> response = controller.getPeriodsForProgramInDeliveryZone(request, invalidZoneId, 3l);

    assertThat(response.getStatusCode(), is(UNAUTHORIZED));
    assertThat(response.getBody().getErrorMsg(), is(FORBIDDEN_EXCEPTION));
  }
}
