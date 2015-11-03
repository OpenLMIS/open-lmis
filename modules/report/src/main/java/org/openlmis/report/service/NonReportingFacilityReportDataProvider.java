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

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.NonReportingFacilityReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.dto.NameCount;
import org.openlmis.report.model.params.NonReportingFacilityParam;
import org.openlmis.report.model.report.MasterReport;
import org.openlmis.report.util.ParameterAdaptor;
import org.openlmis.report.util.SelectedFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class NonReportingFacilityReportDataProvider extends ReportDataProvider {

  public static final String PERCENTAGE_NON_REPORTING = "PERCENTAGE_NON_REPORTING";
  public static final String REPORT_FILTER_PARAM_VALUES = "REPORT_FILTER_PARAM_VALUES";
  public static final String TOTAL_NON_REPORTING = "TOTAL_NON_REPORTING";
  public static final String TOTAL_FACILITIES = "TOTAL_FACILITIES";

  @Autowired
  private NonReportingFacilityReportMapper reportMapper;

  @Autowired
  private SelectedFilterHelper filterHelper;

  @Override
  protected List<? extends ReportData> getResultSet(Map<String, String[]> filterCriteria) {
    RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    return reportMapper.getReport(getFilterParameters(filterCriteria), rowBounds, this.getUserId());
  }

  @Override
  public List<? extends ReportData> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
    NonReportingFacilityParam nonReportingFacilityParam = getFilterParameters(filterCriteria);
    List<MasterReport> reportList = new ArrayList<>();
    MasterReport report = new MasterReport();
    report.setDetails(reportMapper.getReport(nonReportingFacilityParam, rowBounds, this.getUserId()));
    List<NameCount> summary = reportMapper.getReportSummary(nonReportingFacilityParam, this.getUserId());

    Double totalFacilities = reportMapper.getTotalFacilities(nonReportingFacilityParam, this.getUserId());
    Double nonReporting = reportMapper.getNonReportingTotalFacilities(nonReportingFacilityParam, this.getUserId());

    // Assume by default that the 100% of facilities didn't report
    Long percentNonReporting = 100L;
    Long percentReporting = 100L;
    if (totalFacilities != 0) {
      percentNonReporting = Math.round((nonReporting / totalFacilities) * 100);
      percentReporting = 100 - percentNonReporting;
    }

    NameCount percentageNonReporting = new NameCount();
    NameCount percentageReporting = new NameCount();

    percentageNonReporting.setName("Percentage not-reporting");
    percentageNonReporting.setCount(percentNonReporting.toString() + "%");
    summary.add(1, percentageNonReporting);

    percentageReporting.setName("Percentage reporting");
    percentageReporting.setCount(percentReporting.toString() + "%");
    summary.add(percentageReporting);

    NameCount percentageNonReportingChart = new NameCount();
    NameCount percentageReportingChart = new NameCount();

    percentageNonReportingChart.setName("Percentage not-reporting");
    percentageNonReportingChart.setCount(percentNonReporting.toString());
    summary.add(percentageNonReportingChart);

    percentageReportingChart.setName("Percentage reporting");
    percentageReportingChart.setCount(percentReporting.toString());
    summary.add(percentageReportingChart);

    report.setSummary(summary);
    reportList.add(report);

    List<? extends ReportData> list;
    list = reportList;
    return list;
  }

  private NonReportingFacilityParam getFilterParameters(Map params) {
    return ParameterAdaptor.parse(params, NonReportingFacilityParam.class);
  }

  @Override
  public HashMap<String, String> getExtendedHeader(Map params) {
    NonReportingFacilityParam nonReportingFacilityParam = getFilterParameters(params);

    HashMap<String, String> result = new HashMap<String, String>();

    Double totalFacilities = reportMapper.getTotalFacilities(nonReportingFacilityParam, this.getUserId());
    Double nonReporting = reportMapper.getNonReportingTotalFacilities(nonReportingFacilityParam, this.getUserId());

    result.put(TOTAL_FACILITIES, totalFacilities.toString());
    result.put(TOTAL_NON_REPORTING, nonReporting.toString());

    Long percent = 100L;
    if (totalFacilities != 0) {
      percent = Math.round((nonReporting / totalFacilities) * 100);
    }
    result.put(PERCENTAGE_NON_REPORTING, percent.toString());

    result.put(REPORT_FILTER_PARAM_VALUES, filterHelper.getProgramPeriodGeoZone(params));
    return result;
  }


}
