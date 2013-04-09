/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.restapi.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.restapi.domain.Report;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestService;
import org.openlmis.core.exception.DataException;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.openlmis.restapi.response.RestResponse.ERROR;

@RunWith(MockitoJUnitRunner.class)
public class RestControllerTest {

  @Mock
  RestService service;

  @InjectMocks
  RestController controller;

  @Test
  public void shouldSubmitRequisitionForACommTrackUser() throws Exception {
    Report report = new Report();

    Rnr requisition = new Rnr();
    requisition.setId(1);
    when(service.submitReport(report)).thenReturn(requisition);

    ResponseEntity<RestResponse> response = controller.submitRequisition(report);

    assertThat((Integer) response.getBody().getData().get("R&R"), is(1));
  }

  @Test
  public void shouldGiveErrorMessageIfReportInvalid() throws Exception {
    String errorMessage = "some error";
    Report report = new Report();

    Rnr requisition = new Rnr();
    requisition.setId(1);
    doThrow(new DataException(errorMessage)).when(service).submitReport(report);

    ResponseEntity<RestResponse> response = controller.submitRequisition(report);

    assertThat((String) response.getBody().getData().get(ERROR), is(errorMessage));
  }
}
