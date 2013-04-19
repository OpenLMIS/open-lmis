/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.restapi.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.exception.DataException;
import org.openlmis.restapi.domain.Report;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestService;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.restapi.response.RestResponse.ERROR;

@RunWith(MockitoJUnitRunner.class)
public class RestControllerTest {

  @Mock
  RestService service;

  @InjectMocks
  RestController controller;

  Principal principal;

  @Before
  public void setUp() throws Exception {
    principal = mock(Principal.class);
    when(principal.getName()).thenReturn("vendor name");
  }

  @Test
  public void shouldSubmitRequisitionForACommTrackUser() throws Exception {
    Report report = new Report();

    Rnr requisition = new Rnr();
    requisition.setId(1);
    when(service.submitReport(report)).thenReturn(requisition);

    ResponseEntity<RestResponse> response = controller.submitRequisition(report, principal);

    assertThat((Integer) response.getBody().getData().get("R&R"), is(1));
  }

  @Test
  public void shouldGiveErrorMessageIfReportInvalid() throws Exception {
    String errorMessage = "some error";
    Report report = new Report();

    Rnr requisition = new Rnr();
    requisition.setId(1);
    doThrow(new DataException(errorMessage)).when(service).submitReport(report);

    ResponseEntity<RestResponse> response = controller.submitRequisition(report, principal);

    assertThat((String) response.getBody().getData().get(ERROR), is(errorMessage));
  }

  @Test
  public void shouldSetVendorNameInReport() throws Exception {
    String errorMessage = "some error";
    Report report = new Report();

    Rnr requisition = new Rnr();
    requisition.setId(1);
    doThrow(new DataException(errorMessage)).when(service).submitReport(report);

    controller.submitRequisition(report, principal);

    assertThat(report.getVendor().getName(), is("vendor name"));
  }

  @Test
  public void shouldApproveReport() throws Exception {
    Report report = new Report();
    Integer id = 1;
    Rnr expectedRnr = new Rnr();
    when(service.approve(report)).thenReturn(expectedRnr);

    Rnr orderedRnr = controller.approve(id, report, principal);

    assertThat(orderedRnr, is(expectedRnr));
    assertThat(report.getVendor().getName(), is("vendor name"));
    assertThat(report.getRnrId(), is(1));
    verify(service).approve(report);
  }

  @Test
  public void shouldResolveUnhandledException() throws Exception {
    final ResponseEntity<RestResponse> response = controller.handleException(new Exception());
    final RestResponse body = response.getBody();
    assertThat((String) body.getData().get(ERROR), is("Oops, something has gone wrong. Please try again later"));
  }
}
