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
public class ReportExportController {


  public static final String USER_ID = "USER_ID";
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
    //TODO: change the methods to have a long user id parameter instead of integer
    Integer userId = Integer.parseInt(request.getSession().getAttribute(USER_ID).toString());

    switch (outputOption.toUpperCase()) {
      case "PDF":
        reportManager.showReport(userId, reportKey, request.getParameterMap(), ReportOutputOption.PDF, response);
        break;
      case "XLS":
        reportManager.showReport(userId, reportKey, request.getParameterMap(), ReportOutputOption.XLS, response);
        break;
      case "HTML":
        reportManager.showReport(userId, reportKey, request.getParameterMap(), ReportOutputOption.HTML, response);
        break;
      case "CSV":
        reportManager.showReport(userId, reportKey, request.getParameterMap(), ReportOutputOption.CSV, response);
        break;
    }
  }

    @RequestMapping(value = "/exportfile/{reportKey}/{outputOption}")
    public void exportReportBytesStream(
            @PathVariable(value = "reportKey") String reportKey
            , @PathVariable(value = "outputOption") String outputOption
            , HttpServletRequest request
    ) {
        //TODO: change the methods to have a long user id parameter instead of integer
        Integer userId = Integer.parseInt(request.getSession().getAttribute(USER_ID).toString());

        ByteArrayOutputStream byteArrayOutputStream = reportManager.exportReportBytesStream(userId, reportKey, request.getParameterMap(), outputOption);

        OutputStream outStream = null;
        ByteArrayOutputStream byteOutStream = null;
        try {
            outStream = new FileOutputStream("C:\\companies\\doop.pdf");
            // writing bytes in to byte output stream
            byteArrayOutputStream.writeTo(outStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

  @RequestMapping(value = "/download/mailinglabels/list/{outputOption}")
  public void showMailingListReport(
    @PathVariable(value = "outputOption") String outputOption
    , HttpServletRequest request
    , HttpServletResponse response
  ) {
    showReport("facility_mailing_list", outputOption, request, response);
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