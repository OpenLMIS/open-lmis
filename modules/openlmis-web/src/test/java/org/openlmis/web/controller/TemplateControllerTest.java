/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.reporting.model.Template;
import org.openlmis.reporting.service.TemplateService;
import org.openlmis.web.response.OpenLmisResponse;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.web.controller.TemplateController.CONSISTENCY_REPORT;
import static org.openlmis.web.controller.TemplateController.JASPER_CREATE_REPORT_SUCCESS;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest(TemplateController.class)
public class TemplateControllerTest {

  @Mock
  private TemplateService templateService;

  @Mock
  private MessageService messageService;

  @InjectMocks
  private TemplateController controller;

  private MockHttpServletRequest request;

  private static final Long USER = 1L;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER_ID, USER);
    request.setSession(session);
  }

  @Test
  public void shouldUploadJasperTemplateFileIfValid() throws Exception {
    MockMultipartFile reportTemplateFile = new MockMultipartFile("template.jrxml", "template.jrxml", "", new byte[1]);
    Template template = new Template();
    when(messageService.message(JASPER_CREATE_REPORT_SUCCESS)).thenReturn("Report created successfully");

    ResponseEntity<OpenLmisResponse> response = controller.createJasperReportTemplate(request, reportTemplateFile, template);

    assertThat(response.getBody().getSuccessMsg(), is("Report created successfully"));
    verify(templateService).validateFileAndInsertTemplate(template, reportTemplateFile, USER, CONSISTENCY_REPORT);
  }

  @Test
  public void shouldGiveErrorForInvalidReport() throws Exception {
    Template template = new Template();
    doThrow(new DataException("Error message")).when(templateService).validateFileAndInsertTemplate(template, null, USER, CONSISTENCY_REPORT);

    ResponseEntity<OpenLmisResponse> response = controller.createJasperReportTemplate(request, null, template);

    assertThat(response.getBody().getErrorMsg(), is("Error message"));
  }

  @Test
  public void shouldGetAllReportTemplates() throws Exception {
    List<Template> expected = new ArrayList<>();
    when(templateService.getAll()).thenReturn(expected);

    assertThat(controller.getAll(), is(expected));
  }

}
