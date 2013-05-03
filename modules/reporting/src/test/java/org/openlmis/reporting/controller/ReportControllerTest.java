/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.reporting.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.ReportTemplate;
import org.openlmis.core.repository.mapper.ReportTemplateMapper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import javax.sql.DataSource;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReportControllerTest {


  @Mock
  ReportTemplateMapper reportTemplateMapper;

  @Mock
  private JasperReportsViewFactory viewFactory;

  @Mock
  private DataSource dataSource;

  @InjectMocks
  ReportController reportController;

  MockHttpServletRequest request;

  @Test
  public void shouldGenerateReportInRequestedFormat() throws Exception {
    ReportTemplate reportTemplate = new ReportTemplate();
    when(reportTemplateMapper.getById(1)).thenReturn(reportTemplate);
    request = new MockHttpServletRequest();
    JasperReportsMultiFormatView mockView = mock(JasperReportsMultiFormatView.class);
    when(viewFactory.getJasperReportsView(reportTemplate)).thenReturn(mockView);

    ModelAndView modelAndView = reportController.generateReport(request, 1, "pdf");

    assertThat((JasperReportsMultiFormatView) modelAndView.getView(), is(mockView));
    verify(viewFactory).getJasperReportsView(reportTemplate);
    verify(reportTemplateMapper).getById(1);
  }

}
