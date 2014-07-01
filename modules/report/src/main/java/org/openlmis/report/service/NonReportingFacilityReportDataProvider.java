/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.ProcessingPeriod;
import org.openlmis.core.repository.GeographicZoneRepository;
import org.openlmis.report.mapper.NonReportingFacilityReportMapper;
import org.openlmis.report.mapper.lookup.RequisitionGroupReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.dto.NameCount;
import org.openlmis.report.model.report.MasterReport;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class NonReportingFacilityReportDataProvider extends ReportDataProvider {

  @Autowired
  private NonReportingFacilityReportMapper reportMapper;

  @Autowired
  private GeographicZoneRepository geographicZoneMapper;

  @Override
  protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
    RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    return reportMapper.getReport(filterCriteria, rowBounds, this.getUserId());
  }

  @Override
  public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);

    List<MasterReport> reportList = new ArrayList<MasterReport>();
    MasterReport report = new MasterReport();
    report.details = reportMapper.getReport(filterCriteria, rowBounds, this.getUserId());
    List<NameCount> summary = reportMapper.getReportSummary(filterCriteria, this.getUserId());

    // TODO: move this to other section of the application
    NameCount percentage = new NameCount();
    percentage.setName("Percentage not-reporting");

    String totalFacilities = reportMapper.getTotalFacilities(filterCriteria, this.getUserId()).get(0).toString();
    String nonReporting = reportMapper.getNonReportingTotalFacilities(filterCriteria, this.getUserId()).get(0).toString();

    // Assume by default that the 100% of facilities didn't report
    Long percent = Long.parseLong("100");
    if (totalFacilities != "0") {
      percent = Math.round((Double.parseDouble(nonReporting) / Double.parseDouble(totalFacilities)) * 100);
    }

    percentage.setCount(percent.toString() + "%");
    summary.add(0, percentage);

    report.summary = summary;

    reportList.add(report);

    List<? extends ReportData> list;
    list = reportList;
    return list;
  }

  @Override
  public HashMap<String, String> getAdditionalReportData(Map params) {
    HashMap<String, String> result = new HashMap<String, String>();

    // spit out the summary section on the report.
    String totalFacilities = reportMapper.getTotalFacilities(params, this.getUserId()).get(0).toString();
    String nonReporting = reportMapper.getNonReportingTotalFacilities(params, this.getUserId()).get(0).toString();
    result.put("TOTAL_FACILITIES", totalFacilities);
    result.put("TOTAL_NON_REPORTING", nonReporting);

    // Assume by default that the 100% of facilities didn't report
    Long percent = Long.parseLong("100");
    if (totalFacilities != "0") {
      percent = Math.round((Double.parseDouble(nonReporting) / Double.parseDouble(totalFacilities)) * 100);
    }
    result.put("PERCENTAGE_NON_REPORTING", percent.toString());

    // Interprate the different reporting parameters that were selected on the UI
    String period = (StringHelper.isBlank(params, "period"))?"" :((String[]) params.get("period"))[0];
    String zone = (StringHelper.isBlank(params, "zone"))?"" : ((String[]) params.get("zone"))[0];
    String facilityType = (StringHelper.isBlank(params, "facilityType"))?"" :((String[]) params.get("facilityType"))[0];
    String program = (StringHelper.isBlank(params, "program"))? "" :((String[]) params.get("program"))[0];

    // compose the filter text as would be presented on the pdf reports.
    String header = "";
    if (program != "" && !program.endsWith("undefined")) {
      header += "Program: " + this.reportMapper.getProgram(Integer.parseInt(program)).get(0).getName();
    }
    if (zone != "" && !zone.endsWith("undefined")) {
      header += "\nGeographic Zone: " + this.geographicZoneMapper.getById(Integer.parseInt(zone)).getName();
    }

    if (facilityType != "" && !facilityType.endsWith("undefined")) {
      header += "\nFacility Type : " + this.reportMapper.getFacilityType(Integer.parseInt(facilityType)).get(0).getName();
    } else {
      header += "\nFacility Type : All Facility Types";
    }

    ProcessingPeriod periodObject = this.reportMapper.getPeriodId(Integer.parseInt(period));

    if (period != "" && !period.endsWith("undefined")) {
      header += "\nPeriod : " + periodObject.getName() + " - " + periodObject.getStringYear();
    }

    result.put("REPORT_FILTER_PARAM_VALUES", header);
    return result;
  }


}
