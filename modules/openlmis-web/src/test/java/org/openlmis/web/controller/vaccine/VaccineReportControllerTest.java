/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.web.controller.vaccine;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.domain.*;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.UserService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.dto.ReportStatusDTO;
import org.openlmis.vaccine.service.reports.VaccineReportService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.List;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class VaccineReportControllerTest {

  @Mock
  VaccineReportService service;

  @Mock
  ProgramService programService;

  @Mock
  FacilityService facilityService;

  @Mock
  UserService userService;

  @InjectMocks
  VaccineReportController controller;

  private MockHttpSession session;
  private MockHttpServletRequest httpServletRequest;

  @Before
  public void setUp() throws Exception {
    httpServletRequest = new MockHttpServletRequest();
    session = new MockHttpSession();
    httpServletRequest.setSession(session);
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, 1L);
  }

  @Test
  public void shouldGetProgramsForConfiguration() throws Exception {
    List<Program> programs = new ArrayList<>();

    when(programService.getAllIvdPrograms()).thenReturn(programs);
    ResponseEntity<OpenLmisResponse> response = controller.getProgramsForConfiguration();

    verify(programService).getAllIvdPrograms();
    assertThat(programs, is(response.getBody().getData().get("programs")));
  }


  @Test
  public void shouldGetProgramsForIVDForm() throws Exception {
    List<Program> programs = new ArrayList<>();
    User user = new User();
    user.setFacilityId(1L);

    when(programService.getIvdProgramsSupportedByUserHomeFacilityWithRights(1L, 1L, "CREATE_REQUISITION", "AUTHORIZE_REQUISITION")).thenReturn(programs);
    when(userService.getById(1L)).thenReturn(user);
    ResponseEntity<OpenLmisResponse> response = controller.getProgramForIvdFormHomeFacility(httpServletRequest);

    verify(programService).getIvdProgramsSupportedByUserHomeFacilityWithRights(1L, 1L, "CREATE_REQUISITION", "AUTHORIZE_REQUISITION");
    assertThat(programs, is(response.getBody().getData().get("programs")));
  }

  @Test
  public void shouldGetFacilities() throws Exception {
    List<Facility> facilities = new ArrayList<>();
    when(facilityService.getUserSupervisedFacilities(1L, 1L, RightName.CREATE_REQUISITION)).thenReturn(facilities);
    ResponseEntity<OpenLmisResponse> response = controller.getFacilities(1L, httpServletRequest);

    assertThat(facilities, is(response.getBody().getData().get("facilities")));
  }

  @Test
  public void shouldGetPeriods() throws Exception {
    List<ReportStatusDTO> periods = new ArrayList<>();
    when(service.getPeriodsFor(1L, 1L)).thenReturn(periods);
    ResponseEntity<OpenLmisResponse> response = controller.getPeriods(1L, 1L, httpServletRequest);

    assertThat(periods, is(response.getBody().getData().get("periods")));
  }

  @Test
  public void shouldInitialize() throws Exception {

  }

  @Test
  public void shouldGetReport() throws Exception {

  }

  @Test
  public void shouldSave() throws Exception {

  }

  @Test
  public void shouldSubmit() throws Exception {

  }
}