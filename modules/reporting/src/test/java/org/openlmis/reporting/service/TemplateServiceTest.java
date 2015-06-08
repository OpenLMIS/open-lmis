/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.reporting.service;

import net.sf.jasperreports.engine.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.Right;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.RightService;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.reporting.model.ReportRight;
import org.openlmis.reporting.model.Template;
import org.openlmis.reporting.model.TemplateParameter;
import org.openlmis.reporting.repository.TemplateRepository;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest({TemplateService.class, JasperCompileManager.class})
@Category(UnitTests.class)
public class TemplateServiceTest {

  @Mock
  TemplateRepository repository;

  @Mock
  RoleRightsService roleRightsService;

  @Mock
  RightService rightService;

  @Mock
  ReportRightService reportRightService;

  @Mock
  MessageService messageService;

  @InjectMocks
  TemplateService service;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldThrowErrorIfFileNotOfTypeJasperXML() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage("report.template.error.file.type");

    MockMultipartFile file = new MockMultipartFile("report.pdf", new byte[1]);
    service.validateFileAndInsertTemplate(new Template(), file);
  }

  @Test
  public void shouldThrowErrorIfFileEmpty() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage("report.template.error.file.empty");
    MockMultipartFile file = new MockMultipartFile("report.jrxml", "report.jrxml", "", new byte[0]);

    service.validateFileAndInsertTemplate(new Template(), file);
  }

  @Test
  public void shouldThrowErrorIfFileNotPresent() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage("report.template.error.file.missing");

    service.validateFileAndInsertTemplate(new Template(), null);
  }

  @Test
  public void shouldThrowErrorIfFileIsInvalid() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage("report.template.error.file.invalid");

    service.validateFileAndInsertTemplate(new Template(), new MockMultipartFile("report.jrxml", "report.jrxml", "", new byte[1]));
  }

  @Test
  public void shouldThrowErrorIfTemplateNameAlreadyExists() throws Exception {
    Template template = new Template();
    template.setName("Name");
    expectedException.expect(DataException.class);
    expectedException.expectMessage("report.template.name.already.exists");
    when(repository.getByName(template.getName())).thenThrow(new DataException("report.template.name.already.exists"));

    service.validateFileAndInsertTemplate(template, null);
  }

  @Test
  public void shouldThrowErrorIfDisplayNameOfParameterIsMissing() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage("Error Message");
    MultipartFile file = mock(MultipartFile.class);
    when(file.getOriginalFilename()).thenReturn("file.jrxml");

    mockStatic(JasperCompileManager.class);
    JasperReport report = mock(JasperReport.class);
    InputStream inputStream = mock(InputStream.class);
    when(file.getInputStream()).thenReturn(inputStream);

    JRParameter param1 = mock(JRParameter.class);
    JRParameter param2 = mock(JRParameter.class);
    JRPropertiesMap propertiesMap = mock(JRPropertiesMap.class);

    when(messageService.message("report.template.parameter.display.name.missing", param1.getName())).thenReturn("Error Message");
    when(report.getParameters()).thenReturn(new JRParameter[]{param1, param2});
    when(JasperCompileManager.compileReport(inputStream)).thenReturn(report);
    when(param1.getPropertiesMap()).thenReturn(propertiesMap);
    String[] propertyNames = {"name1"};
    when(propertiesMap.getPropertyNames()).thenReturn(propertyNames);
    when(propertiesMap.getProperty("displayName")).thenReturn(null);
    Template template = new Template();

    service.validateFileAndInsertTemplate(template, file);

    verify(repository, never()).insertWithParameters(template);
  }

  @Test
  public void shouldThrowErrorIfThereAreExtraParameterProperties() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage("Error Message");
    MultipartFile file = mock(MultipartFile.class);
    when(file.getOriginalFilename()).thenReturn("file.jrxml");

    mockStatic(JasperCompileManager.class);
    JasperReport report = mock(JasperReport.class);
    InputStream inputStream = mock(InputStream.class);
    when(file.getInputStream()).thenReturn(inputStream);

    JRParameter param1 = mock(JRParameter.class);
    JRParameter param2 = mock(JRParameter.class);
    JRPropertiesMap propertiesMap = mock(JRPropertiesMap.class);

    when(messageService.message("report.template.extra.properties", param1.getName())).thenReturn("Error Message");
    when(report.getParameters()).thenReturn(new JRParameter[]{param1, param2});
    when(JasperCompileManager.compileReport(inputStream)).thenReturn(report);
    when(param1.getPropertiesMap()).thenReturn(propertiesMap);
    String[] propertyNames = {"name1", "name2"};
    when(propertiesMap.getPropertyNames()).thenReturn(propertyNames);
    when(propertiesMap.getProperty("displayName")).thenReturn("Param Display Name");
    Template template = new Template();

    service.validateFileAndInsertTemplate(template, file);

    verify(repository, never()).insertWithParameters(template);
  }

  @Test
  public void shouldValidateFileAndSetData() throws Exception {
    String[] propertyNames = {"displayName"};
    MultipartFile file = mock(MultipartFile.class);
    when(file.getOriginalFilename()).thenReturn("file.jrxml");

    mockStatic(JasperCompileManager.class);
    JasperReport report = mock(JasperReport.class);
    InputStream inputStream = mock(InputStream.class);
    when(file.getInputStream()).thenReturn(inputStream);

    JRParameter param1 = mock(JRParameter.class);
    JRParameter param2 = mock(JRParameter.class);
    JRPropertiesMap propertiesMap = mock(JRPropertiesMap.class);
    JRExpression jrExpression = mock(JRExpression.class);

    when(report.getParameters()).thenReturn(new JRParameter[]{param1, param2});
    when(JasperCompileManager.compileReport(inputStream)).thenReturn(report);
    when(propertiesMap.getPropertyNames()).thenReturn(propertyNames);
    when(propertiesMap.getProperty("displayName")).thenReturn("Param Display Name");

    when(param1.getPropertiesMap()).thenReturn(propertiesMap);
    when(param1.getValueClassName()).thenReturn("String");
    when(param1.getName()).thenReturn("name");
    when(param1.getDescription()).thenReturn("desc");
    when(param1.getDefaultValueExpression()).thenReturn(jrExpression);
    when(jrExpression.getText()).thenReturn("text");

    when(param2.getPropertiesMap()).thenReturn(propertiesMap);
    when(param2.getValueClassName()).thenReturn("Integer");
    when(param2.getName()).thenReturn("name");
    when(param2.getDescription()).thenReturn("desc");
    when(param2.getDefaultValueExpression()).thenReturn(jrExpression);

    ByteArrayOutputStream byteOutputStream = mock(ByteArrayOutputStream.class);
    whenNew(ByteArrayOutputStream.class).withAnyArguments().thenReturn(byteOutputStream);
    ObjectOutputStream objectOutputStream = spy(new ObjectOutputStream(byteOutputStream));
    whenNew(ObjectOutputStream.class).withArguments(byteOutputStream).thenReturn(objectOutputStream);
    doNothing().when(objectOutputStream).writeObject(report);
    byte[] byteData = new byte[1];
    when(byteOutputStream.toByteArray()).thenReturn(byteData);
    Template template = new Template();
    Right right = new Right();
    ReportRight reportRight = new ReportRight();

    whenNew(Right.class).withAnyArguments().thenReturn(right);
    whenNew(ReportRight.class).withArguments(template, right).thenReturn(reportRight);

    service.validateFileAndInsertTemplate(template, file);

    verify(repository).insertWithParameters(template);
    verify(rightService).insertRight(right);
    verify(reportRightService).insert(reportRight);
    assertThat(template.getParameters().get(0).getDisplayName(), is("Param Display Name"));
    assertThat(template.getParameters().get(0).getDescription(), is("desc"));
    assertThat(template.getParameters().get(0).getName(), is("name"));
    assertThat(template.getParameters().get(0).getCreatedBy(), is(template.getCreatedBy()));
  }

  @Test
  public void shouldValidateFileAndSetDataIfDefaultValueExpressionIsNull() throws Exception {
    MultipartFile file = mock(MultipartFile.class);
    when(file.getOriginalFilename()).thenReturn("file.jrxml");

    mockStatic(JasperCompileManager.class);
    JasperReport report = mock(JasperReport.class);
    InputStream inputStream = mock(InputStream.class);
    when(file.getInputStream()).thenReturn(inputStream);

    JRParameter param1 = mock(JRParameter.class);
    JRParameter param2 = mock(JRParameter.class);
    JRPropertiesMap propertiesMap = mock(JRPropertiesMap.class);
    JRExpression jrExpression = mock(JRExpression.class);
    String[] propertyNames = {"displayName"};

    when(report.getParameters()).thenReturn(new JRParameter[]{param1, param2});
    when(JasperCompileManager.compileReport(inputStream)).thenReturn(report);
    when(propertiesMap.getPropertyNames()).thenReturn(propertyNames);
    when(propertiesMap.getProperty("displayName")).thenReturn("Param Display Name");

    when(param1.getPropertiesMap()).thenReturn(propertiesMap);
    when(param1.getValueClassName()).thenReturn("String");
    when(param1.getDefaultValueExpression()).thenReturn(jrExpression);
    when(jrExpression.getText()).thenReturn("text");

    when(param2.getPropertiesMap()).thenReturn(propertiesMap);
    when(param2.getValueClassName()).thenReturn("Integer");
    when(param2.getDefaultValueExpression()).thenReturn(null);

    ByteArrayOutputStream byteOutputStream = mock(ByteArrayOutputStream.class);
    whenNew(ByteArrayOutputStream.class).withAnyArguments().thenReturn(byteOutputStream);
    ObjectOutputStream objectOutputStream = spy(new ObjectOutputStream(byteOutputStream));
    whenNew(ObjectOutputStream.class).withArguments(byteOutputStream).thenReturn(objectOutputStream);
    doNothing().when(objectOutputStream).writeObject(report);
    byte[] byteData = new byte[1];
    when(byteOutputStream.toByteArray()).thenReturn(byteData);
    Template template = new Template();
    Right right = new Right();
    ReportRight reportRight = new ReportRight();

    whenNew(Right.class).withAnyArguments().thenReturn(right);
    whenNew(ReportRight.class).withArguments(template, right).thenReturn(reportRight);

    service.validateFileAndInsertTemplate(template, file);

    verify(repository).insertWithParameters(template);
    verify(rightService).insertRight(right);
    verify(reportRightService).insert(reportRight);
    assertThat(template.getParameters().get(0).getDisplayName(), is("Param Display Name"));
    assertThat(template.getParameters().get(0).getCreatedBy(), is(template.getCreatedBy()));
  }

  @Test
  public void shouldSetParametersMapWithValueOfTemplateParameters() throws Exception {
    Template template = new Template();

    TemplateParameter parameter1 = new TemplateParameter();
    parameter1.setName("param1");
    parameter1.setDataType("java.lang.Integer");

    TemplateParameter parameter2 = new TemplateParameter();
    parameter2.setName("param2");
    parameter2.setDataType("java.lang.Float");

    template.setParameters(asList(parameter1, parameter2));

    HttpServletRequest request = mock(HttpServletRequest.class);
    Map requestParamMap = new HashMap<>();
    requestParamMap.put("param1", "2");
    requestParamMap.put("param2", "23.2");

    when(request.getParameterMap()).thenReturn(requestParamMap);
    when(request.getParameter("param1")).thenReturn("2");
    when(request.getParameter("param2")).thenReturn("23.2");

    Map<String,Object> parametersMap = service.getParametersMap(template, 1, request, "pdf");

    assertThat(parametersMap.size(), is(3));
    assertThat(parametersMap.get("param1"), is((Object) 2));
    assertThat(parametersMap.get("param2"), is((Object) 23.2f));
    assertThat(parametersMap.get("format"), is((Object) "pdf"));
  }

  @Test
  public void shouldNotSetParametersMapIfParamValueIsNullOrUndefinedOrBlank() throws Exception {
    Template template = new Template();

    TemplateParameter parameter1 = new TemplateParameter();
    parameter1.setName("param1");
    parameter1.setDataType("java.lang.Integer");

    TemplateParameter parameter2 = new TemplateParameter();
    parameter2.setName("param2");
    parameter2.setDataType("java.lang.Float");

    TemplateParameter parameter3 = new TemplateParameter();
    parameter3.setName("param3");
    parameter3.setDataType("java.lang.Float");

    template.setParameters(asList(parameter1, parameter2, parameter3));

    HttpServletRequest request = mock(HttpServletRequest.class);
    Map requestParamMap = new HashMap<>();
    requestParamMap.put("param1", "null");
    requestParamMap.put("param2", "undefined");
    requestParamMap.put("param3", "");

    when(request.getParameterMap()).thenReturn(requestParamMap);
    when(request.getParameter("param1")).thenReturn("null");
    when(request.getParameter("param2")).thenReturn("undefined");
    when(request.getParameter("param3")).thenReturn("");

    Map<String,Object> parametersMap = service.getParametersMap(template, 1, request, "pdf");

    assertThat(parametersMap.size(), is(1));
    assertThat(parametersMap.get("format"), is((Object) "pdf"));
  }

  @Test
  public void shouldSetOnlyFormatInMapIfTemplateParametersNotPresent() throws Exception {
    Template template = new Template();
    HttpServletRequest request = mock(HttpServletRequest.class);

    Map<String,Object> parametersMap = service.getParametersMap(template, 1, request, "pdf");

    assertThat(parametersMap.size(), is(1));
    assertThat((String) parametersMap.get("format"), is("pdf"));
  }
}
