/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.allocation.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.allocation.domain.DeliveryZone;
import org.openlmis.allocation.response.AllocationResponse;
import org.openlmis.allocation.service.DeliveryZoneService;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.Program;
import org.openlmis.db.categories.UnitTests;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.openlmis.allocation.controller.DeliveryZoneController.DELIVERY_ZONES;
import static org.openlmis.core.domain.Right.PLAN_DISTRIBUTION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DeliveryZoneControllerTest {


  @InjectMocks
  DeliveryZoneController controller;

  @Mock
  DeliveryZoneService service;


  MockHttpServletRequest request;
  private static final String USER = "user";
  private static final Long USER_ID = 1l;

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER_ID);

    request.setSession(session);
  }

  @Test
  public void shouldGetAllDeliveryZonesForUser() throws Exception {

    List<DeliveryZone> deliveryZones = new ArrayList<>();
    when(service.getByUserForRight(USER_ID, PLAN_DISTRIBUTION)).thenReturn(deliveryZones);

    ResponseEntity<AllocationResponse> response = controller.getDeliveryZonesForInitiatingAllocation(request);

    assertThat((List<DeliveryZone>) response.getBody().getData().get(DELIVERY_ZONES), is(deliveryZones));
  }

  @Test
  public void shouldGetProgramsForADeliveryZone() throws Exception {
    List<Program> programs = new ArrayList<>();
    when(service.getProgramsForDeliveryZone(1l)).thenReturn(programs);
    ArrayList<DeliveryZone> deliveryZones = new ArrayList<DeliveryZone>() {{
      add(new DeliveryZone(1l));
    }};
    when(service.getByUserForRight(USER_ID, PLAN_DISTRIBUTION)).thenReturn(deliveryZones);
    ResponseEntity<AllocationResponse> response = controller.getProgramsForDeliveryZone(request, 1l);

    assertThat((List<Program>) response.getBody().getData().get("deliveryZonePrograms"), is(programs));
  }

  @Test
  public void shouldThrowErrorIfUserNotAuthorizedForDeliveryZone() throws Exception {
    List<Program> programs = new ArrayList<>();
    when(service.getProgramsForDeliveryZone(1l)).thenReturn(programs);
    when(service.getByUserForRight(USER_ID, PLAN_DISTRIBUTION)).thenReturn(new ArrayList<DeliveryZone>());

    ResponseEntity<AllocationResponse> response = controller.getProgramsForDeliveryZone(request, 1l);

    assertThat(response.getBody().getErrorMsg(), is("unauthorized"));
    assertThat(response.getStatusCode(), is(UNAUTHORIZED));
  }
}
