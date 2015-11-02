/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.controller;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.openlmis.core.web.OpenLmisResponse;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.report.model.CustomReport;
import org.openlmis.report.repository.CustomReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@AllArgsConstructor
@NoArgsConstructor
@Controller
@RequestMapping(value="/report-api")
public class CustomReportController extends BaseController {

  public static final String DURATION = "duration";
  public static final String DATE = "date";
  public static final String VALUES = "values";
  public static final String REPORTS = "reports";
  public static final String QUERY_MODEL = "queryModel";
  public static final String REPORT = "report";
  @Autowired
  CustomReportRepository reportRepository;

  @RequestMapping(value = "list")
  public ResponseEntity<OpenLmisResponse> getListOfReports(){
    return OpenLmisResponse.response(REPORTS, reportRepository.getReportList());
  }

  @RequestMapping(value = "full-list")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_CUSTOM_REPORTS')")
  public ResponseEntity<OpenLmisResponse> getFullListOfReports(){
    return OpenLmisResponse.response(REPORTS, reportRepository.getReportListWithFullAttributes());
  }

  @RequestMapping(value = "report")
  public ResponseEntity<OpenLmisResponse> getReportData( @RequestParam Map filter ){
    long requestTime = new Date().getTime();
    ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response(VALUES, reportRepository.getReportData(filter));
    response.getBody().addData(DATE, new Date());
    long responseTime = new Date().getTime();
    long duration = responseTime - requestTime;
    response.getBody().addData(DURATION, duration);
    return response;
  }

  @RequestMapping(value="report.csv" , method = GET)
  public ModelAndView getCsvReport( @RequestParam Map filter){
    List<Map> report = reportRepository.getReportData(filter);
    Map queryModel = reportRepository.getQueryModelByKey(filter.get("report_key").toString());
    ModelAndView view = new ModelAndView("customCsvTemplate");
    view.addObject(REPORT, report);
    view.addObject(QUERY_MODEL, queryModel);
    return view;
  }


  @RequestMapping(value = "save", method = RequestMethod.POST)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_CUSTOM_REPORTS')")
  public ResponseEntity<OpenLmisResponse> saveCustomReport( @RequestBody CustomReport report){
    if(report.getId() != null){
      reportRepository.update(report);
    }else{
      reportRepository.insert(report);
    }
    return OpenLmisResponse.response(REPORT, report);
  }

}
