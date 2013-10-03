/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.reporting.model;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openlmis.core.exception.DataException;
import org.openlmis.db.categories.UnitTests;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ReportTemplate.class, JasperCompileManager.class})
@Category(UnitTests.class)
public class ReportTemplateTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldThrowErrorIfFileNotOfTypeJasperXML() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage("report.template.error.file.type");
    new ReportTemplate("report", new MockMultipartFile("report.pdf", new byte[1]), 1L);
  }

  @Test
  public void shouldThrowErrorIfFileEmpty() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage("report.template.error.file.empty");
    MockMultipartFile file = new MockMultipartFile("report.jrxml", "report.jrxml", "", new byte[0]);

    new ReportTemplate("report", file, 1L);
  }

  @Test
  public void shouldThrowErrorIfFileNotPresent() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage("report.template.error.file.missing");
    new ReportTemplate("report", null, 1L);
  }

  @Test
  public void shouldThrowErrorIfFileIsInvalid() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage("report.template.error.file.invalid");
    new ReportTemplate("report", new MockMultipartFile("report.jrxml", "report.jrxml", "", new byte[1]), 1L);
  }

  @Test
  public void shouldValidateFileAndSetData() throws Exception {
    MultipartFile file = mock(MultipartFile.class);
    when(file.getOriginalFilename()).thenReturn("file.jrxml");

    mockStatic(JasperCompileManager.class);
    JasperReport report = mock(JasperReport.class);
    InputStream inputStream = mock(InputStream.class);
    when(file.getInputStream()).thenReturn(inputStream);
    JRParameter param1 = mock(JRParameter.class);
    JRParameter param2 = mock(JRParameter.class);
    when(report.getParameters()).thenReturn(new JRParameter[] {param1, param2});
    when(JasperCompileManager.compileReport(inputStream)).thenReturn(report);

    ByteArrayOutputStream byteOutputStream = mock(ByteArrayOutputStream.class);
    whenNew(ByteArrayOutputStream.class).withAnyArguments().thenReturn(byteOutputStream);
    ObjectOutputStream objectOutputStream = spy(new ObjectOutputStream(byteOutputStream));
    whenNew(ObjectOutputStream.class).withArguments(byteOutputStream).thenReturn(objectOutputStream);
    doNothing().when(objectOutputStream).writeObject(report);
    byte[] byteData = new byte[1];
    when(byteOutputStream.toByteArray()).thenReturn(byteData);

    ReportTemplate reportTemplate = new ReportTemplate("report", file, 1L);

    assertThat(reportTemplate.getData(), is(byteData));
    assertThat(reportTemplate.getParameters().size(), is(2));
  }
}
