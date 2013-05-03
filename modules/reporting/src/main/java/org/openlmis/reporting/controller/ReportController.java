/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.reporting.controller;

import org.openlmis.core.domain.ReportTemplate;
import org.openlmis.core.repository.mapper.ReportTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ReportController {
  public static final String PDF_VIEW = "pdf";

  @Autowired
  private JasperReportsViewFactory jasperReportsViewFactory;

  @Autowired
  private ReportTemplateMapper reportTemplateMapper;

  @RequestMapping(method = RequestMethod.GET, value = "/reports/{id}/{format}")
  public ModelAndView generateReport(HttpServletRequest request, @PathVariable("id") Integer id
      , @PathVariable("format") String format) throws Exception {

    String viewFormat = format == null ? PDF_VIEW : format;

    ReportTemplate reportTemplate = reportTemplateMapper.getById(id);

    JasperReportsMultiFormatView jasperView = jasperReportsViewFactory.getJasperReportsView(reportTemplate);

    Map map = new HashMap();
    map.put("format", viewFormat);

    return new ModelAndView(jasperView, map);
  }

}