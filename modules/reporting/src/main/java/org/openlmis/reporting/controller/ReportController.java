/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.reporting.controller;

import org.openlmis.core.domain.Report;
import org.openlmis.core.repository.mapper.ReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.AbstractJasperReportsSingleFormatView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ReportController {

  @Autowired
  private JasperReportsViewFactory jasperReportsViewFactory;

  @Autowired
  private ReportMapper reportMapper;

  @RequestMapping(method = RequestMethod.GET, value = "/report/{id}/{format}")
  public ModelAndView generatePdfReport(HttpServletRequest request, @PathVariable("id") Integer id
    , @PathVariable("format") String format) throws Exception {

    Report dbReport = reportMapper.getById(id);

    AbstractJasperReportsSingleFormatView jasperView = jasperReportsViewFactory.getJasperReportsView(dbReport, format);

    return new ModelAndView(jasperView, request.getParameterMap());
  }

}