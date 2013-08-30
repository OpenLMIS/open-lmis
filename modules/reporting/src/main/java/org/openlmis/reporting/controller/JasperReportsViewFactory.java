/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.reporting.controller;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperReport;
import org.openlmis.reporting.model.ReportTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import javax.sql.DataSource;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static java.io.File.createTempFile;
import static net.sf.jasperreports.engine.export.JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN;
import static org.apache.commons.io.FileUtils.writeByteArrayToFile;
import static org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext;

@Service
public class JasperReportsViewFactory {

  @Autowired
  DataSource replicationDataSource;

  public JasperReportsMultiFormatView getJasperReportsView(ReportTemplate reportTemplate)
    throws IOException, ClassNotFoundException, JRException {
    JasperReportsMultiFormatView jasperView = new JasperReportsMultiFormatView();

    setExportParams(jasperView);

    setDataSourceAndURLAndApplicationContext(reportTemplate, jasperView);

    return jasperView;
  }

  private void setExportParams(JasperReportsMultiFormatView jasperView) {
    Map<JRExporterParameter, Object> reportFormatMap = new HashMap<>();
    reportFormatMap.put(IS_USING_IMAGES_TO_ALIGN, false);
    jasperView.setExporterParameters(reportFormatMap);
  }

  private void setDataSourceAndURLAndApplicationContext(ReportTemplate reportTemplate,
                                                        JasperReportsMultiFormatView jasperView)
    throws IOException, ClassNotFoundException, JRException {
    WebApplicationContext ctx = getCurrentWebApplicationContext();

    jasperView.setJdbcDataSource(replicationDataSource);
    jasperView.setUrl(getReportURLForReportData(reportTemplate));

    if (ctx != null)
      jasperView.setApplicationContext(ctx);
  }

  public String getReportURLForReportData(ReportTemplate reportTemplate)
    throws IOException, ClassNotFoundException, JRException {

    File tmpFile = createTempFile(reportTemplate.getName() + "_temp", ".jasper");
    ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(reportTemplate.getData()));
    JasperReport jasperReport = (JasperReport) inputStream.readObject();
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(bos);
    out.writeObject(jasperReport);
    writeByteArrayToFile(tmpFile, bos.toByteArray());
    return tmpFile.toURI().toURL().toString();
  }
}