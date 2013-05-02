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
import org.openlmis.core.domain.ReportTemplate;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import javax.sql.DataSource;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JasperReportsViewFactory.class)
public class JasperReportsViewFactoryTest {

  @Mock
  private DataSource dataSource;

  @InjectMocks
  private JasperReportsViewFactory viewFactory;

  private ReportTemplate reportTemplate;

  private JasperReportsMultiFormatView jasperReportsView;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    reportTemplate = new ReportTemplate("facilityReport", new MockMultipartFile("facilityReport.jrxml","facilityReport.jrxml","", new byte[1]), 1);
    jasperReportsView = spy(new JasperReportsMultiFormatView());
  }

  @Test
  public void shouldGetRequestedViewAndSetDataSourceAndWebContextInJasperView() throws Exception {
    whenNew(JasperReportsMultiFormatView.class).withNoArguments().thenReturn(jasperReportsView);

    JasperReportsMultiFormatView reportView = viewFactory.getJasperReportsView(reportTemplate);

    assertThat(reportView, is(jasperReportsView));
    verify(jasperReportsView).setJdbcDataSource(dataSource);
  }
}
