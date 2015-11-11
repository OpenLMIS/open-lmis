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
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.core.service.UserService;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.vaccine.domain.reports.VaccineReport;
import org.openlmis.vaccine.dto.ReportStatusDTO;
import org.openlmis.vaccine.service.reports.VaccineReportService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;


@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({Date.class})
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
  public void shouldGetPeriods() throws Exception {
    Date currentDate = new Date();
    List<ReportStatusDTO> periods = new ArrayList<>();
    when(service.getPeriodsFor(1L, 1L, currentDate)).thenReturn(periods);
    whenNew(Date.class).withNoArguments().thenReturn(currentDate);

    ResponseEntity<OpenLmisResponse> response = controller.getPeriods(1L, 1L);
    
    assertThat(periods, is(response.getBody().getData().get("periods")));
  }

  @Test
  public void shouldInitialize() throws Exception {
    VaccineReport report = new VaccineReport();
    when(service.initialize(1L, 1L, 1L, 1L)).thenReturn(report);

    ResponseEntity<OpenLmisResponse> response = controller.initialize(1L, 1L, 1L, httpServletRequest);

    verify(service).initialize(1L, 1L, 1L, 1L);
    assertThat(report, is(response.getBody().getData().get("report")));
  }

  @Test
  public void shouldGetReport() throws Exception {
    VaccineReport report = new VaccineReport();
    when(service.getById(23L)).thenReturn(report);

    ResponseEntity<OpenLmisResponse> response = controller.getReport(23L);

    verify(service).getById(23L);
    assertThat(report, is(response.getBody().getData().get("report")));
  }

  @Test
  public void shouldSave() throws Exception {
    VaccineReport report = new VaccineReport();
    doNothing().when(service).save(report, 1L);

    ResponseEntity<OpenLmisResponse> response = controller.save(report, httpServletRequest);

    verify(service).save(report, 1L);
    assertThat(report, is(response.getBody().getData().get("report")));
  }

  @Test
  public void shouldSubmit() throws Exception {
    VaccineReport report = new VaccineReport();
    doNothing().when(service).submit(report, 1L);

    ResponseEntity<OpenLmisResponse> response = controller.submit(report, httpServletRequest);

    verify(service).submit(report, 1L);

    // the status would have changed.
    assertThat(report, is(response.getBody().getData().get("report")));
  }
}