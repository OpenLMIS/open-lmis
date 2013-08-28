/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.reporting.controller;

import net.sf.jasperreports.engine.JasperReport;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.reporting.model.ReportTemplate;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JasperReportsViewFactory.class})
@Category(UnitTests.class)
public class JasperReportsViewFactoryTest {

  @Mock
  private DataSource dataSource;

  @InjectMocks
  private JasperReportsViewFactory viewFactory;

  private ReportTemplate reportTemplate;

  private JasperReportsMultiFormatView jasperReportsView;

  JasperReport jasperReport;

  ObjectInputStream objectInputStream;
  ObjectOutputStream objectOutputStream;
  ByteArrayOutputStream byteArrayOutputStream;
  byte[] reportByteData;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    reportTemplate = mock(ReportTemplate.class);
    when(reportTemplate.getName()).thenReturn("report1.jrxml");
    reportByteData = new byte[1];
    when(reportTemplate.getData()).thenReturn(reportByteData);
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
    JasperReportsMultiFormatView reportView = viewFactory.getJasperReportsView(reportTemplate);

    assertThat(reportView, is(jasperReportsView));
    verify(jasperReportsView).setJdbcDataSource(dataSource);
    verify(objectOutputStream).writeObject(jasperReport);

  }
}
