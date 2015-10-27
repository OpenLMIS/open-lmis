/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.restapi.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.restapi.domain.ReplenishmentDTO;
import org.openlmis.restapi.domain.Report;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestRequisitionService;
import org.openlmis.rnr.domain.Rnr;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.restapi.controller.RestRequisitionController.RNR;
import static org.openlmis.restapi.controller.RestRequisitionController.UNEXPECTED_EXCEPTION;
import static org.openlmis.restapi.response.RestResponse.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.http.HttpStatus.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest({RestResponse.class, ReplenishmentDTO.class})
public class RestRequisitionControllerTest {

  @Mock
  RestRequisitionService service;


  @InjectMocks
  RestRequisitionController controller;

  @Mock
  MessageService messageService;

  Principal principal;

  @Before
  public void setUp() throws Exception {
    principal = mock(Principal.class);
    when(principal.getName()).thenReturn("1");
    mockStatic(RestResponse.class);
  }

  @Test
  public void shouldSubmitRequisitionForACommTrackUser() throws Exception {
    Report report = new Report();

    Rnr requisition = new Rnr();
    requisition.setId(1L);
    when(service.submitReport(report, 1L)).thenReturn(requisition);
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(RNR, requisition.getId()), OK);
    when(RestResponse.response(RNR, requisition.getId(), HttpStatus.CREATED)).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = controller.submitRequisition(report, principal);

    assertThat((Long) response.getBody().getData().get(RNR), is(1L));
  }

  @Test
  public void shouldSubmitSDPRequisitionForACommTrackUser() throws Exception {
    Report report = new Report();

    Rnr requisition = new Rnr();
    requisition.setId(1L);
    when(service.submitSdpReport(report, 1L)).thenReturn(requisition);
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(RNR, requisition.getId()), OK);
    when(RestResponse.response(RNR, requisition.getId(), HttpStatus.CREATED)).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = controller.submitSDPRequisition(report, principal);

    assertThat((Long) response.getBody().getData().get(RNR), is(1L));
  }


  @Test
  public void shouldGiveErrorMessageIfReportInvalid() throws Exception {
    String errorMessage = "some error";
    Report report = new Report();

    Rnr requisition = new Rnr();
    requisition.setId(1L);
    DataException dataException = new DataException(errorMessage);
    doThrow(dataException).when(service).submitReport(report, 1L);
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(ERROR, errorMessage), HttpStatus.BAD_REQUEST);
    when(error(dataException.getOpenLmisMessage(), HttpStatus.BAD_REQUEST)).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = controller.submitRequisition(report, principal);

    assertThat((String) response.getBody().getData().get(ERROR), is(errorMessage));
  }

  @Test
  public void shouldApproveReport() throws Exception {

    Long requisitionId = 1L;
    Long userId = 1L;
    Rnr expectedRnr = new Rnr();
    expectedRnr.setId(1L);
    Report report = mock(Report.class);

    doNothing().when(report).validateForApproval();
    doNothing().when(service).approve(report, requisitionId, userId);
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(SUCCESS, "success"), OK);
    when(RestResponse.success("msg.rnr.approved.success")).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = controller.approve(requisitionId, report, principal);

    assertThat(response.getBody().getSuccess(), is("success"));
    verify(service).approve(report, requisitionId, userId);
  }

  @Test
  public void shouldNotApproveRnrIfReportValidationFails() throws Exception {
    Report report = mock(Report.class);

    String errorMessage = "validation error";
    Long requisitionId = 1L;

    DataException dataException = new DataException(errorMessage);
    doThrow(dataException).when(report).validateForApproval();
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(ERROR, errorMessage), BAD_REQUEST);
    when(error(dataException.getOpenLmisMessage(), BAD_REQUEST)).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = controller.approve(requisitionId, report, principal);

    assertThat((String) response.getBody().getData().get(ERROR), is(errorMessage));
  }

  @Test
  public void shouldGiveErrorMessageIfSomeErrorOccursWhileApproving() throws Exception {
    String errorMessage = "some error";
    Long requisitionId = 1L;
    Report report = mock(Report.class);

    doNothing().when(report).validateForApproval();
    DataException dataException = new DataException(errorMessage);
    doThrow(dataException).when(service).approve(report, requisitionId, 1L);
    ResponseEntity<RestResponse> expectResponse = new ResponseEntity<>(new RestResponse(ERROR, errorMessage), BAD_REQUEST);
    when(error(dataException.getOpenLmisMessage(), BAD_REQUEST)).thenReturn(expectResponse);

    ResponseEntity<RestResponse> response = controller.approve(requisitionId, report, principal);

    assertThat((String) response.getBody().getData().get(ERROR), is(errorMessage));
  }

  @Test
  public void shouldResolveUnhandledException() throws Exception {
    String errorMessage = "Oops, something has gone wrong. Please try again later";
    when(messageService.message(UNEXPECTED_EXCEPTION)).thenReturn(errorMessage);

    ResponseEntity<RestResponse> expectedResponse = new ResponseEntity<>(new RestResponse(ERROR, errorMessage), INTERNAL_SERVER_ERROR);

    when(error(UNEXPECTED_EXCEPTION, INTERNAL_SERVER_ERROR)).thenReturn(expectedResponse);

    final ResponseEntity<RestResponse> response = controller.handleException(new Exception());

    final RestResponse body = response.getBody();
    assertThat((String) body.getData().get(ERROR), is(errorMessage));
  }

  @Test
  public void shouldGetRequisitionById() throws Exception {
    mockStatic(ReplenishmentDTO.class);
    Long rnrId = 3L;
    Rnr rnr = new Rnr(rnrId);
    ReplenishmentDTO replenishmentDTO = new ReplenishmentDTO();
    Order order = mock(Order.class);
    when(ReplenishmentDTO.prepareForREST(rnr, order)).thenReturn(replenishmentDTO);
    when(service.getReplenishmentDetails(rnrId)).thenReturn(replenishmentDTO);
    ResponseEntity<RestResponse> expectedResponse = new ResponseEntity<>(new RestResponse("requisition", replenishmentDTO), OK);
    when(RestResponse.response("requisition", replenishmentDTO)).thenReturn(expectedResponse);

    ResponseEntity<RestResponse> response = controller.getReplenishment(rnrId);

    assertThat(response, is(expectedResponse));
    verify(service).getReplenishmentDetails(rnrId);
  }

  @Test
  public void shouldThrowErrorIfGetServiceThrowsError() throws Exception {
    Long rnrId = 3L;
    ReplenishmentDTO replenishmentDTO = new ReplenishmentDTO();
    DataException exception = new DataException("some error");
    doThrow(exception).when(service).getReplenishmentDetails(rnrId);

    ResponseEntity<RestResponse> expectedResponse = new ResponseEntity<>(new RestResponse("requisition", replenishmentDTO), BAD_REQUEST);
    when(error(exception.getOpenLmisMessage(), BAD_REQUEST)).thenReturn(expectedResponse);

    ResponseEntity<RestResponse> response = controller.getReplenishment(rnrId);

    assertThat(response, is(expectedResponse));
  }

  @Test
  public void shouldReturnSuccessCodeAndRequisitionListIfNoException() {
    String facilityCode = "F1";

    List<Report> expectedRequisitions = new ArrayList<>();
    when(service.getRequisitionsByFacility(facilityCode)).thenReturn(expectedRequisitions);
    String requisitionsKey = "requisitions";
    ResponseEntity<RestResponse> expectedResponse = new ResponseEntity<>(new RestResponse(requisitionsKey, expectedRequisitions), OK);
    when(RestResponse.response(requisitionsKey, expectedRequisitions, HttpStatus.OK)).thenReturn(expectedResponse);

    ResponseEntity<RestResponse> response = controller.getRequisitionsByFacility(facilityCode);
    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat((List<Report>) response.getBody().getData().get(requisitionsKey), is(expectedRequisitions));
  }

  @Test
  public void shouldReturnErrorIfCodeAndRequisitionThrowsException() {
    String facilityCode = "F1";

    DataException exception = new DataException("some error");
    doThrow(exception).when(service).getRequisitionsByFacility(facilityCode);

    ResponseEntity<RestResponse> expectedResponse = new ResponseEntity<>(new RestResponse(ERROR, messageService.message("some error")), BAD_REQUEST);
    when(error(exception.getOpenLmisMessage(), BAD_REQUEST)).thenReturn(expectedResponse);

    ResponseEntity<RestResponse> response = controller.getRequisitionsByFacility(facilityCode);
    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
  }

}
