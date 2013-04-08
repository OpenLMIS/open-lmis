/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller.commtrack;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.commtrack.domain.CommtrackRequisition;
import org.openlmis.commtrack.service.CommtrackService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommtrackControllerTest {

  @Mock
  CommtrackService service;

  @InjectMocks
  CommtrackController controller;

  @Test
  public void shouldSubmitRequisitionForACommTrackUser() throws Exception {
    CommtrackRequisition requisition = new CommtrackRequisition();

    when(service.submitRequisition(requisition)).thenReturn(1);

    ResponseEntity<OpenLmisResponse> response = controller.submitRequisition(requisition);

    assertThat((Integer) response.getBody().getData().get("R&R"), is(1));
  }
}
