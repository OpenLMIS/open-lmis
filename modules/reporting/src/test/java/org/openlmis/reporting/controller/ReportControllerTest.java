/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.reporting.controller;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Report;
import org.openlmis.core.repository.mapper.ReportMapper;
import org.springframework.web.servlet.ModelAndView;

import javax.sql.DataSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReportControllerTest {


  @Mock
  ReportMapper reportMapper;

  @Mock
  private JasperReportsViewFactory viewFactory;

  @Mock
  private DataSource dataSource;

  @InjectMocks
  ReportController reportController;

  @Test
  public void shouldGeneratePdfReport() throws Exception {
    int requisition_id = 36;
    Report report = new Report();
    report.setData(new byte[1]);
    report.setName("Sample Report");
    report.setParameters("<rnrId, Integer>");
    when(reportMapper.getById(requisition_id)).thenReturn(report);
    ModelAndView modelAndView = reportController.generatePdfReport(requisition_id, "pdf");
    assertThat(modelAndView, is(Matchers.notNullValue()));
  }
}
