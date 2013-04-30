/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.reporting.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Report;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.BeansException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.jasperreports.AbstractJasperReportsSingleFormatView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsCsvView;

import javax.sql.DataSource;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ContextLoader.class)
public class JasperReportsViewFactoryTest {

  public static final String VIEW_FORMAT = "pdf";

  @Mock
  private Map<String, AbstractJasperReportsSingleFormatView> jasperViews;

  @Mock
  private WebApplicationContext webContext;

  @Mock
  private DataSource dataSource;

  @InjectMocks
  private JasperReportsViewFactory viewFactory;
  private Report report;

  private AbstractJasperReportsSingleFormatView jasperReportsView;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    report = new Report(new MockMultipartFile("facilityReport.jrxml", new byte[1]), 1);
    mockStatic(ContextLoader.class);
    when(ContextLoader.getCurrentWebApplicationContext()).thenReturn(null);
    jasperReportsView = spy(new JasperReportsCsvView());
    when(jasperViews.get(VIEW_FORMAT)).thenReturn(jasperReportsView);
  }

  @Test
  public void shouldGetRequestedViewAndSetDataSourceAndWebContextInJasperView() throws Exception {
    AbstractJasperReportsSingleFormatView reportView = viewFactory.getJasperReportsView(report, VIEW_FORMAT);

    assertThat(reportView, is(jasperReportsView));
    verify(jasperReportsView).setJdbcDataSource(dataSource);
  }
}
