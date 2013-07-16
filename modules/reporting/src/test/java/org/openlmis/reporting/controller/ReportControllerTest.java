/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.reporting.controller;

import net.sf.jasperreports.engine.JasperFillManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.reporting.model.ReportTemplate;
import org.openlmis.reporting.repository.mapper.ReportTemplateMapper;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import javax.sql.DataSource;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.reporting.controller.ReportController.USER_ID;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@Category(UnitTests.class)
@PrepareForTest(ReportController.class)
public class ReportControllerTest {


  @Mock
  ReportTemplateMapper reportTemplateMapper;

  @Mock
  private JasperReportsViewFactory viewFactory;

  @Mock
  private DataSource dataSource;

  @InjectMocks
  ReportController reportController;

  private MockHttpServletRequest httpServletRequest;
  private MockHttpSession session;
  private long userId = 1l;


  @Before
  public void setUp() {
    httpServletRequest = new MockHttpServletRequest();
    session = new MockHttpSession();
    session.setAttribute(USER_ID, userId);
    httpServletRequest.setSession(session);
  }
  @Test
  public void shouldGenerateReportInRequestedFormat() throws Exception {
    ReportTemplate reportTemplate = new ReportTemplate();
    when(reportTemplateMapper.getById(1)).thenReturn(reportTemplate);
    JasperReportsMultiFormatView mockView = mock(JasperReportsMultiFormatView.class);
    HashMap<String, Object> parameterMap = new HashMap<>();
    parameterMap.put("createdBy", userId);
    when(viewFactory.getJasperReportsView(reportTemplate, parameterMap)).thenReturn(mockView);
    whenNew(HashMap.class).withNoArguments().thenReturn(parameterMap);

    ModelAndView modelAndView = reportController.generateReport(httpServletRequest, 1, "pdf");

    assertThat((JasperReportsMultiFormatView) modelAndView.getView(), is(mockView));
    verify(viewFactory).getJasperReportsView(reportTemplate, parameterMap);
    verify(reportTemplateMapper).getById(1);
  }



}
