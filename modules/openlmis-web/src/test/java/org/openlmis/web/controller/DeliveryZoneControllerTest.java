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
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.Program;
import org.openlmis.core.service.AllocationPermissionService;
import org.openlmis.core.service.DeliveryZoneService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.core.web.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.RightName.MANAGE_DISTRIBUTION;
import static org.openlmis.web.controller.DeliveryZoneController.DELIVERY_ZONES;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DeliveryZoneControllerTest {
  @InjectMocks
  DeliveryZoneController controller;

  @Mock
  DeliveryZoneService service;

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

    request.setSession(session);
  }

  @Test
  public void shouldGetAllDeliveryZonesForUser() throws Exception {
    List<DeliveryZone> deliveryZones = new ArrayList<>();
    when(service.getByUserForRight(USER_ID, MANAGE_DISTRIBUTION)).thenReturn(deliveryZones);

    ResponseEntity<OpenLmisResponse> response = controller.getDeliveryZonesForInitiatingAllocation(request);

    assertThat((List<DeliveryZone>) response.getBody().getData().get(DELIVERY_ZONES), is(deliveryZones));
  }

  @Test
  public void shouldGetAllDeliveryZones() {
    List<DeliveryZone> deliveryZones = new ArrayList<>();
    when(service.getAll()).thenReturn(deliveryZones);

    ResponseEntity<OpenLmisResponse> response = controller.getAll();

    assertThat((List<DeliveryZone>) response.getBody().getData().get(DELIVERY_ZONES), is(deliveryZones));
    verify(service).getAll();
  }

  @Test
  public void shouldGetActiveProgramsForADeliveryZone() throws Exception {
    List<Program> programs = new ArrayList<>();
    when(service.getActiveProgramsForDeliveryZone(1l)).thenReturn(programs);
    ResponseEntity<OpenLmisResponse> response = controller.getActiveProgramsForDeliveryZone(1l);

    assertThat((List<Program>) response.getBody().getData().get("deliveryZonePrograms"), is(programs));
  }

  @Test
  public void shouldGetAllProgramsForADeliveryZone() throws Exception {
    List<Program> programs = new ArrayList<>();
    when(service.getAllProgramsForDeliveryZone(1l)).thenReturn(programs);
    ResponseEntity<OpenLmisResponse> response = controller.getAllProgramsForDeliveryZone(1l);

    assertThat((List<Program>) response.getBody().getData().get("deliveryZonePrograms"), is(programs));
  }

  @Test
  public void shouldGetDeliveryZoneById() throws Exception {
    DeliveryZone zone = new DeliveryZone();
    when(service.getById(1l)).thenReturn(zone);
    when(permissionService.hasPermissionOnZone(USER_ID, 1l)).thenReturn(true);

    ResponseEntity<OpenLmisResponse> response = controller.get(1l);

    verify(service).getById(1l);
    assertThat((DeliveryZone) response.getBody().getData().get("zone"), is(zone));
  }
}
