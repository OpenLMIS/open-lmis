/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.reporting.controller;

import org.apache.commons.io.FileUtils;
import org.openlmis.core.domain.Report;
import org.openlmis.core.repository.mapper.ReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.AbstractJasperReportsSingleFormatView;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ReportController {

  @Autowired
  JasperReportsViewFactory jasperReportsViewFactory;

  @Autowired
  ReportMapper reportMapper;

  @RequestMapping(method = RequestMethod.GET, value = "/report/{id}/{format}")
  public ModelAndView generatePdfReport(@PathVariable("id") Integer id
    , @PathVariable("format") String format) throws Exception {

    Report dbReport = reportMapper.getById(id);

    // TODO : Better way to do this?
    String reportURL = getReportURLForReportData(dbReport.getData());

    AbstractJasperReportsSingleFormatView jasperView = jasperReportsViewFactory.getJasperReportsView(
      reportURL, format);

    // add parameters used by the report
    Map<String, Object> parameterMap = new HashMap<String, Object>();
//    parameterMap.putAll(params);

    return new ModelAndView(jasperView, parameterMap);
  }

  private String getReportURLForReportData(byte[] reportData) throws IOException {
    File tmpFile = File.createTempFile("report", ".jrxml");
    FileUtils.writeByteArrayToFile(tmpFile, reportData);
    return tmpFile.toURI().toURL().toString();
  }

}