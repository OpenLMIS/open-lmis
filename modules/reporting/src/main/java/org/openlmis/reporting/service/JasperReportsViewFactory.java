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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperReport;
import org.openlmis.reporting.model.Template;
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

/**
 * Exposes the services for generating jasper report multi format view using a data source to fetch data.
 */

@Service
public class JasperReportsViewFactory {

  @Autowired
  DataSource replicationDataSource;

  public JasperReportsMultiFormatView getJasperReportsView(Template template)
    throws IOException, ClassNotFoundException, JRException {
    JasperReportsMultiFormatView jasperView = new JasperReportsMultiFormatView();

    setExportParams(jasperView);

    setDataSourceAndURLAndApplicationContext(template, jasperView);

    return jasperView;
  }

  private void setExportParams(JasperReportsMultiFormatView jasperView) {
    Map<JRExporterParameter, Object> reportFormatMap = new HashMap<>();
    reportFormatMap.put(IS_USING_IMAGES_TO_ALIGN, false);
    jasperView.setExporterParameters(reportFormatMap);
  }

  private void setDataSourceAndURLAndApplicationContext(Template template,
                                                        JasperReportsMultiFormatView jasperView)
    throws IOException, ClassNotFoundException, JRException {
    WebApplicationContext ctx = getCurrentWebApplicationContext();

    jasperView.setJdbcDataSource(replicationDataSource);
    jasperView.setUrl(getReportURLForReportData(template));

    if (ctx != null)
      jasperView.setApplicationContext(ctx);
  }

  public String getReportURLForReportData(Template template)
    throws IOException, ClassNotFoundException, JRException {

    File tmpFile = createTempFile(template.getName() + "_temp", ".jasper");
    ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(template.getData()));
    JasperReport jasperReport = (JasperReport) inputStream.readObject();
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(bos);
    out.writeObject(jasperReport);
    writeByteArrayToFile(tmpFile, bos.toByteArray());
    return tmpFile.toURI().toURL().toString();
  }
}