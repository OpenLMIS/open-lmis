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

import lombok.NoArgsConstructor;
import org.openlmis.core.web.controller.BaseController;
import org.openlmis.report.ReportManager;
import org.openlmis.report.ReportOutputOption;
import org.openlmis.report.service.lookup.ReportLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@NoArgsConstructor
@RequestMapping(value = "/reports")
public class ReportExportController extends BaseController {


    private static final String USER_ID = "USER_ID";
    private static final String FACILITY_MAILING_LIST = "facility_mailing_list";

    private static final String PDF = "PDF";
    private static final String XLS = "XLS";
    private static final String HTML = "HTML";
    private static final String CSV = "CSV";

  @Autowired
  public ReportManager reportManager;

  @Autowired
  public ReportLookupService reportService;


  @RequestMapping(value = "/download/{reportKey}/{outputOption}")
  public void showReport(
    @PathVariable(value = "reportKey") String reportKey
    , @PathVariable(value = "outputOption") String outputOption
    , HttpServletRequest request
    , HttpServletResponse response
  ) {
    Integer userId = Integer.parseInt(request.getSession().getAttribute(USER_ID).toString());

    switch (outputOption.toUpperCase()) {
        case PDF:
        reportManager.showReport(userId, reportKey, request.getParameterMap(), ReportOutputOption.PDF, response);
        break;
        case XLS:
        reportManager.showReport(userId, reportKey, request.getParameterMap(), ReportOutputOption.XLS, response);
        break;
        case HTML:
        reportManager.showReport(userId, reportKey, request.getParameterMap(), ReportOutputOption.HTML, response);
        break;
        case CSV:
        reportManager.showReport(userId, reportKey, request.getParameterMap(), ReportOutputOption.CSV, response);
        break;
        default:
    }
  }


   @RequestMapping(value = "/download/unscheduled_reporting/list/{outputOption}")
    public void showUnscheduledReportingReport(
            @PathVariable(value = "outputOption") String outputOption
            , HttpServletRequest request
            , HttpServletResponse response
    ) {
        showReport("unscheduled_reporting", outputOption, request, response);
    }

    @RequestMapping(value = "/download/equipment_replacement_list/list/{outputOption}")
    public void showReplacementEquipmentList(
            @PathVariable(value = "outputOption") String outputOption
            , HttpServletRequest request
            , HttpServletResponse response
    ) {
        showReport("equipment_replacement_list", outputOption, request, response);
    }

}