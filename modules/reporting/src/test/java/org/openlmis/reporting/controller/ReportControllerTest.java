/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.reporting.controller;

import net.sf.jasperreports.engine.JRException;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.reporting.model.Template;
import org.openlmis.reporting.service.JasperReportsViewFactory;
import org.openlmis.reporting.service.TemplateService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.reporting.controller.ReportController.USER_ID;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
public class ReportControllerTest {

  @Mock
  TemplateService templateService;

  @Mock
  private JasperReportsViewFactory viewFactory;

  @Mock
  private DataSource dataSource;

  @InjectMocks
  ReportController controller;

  private MockHttpServletRequest request;

  @Before
  public void setUp() {
    request = new MockHttpServletRequest();
    Long userId = 1L;
    request.getSession().setAttribute(USER_ID, userId);
  }

  @Test
  public void shouldGetReportParameters() throws Exception {
    Long id = 2L;
    Template template = new Template();
    when(templateService.getLWById(id)).thenReturn(template);

    Template reportWithParameters = controller.getReportWithParameters(id);

    assertThat(reportWithParameters, is(template));
  }

  @Test
  public void shouldGenerateReportInRequestedFormat() throws Exception {
    Template template = new Template();
    String format = "pdf";
    Map<String, Object> parameterMap = new HashMap<>();
    JasperReportsMultiFormatView mockView = mock(JasperReportsMultiFormatView.class);

    when(templateService.getById(1L)).thenReturn(template);
    when(viewFactory.getJasperReportsView(template)).thenReturn(mockView);
    when(templateService.getParametersMap(template, 1, request, format)).thenReturn(parameterMap);

    ModelAndView modelAndView = controller.generateReport(request, 1L, format);

    assertThat((JasperReportsMultiFormatView) modelAndView.getView(), is(mockView));
    assertThat(modelAndView.getModel(), is(parameterMap));
    verify(viewFactory).getJasperReportsView(template);
    verify(templateService).getById(1L);
    verify(templateService).getParametersMap(template, 1, request, format);
  }

  @Test
  public void shouldReturnInvalidReportPageInCaseOfErrors() throws Exception {
    Template template = new Template();
    String format = "pdf";

    when(templateService.getById(1L)).thenReturn(template);
    doThrow(new JRException("error")).when(viewFactory).getJasperReportsView(template);

    ModelAndView modelAndView = controller.generateReport(request, 1L, format);

    assertThat(modelAndView.getViewName(), is("error-page"));
    verify(viewFactory).getJasperReportsView(template);
    verify(templateService).getById(1L);
  }
}
