/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.reporting.controller;

import org.openlmis.core.domain.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.jasperreports.*;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;

import static java.io.File.createTempFile;
import static org.apache.commons.io.FileUtils.writeByteArrayToFile;
import static org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext;

@Service
public class JasperReportsViewFactory {

  public static final String CSV_VIEW = "csv";
  public static final String HTML_VIEW = "html";
  public static final String EXCEL_VIEW = "xls";
  public static final String PDF_VIEW = "pdf";

  @Autowired
  DataSource replicationDataSource;

  public AbstractJasperReportsSingleFormatView getJasperReportsView(
    Report report, String format) throws IOException {
    String viewFormat = format == null ? PDF_VIEW : format;

    // get jasperView class based on the format supplied
    // defaults to pdf
    AbstractJasperReportsSingleFormatView jasperView;
    if (viewFormat.equals(CSV_VIEW)) {
      jasperView = new JasperReportsCsvView();
    } else if (viewFormat.equals(HTML_VIEW)) {
      jasperView = new JasperReportsHtmlView();
    } else if (viewFormat.equals(EXCEL_VIEW)) {
      jasperView = new JasperReportsXlsView();
    } else {
      jasperView = new JasperReportsPdfView();
    }

    // get appContext. required by the view
    WebApplicationContext ctx = getCurrentWebApplicationContext();

    jasperView.setJdbcDataSource(replicationDataSource);
    jasperView.setUrl(getReportURLForReportData(report.getData()));
    if (ctx != null)
      jasperView.setApplicationContext(ctx);

    return jasperView;
  }

  private String getReportURLForReportData(byte[] reportData) throws IOException {
    File tmpFile = createTempFile("report", ".jrxml");
    writeByteArrayToFile(tmpFile, reportData);
    return tmpFile.toURI().toURL().toString();
  }
}