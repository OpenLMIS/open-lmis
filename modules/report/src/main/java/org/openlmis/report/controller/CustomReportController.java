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
import org.openlmis.report.model.CustomReport;
import org.openlmis.report.repository.CustomReportRepository;
import org.openlmis.report.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Controller
@RequestMapping(value="/report-api")
public class CustomReportController extends BaseController{

  @Autowired
  CustomReportRepository reportRepository;

  @RequestMapping(value = "list")
  public ResponseEntity<OpenLmisResponse> getListOfReports(){
    return OpenLmisResponse.response("reports", reportRepository.getReportList());
  }

  @RequestMapping(value = "full-list")
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_CUSTOM_REPORTS')")
  public ResponseEntity<OpenLmisResponse> getFullListOfReports(){
    return OpenLmisResponse.response("reports", reportRepository.getReportListWithFullAttributes());
  }

  @RequestMapping(value = "report")
  public ResponseEntity<OpenLmisResponse> getReportData( @RequestParam Map filter ){
    long requestTime = new Date().getTime();
    ResponseEntity<OpenLmisResponse> response = OpenLmisResponse.response("values", reportRepository.getReportData(filter));
    response.getBody().addData("date", new Date());
    long responseTime = new Date().getTime();
    //return the milliseconds it took to run this query.
    //TODO: log this time.
    long duration = responseTime - requestTime;
    response.getBody().addData("duration", duration);
    return response;
  }

  @RequestMapping(value = "save", method = RequestMethod.POST)
  @PreAuthorize("@permissionEvaluator.hasPermission(principal,'MANAGE_CUSTOM_REPORTS')")
  public ResponseEntity<OpenLmisResponse> saveCustomReport( @RequestBody CustomReport report){
    if(report.getId() != null){
      reportRepository.update(report);
    }else{
      reportRepository.insert(report);
    }

    return OpenLmisResponse.response("report", report);
  }

}
