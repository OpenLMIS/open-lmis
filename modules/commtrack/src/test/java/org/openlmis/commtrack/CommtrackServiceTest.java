/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.commtrack;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.commtrack.domain.CommtrackReport;
import org.openlmis.commtrack.service.CommtrackService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.service.RequisitionService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommtrackServiceTest {

  @Mock
  RequisitionService requisitionService;

  @InjectMocks
  CommtrackService service;

  @Test
  public void shouldCreateAndSubmitARequisition() throws Exception {
    CommtrackReport report = new CommtrackReport();
    List<RnrLineItem> products = new ArrayList<RnrLineItem>() ;
    products.add(new RnrLineItem());
    report.setProducts(products);
    Rnr requisition = new Rnr();
    requisition.setId(2);
    when(requisitionService.initiate(report.getFacilityId(), report.getProgramId(), report.getPeriodId(), report.getUserId()))
      .thenReturn(requisition);

    Integer requisitionId = service.submitRequisition(report);

    verify(requisitionService).initiate(report.getFacilityId(), report.getProgramId(), report.getPeriodId(), report.getUserId());
    verify(requisitionService).submit(requisition);
    verify(requisitionService).authorize(requisition);
    assertThat(requisitionId, is(2));
    assertThat(requisition.getFullSupplyLineItems(), is(products));
  }
}
