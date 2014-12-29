/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.reporting.controller;

import org.openlmis.reporting.model.Template;
import org.openlmis.reporting.service.JasperReportsViewFactory;
import org.openlmis.reporting.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * ReportController defines the endpoints to be used by client for multi format reports related functionality.
 */

@Controller
public class ReportController {

  public static final String USER_ID = "USER_ID";

  @Autowired
  private JasperReportsViewFactory jasperReportsViewFactory;

  @Autowired
  private TemplateService templateService;

  private Long loggedInUserId(HttpServletRequest request) {
    return (Long) request.getSession().getAttribute(USER_ID);
  }

  @RequestMapping(method = GET, value = "/reports/{id}")
  public Template getReportWithParameters(@PathVariable("id") Long id) {
    return templateService.getLWById(id);
  }

  @RequestMapping(method = GET, value = "/reports/{id}/{format}")
  public ModelAndView generateReport(HttpServletRequest request, @PathVariable("id") Long id,
                                     @PathVariable("format") String format) {
    JasperReportsMultiFormatView jasperView;
    Map<String, Object> map;
    try {
      int userId = loggedInUserId(request).intValue();
      Template template = templateService.getById(id);
      jasperView = jasperReportsViewFactory.getJasperReportsView(template);
      map = templateService.getParametersMap(template, userId, request, format);
    } catch (Exception e) {
      return new ModelAndView("error-page");
    }
    return new ModelAndView(jasperView, map);
  }

}
