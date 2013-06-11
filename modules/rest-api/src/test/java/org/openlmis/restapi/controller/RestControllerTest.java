/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.restapi.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.restapi.domain.Report;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestService;
import org.openlmis.rnr.domain.Rnr;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.restapi.controller.RestController.RNR;
import static org.openlmis.restapi.controller.RestController.UNEXPECTED_EXCEPTION;
import static org.openlmis.restapi.response.RestResponse.ERROR;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(RestResponse.class)
public class RestControllerTest {

  @Mock
  RestService service;

  @InjectMocks
  RestController controller;

  @Mock
  MessageService messageService;

  Principal principal;

  @Before
  public void setUp() throws Exception {
    principal = mock(Principal.class);
    when(principal.getName()).thenReturn("vendor name");
    mockStatic(RestResponse.class);
  }

  @Test
  public void shouldSubmitRequisitionForACommTrackUser() throws Exception {
    Report report = new Report();

    Rnr requisition = new Rnr();
    requisition.setId(1L);
    when(service.submitReport(report)).thenReturn(requisition);
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(RNR, requisition.getId()), HttpStatus.OK);
    when(RestResponse.response(RNR, requisition.getId())).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = controller.submitRequisition(report, principal);

    assertThat((Long) response.getBody().getData().get(RNR), is(1L));
  }

  @Test
  public void shouldGiveErrorMessageIfReportInvalid() throws Exception {
    String errorMessage = "some error";
    Report report = new Report();

    Rnr requisition = new Rnr();
    requisition.setId(1L);
    DataException dataException = new DataException(errorMessage);
    doThrow(dataException).when(service).submitReport(report);
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(ERROR, errorMessage), HttpStatus.BAD_REQUEST);
    when(RestResponse.error(dataException.getOpenLmisMessage(), HttpStatus.BAD_REQUEST)).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = controller.submitRequisition(report, principal);

    assertThat((String) response.getBody().getData().get(ERROR), is(errorMessage));
  }

  @Test
  public void shouldSetVendorNameInReport() throws Exception {
    String errorMessage = "some error";
    Report report = new Report();

    Rnr requisition = new Rnr();
    requisition.setId(1L);
    doThrow(new DataException(errorMessage)).when(service).submitReport(report);

    controller.submitRequisition(report, principal);

    assertThat(report.getVendor().getName(), is("vendor name"));
  }

  @Test
  public void shouldApproveReport() throws Exception {
    Report report = new Report();
    Long id = 1L;
    Rnr expectedRnr = new Rnr();
    when(service.approve(report)).thenReturn(expectedRnr);

    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(RNR, expectedRnr.getId()), HttpStatus.OK);
    when(RestResponse.response(RNR, expectedRnr.getId())).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = controller.approve(id, report, principal);

    assertThat((Long) response.getBody().getData().get(RNR), is(expectedRnr.getId()));
    assertThat(report.getVendor().getName(), is("vendor name"));
    assertThat(report.getRequisitionId(), is(1L));
    verify(service).approve(report);
  }

  @Test
  public void shouldGiveErrorMessageIfSomeErrorOccursWhileApproving() throws Exception {
    String errorMessage = "some error";
    Long requisitionId = 1L;
    Report report = new Report();

    DataException dataException = new DataException(errorMessage);
    doThrow(dataException).when(service).approve(report);
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(ERROR, errorMessage), HttpStatus.BAD_REQUEST);
    when(RestResponse.error(dataException.getOpenLmisMessage(), HttpStatus.BAD_REQUEST)).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = controller.approve(requisitionId, report, principal);

    assertThat((String) response.getBody().getData().get(ERROR), is(errorMessage));
  }

  @Test
  public void shouldResolveUnhandledException() throws Exception {
    String errorMessage = "Oops, something has gone wrong. Please try again later";
    when(messageService.message(UNEXPECTED_EXCEPTION)).thenReturn(errorMessage);

    ResponseEntity<RestResponse> expectedResponse = new ResponseEntity<>(new RestResponse(ERROR, errorMessage), HttpStatus.INTERNAL_SERVER_ERROR);

    when(RestResponse.error(UNEXPECTED_EXCEPTION, HttpStatus.INTERNAL_SERVER_ERROR)).thenReturn(expectedResponse);

    final ResponseEntity<RestResponse> response = controller.handleException(new Exception());

    final RestResponse body = response.getBody();
    assertThat((String) body.getData().get(ERROR), is(errorMessage));
  }
}
