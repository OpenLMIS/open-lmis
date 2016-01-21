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

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperReport;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.reporting.model.Template;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import static net.sf.jasperreports.engine.export.JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.*;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
@PrepareForTest({JasperReportsViewFactory.class})
public class JasperReportsViewFactoryTest {

  @Mock
  private DataSource dataSource;

  @InjectMocks
  private JasperReportsViewFactory viewFactory;

  private Template template;

  private JasperReportsMultiFormatView jasperReportsView;

  JasperReport jasperReport;

  ObjectInputStream objectInputStream;
  ObjectOutputStream objectOutputStream;
  ByteArrayOutputStream byteArrayOutputStream;
  byte[] reportByteData;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    template = mock(Template.class);
    when(template.getName()).thenReturn("report1.jrxml");
    reportByteData = new byte[1];
    when(template.getData()).thenReturn(reportByteData);
    jasperReport = mock(JasperReport.class);

    objectInputStream = mock(ObjectInputStream.class);
    objectOutputStream = mock(ObjectOutputStream.class);
    byteArrayOutputStream = mock(ByteArrayOutputStream.class);

    ByteArrayInputStream byteArrayInputStream = mock(ByteArrayInputStream.class);
    whenNew(ByteArrayInputStream.class).withArguments(reportByteData).thenReturn(byteArrayInputStream);
    whenNew(ObjectInputStream.class).withArguments(byteArrayInputStream).thenReturn(objectInputStream);
    whenNew(ByteArrayOutputStream.class).withNoArguments().thenReturn(byteArrayOutputStream);
    whenNew(ObjectOutputStream.class).withArguments(byteArrayOutputStream).thenReturn(objectOutputStream);
    jasperReportsView = spy(new JasperReportsMultiFormatView());
  }

  @Test
  public void shouldGetRequestedViewAndSetDataSourceAndWebContextInJasperView() throws Exception {
    whenNew(JasperReportsMultiFormatView.class).withNoArguments().thenReturn(jasperReportsView);
    when(objectInputStream.readObject()).thenReturn(jasperReport);
    when(byteArrayOutputStream.toByteArray()).thenReturn(reportByteData);
    Map<String, Object> parameterMap = new HashMap();
    parameterMap.put("createdBy", 1l);
    JasperReportsMultiFormatView reportView = viewFactory.getJasperReportsView(template);

    assertThat(reportView, is(jasperReportsView));
    verify(jasperReportsView).setJdbcDataSource(dataSource);
    verify(objectOutputStream).writeObject(jasperReport);

  }

  @Test
  public void shouldAddExportParamToGetRidOfImageInHtmlReport() throws Exception {

    whenNew(JasperReportsMultiFormatView.class).withNoArguments().thenReturn(jasperReportsView);
    when(objectInputStream.readObject()).thenReturn(jasperReport);
    when(byteArrayOutputStream.toByteArray()).thenReturn(reportByteData);

    Map<JRExporterParameter, Object> exportParams = new HashMap<>();
    exportParams.put(IS_USING_IMAGES_TO_ALIGN, false);

    viewFactory.getJasperReportsView(template);


    verify(jasperReportsView).setExporterParameters(exportParams);
  }
}
