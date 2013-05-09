/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.reporting.model;

import javassist.expr.NewArray;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openlmis.core.exception.DataException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openlmis.reporting.model.ReportTemplate.CREATE_REPORT_ERROR_FILE_EMPTY;
import static org.openlmis.reporting.model.ReportTemplate.CREATE_REPORT_ERROR_FILE_TYPE;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.whenNew;
@RunWith(PowerMockRunner.class)
@PrepareForTest({ReportTemplate.class, JasperCompileManager.class})
public class ReportTemplateTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldThrowErrorIfFileNotOfTypeJasperXML() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage(CREATE_REPORT_ERROR_FILE_TYPE);
    new ReportTemplate("report", new MockMultipartFile("report.pdf", new byte[1]), 1L);
  }

  @Test
  public void shouldThrowErrorIfFileEmpty() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage(CREATE_REPORT_ERROR_FILE_EMPTY);
    MockMultipartFile file = new MockMultipartFile("report.jrxml", "report.jrxml", "", new byte[0]);

    new ReportTemplate("report", file, 1L);
  }

  @Test
  public void shouldThrowErrorIfFileNotPresent() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage(ReportTemplate.CREATE_REPORT_ERROR_FILE_MISSING);
    new ReportTemplate("report", null, 1L);
  }

  @Test
  public void shouldThrowErrorIfFileIsInvalid() throws Exception {
    expectedException.expect(DataException.class);
    expectedException.expectMessage(ReportTemplate.CREATE_REPORT_ERROR_FILE_INVALID);
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
    when(JasperCompileManager.compileReport(inputStream)).thenReturn(report);

    ByteArrayOutputStream byteOutputStream = mock(ByteArrayOutputStream.class);
    whenNew(ByteArrayOutputStream.class).withAnyArguments().thenReturn(byteOutputStream);
    ObjectOutputStream objectOutputStream = spy(new ObjectOutputStream(byteOutputStream));
    whenNew(ObjectOutputStream.class).withArguments(byteOutputStream).thenReturn(objectOutputStream);
    doNothing().when(objectOutputStream).writeObject(report);
    byte[] byteData = new byte[1];
    when(byteOutputStream.toByteArray()).thenReturn(byteData);

    ReportTemplate reportTemplate = new ReportTemplate("report", file, 1L);

   assertThat(reportTemplate.getData(), is(byteData) );

  }
}
