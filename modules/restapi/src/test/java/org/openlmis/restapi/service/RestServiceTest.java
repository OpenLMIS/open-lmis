/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.restapi.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.restapi.domain.Report;
import org.openlmis.restapi.service.RestService;
import org.openlmis.rnr.domain.Rnr;
import org.openlmis.rnr.domain.RnrLineItem;
import org.openlmis.rnr.service.RequisitionService;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.restapi.builder.CommtrackReportBuilder.defaultCommtrackReport;

@RunWith(MockitoJUnitRunner.class)
public class RestServiceTest {

  @Mock
  RequisitionService requisitionService;

  @InjectMocks
  RestService service;

  Rnr requisition;
  Report report;

  @Before
  public void setUp() throws Exception {
    report = make(a(defaultCommtrackReport));
    requisition = new Rnr();
    requisition.setId(2);
    when(requisitionService.initiate(report.getFacilityId(), report.getProgramId(), report.getPeriodId(), report.getUserId()))
      .thenReturn(requisition);
  }

  @Test
  public void shouldCreateAndSubmitARequisition() throws Exception {
    List<RnrLineItem> products = new ArrayList<RnrLineItem>() ;
    products.add(new RnrLineItem());
    report.setProducts(products);

    Rnr expectedRequisition = service.submitReport(report);

    verify(requisitionService).initiate(report.getFacilityId(), report.getProgramId(), report.getPeriodId(), report.getUserId());
    verify(requisitionService).submit(expectedRequisition);
    verify(requisitionService).authorize(expectedRequisition);
    assertThat(expectedRequisition, is(requisition));
    assertThat(expectedRequisition.getFullSupplyLineItems(), is(products));
  }

  @Test
  public void shouldValidateThatTheReportContainsAllMandatoryFields() throws Exception {
    Report spyReport = spy(report);

    service.submitReport(spyReport);

    verify(spyReport).validate();
  }
}
