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
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.StockedOutReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.params.StockedOutReportParam;
import org.openlmis.report.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.lang.String;

@Component
@NoArgsConstructor
public class StockedOutReportDataProvider extends ReportDataProvider {

  private StockedOutReportMapper reportMapper;

  private StockedOutReportParam stockedOutReportParam = null;

  @Autowired
  public StockedOutReportDataProvider(StockedOutReportMapper mapper) {
    this.reportMapper = mapper;
  }

  @Override
  protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
    RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    return reportMapper.getReport(getReportFilterData(filterCriteria), null, rowBounds);
  }

  @Override
  public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
    return reportMapper.getReport(getReportFilterData(filterCriteria), SortCriteria, rowBounds);
  }

  public StockedOutReportParam getReportFilterData(Map<String, String[]> filterCriteria) {
    if (filterCriteria != null) {
      stockedOutReportParam = new StockedOutReportParam();

      Date originalStart = new Date();
      Date originalEnd = new Date();

      stockedOutReportParam.setZoneId(StringUtils.isBlank(filterCriteria.get("zoneId")[0]) ? 0 : Integer.parseInt(filterCriteria.get("zoneId")[0]));  //defaults to 0
      stockedOutReportParam.setZone(StringUtils.isBlank(filterCriteria.get("zone")[0]) ? "All Zones" : filterCriteria.get("zone")[0]);

      stockedOutReportParam.setFacilityTypeId(StringUtils.isBlank(filterCriteria.get("facilityTypeId")[0]) ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
      stockedOutReportParam.setFacilityType(StringUtils.isBlank(filterCriteria.get("facilityType")[0]) ? "All Facility Types" : filterCriteria.get("facilityType")[0]);

      stockedOutReportParam.setFacilityId(StringUtils.isBlank(filterCriteria.get("facilityId")[0]) ? 0 : Integer.parseInt(filterCriteria.get("facilityId")[0])); //defaults to 0
      stockedOutReportParam.setFacility(StringUtils.isBlank(filterCriteria.get("facility")[0]) ? "All Facilities" : filterCriteria.get("facility")[0]);

      stockedOutReportParam.setRgroupId(StringUtils.isBlank(filterCriteria.get("rgroupId")[0]) ? 0 : Integer.parseInt(filterCriteria.get("rgroupId")[0])); //defaults to 0
      stockedOutReportParam.setRgroup(StringUtils.isBlank(filterCriteria.get("rgroup")[0]) ? "All Reporting Groups" : filterCriteria.get("rgroup")[0]);

      stockedOutReportParam.setProductCategoryId(StringUtils.isBlank(filterCriteria.get("productCategoryId")[0]) ? 0 : Integer.parseInt(filterCriteria.get("productCategoryId")[0])); //defaults to 0
      stockedOutReportParam.setProductCategory(StringUtils.isBlank(filterCriteria.get("productCategory")[0]) ? "All Product Categories" : filterCriteria.get("productCategory")[0]);

      stockedOutReportParam.setProductId(StringUtils.isBlank(filterCriteria.get("productId")[0]) ? 0 : Integer.parseInt(filterCriteria.get("productId")[0])); //defaults to 0
      stockedOutReportParam.setProduct(StringUtils.isBlank(filterCriteria.get("product")[0]) ? "All Products" : filterCriteria.get("product")[0]);

      stockedOutReportParam.setProgramId(StringUtils.isBlank(filterCriteria.get("programId")[0]) ? 0 : Integer.parseInt(filterCriteria.get("programId")[0])); //defaults to 0
      stockedOutReportParam.setProgram(StringUtils.isBlank(filterCriteria.get("program")[0]) ? "All Programs" : filterCriteria.get("program")[0]);

      //monthly
      stockedOutReportParam.setYearFrom(StringUtils.isBlank(filterCriteria.get("fromYear")[0]) ? originalStart.getYear() : Integer.parseInt(filterCriteria.get("fromYear")[0])); //defaults to 0
      stockedOutReportParam.setYearTo(StringUtils.isBlank(filterCriteria.get("toYear")[0]) ? originalEnd.getYear() : Integer.parseInt(filterCriteria.get("toYear")[0])); //defaults to 0
      stockedOutReportParam.setMonthFrom(StringUtils.isBlank(filterCriteria.get("fromMonth")[0]) ? originalStart.getMonth() : Integer.parseInt(filterCriteria.get("fromMonth")[0])); //defaults to 0
      stockedOutReportParam.setMonthTo(StringUtils.isBlank(filterCriteria.get("toMonth")[0]) ? originalEnd.getMonth() : Integer.parseInt(filterCriteria.get("toMonth")[0])); //defaults to 0

      stockedOutReportParam.setPeriodType(StringUtils.isBlank(filterCriteria.get("periodType")[0]) ? "" : filterCriteria.get("periodType")[0].toString());
      stockedOutReportParam.setQuarterFrom(StringUtils.isBlank(filterCriteria.get("fromQuarter")[0]) ? 1 : Integer.parseInt(filterCriteria.get("fromQuarter")[0]));
      stockedOutReportParam.setQuarterTo(StringUtils.isBlank(filterCriteria.get("toQuarter")[0]) ? 1 : Integer.parseInt(filterCriteria.get("toQuarter")[0]));
      stockedOutReportParam.setSemiAnnualFrom(StringUtils.isBlank(filterCriteria.get("fromSemiAnnual")[0]) ? 1 : Integer.parseInt(filterCriteria.get("fromSemiAnnual")[0]));
      stockedOutReportParam.setSemiAnnualTo(StringUtils.isBlank(filterCriteria.get("toSemiAnnual")[0]) ? 1 : Integer.parseInt(filterCriteria.get("toSemiAnnual")[0]));

      int monthFrom = 0;
      int monthTo = 0;

      String periodType = stockedOutReportParam.getPeriodType();

      if (periodType.equals(Constants.PERIOD_TYPE_QUARTERLY)) {
        monthFrom = 3 * (stockedOutReportParam.getQuarterFrom() - 1);
        monthTo = 3 * stockedOutReportParam.getQuarterTo() - 1;

      } else if (periodType.equals(Constants.PERIOD_TYPE_MONTHLY)) {
        monthFrom = stockedOutReportParam.getMonthFrom();
        monthTo = stockedOutReportParam.getMonthTo();

      } else if (periodType.equals(Constants.PERIOD_TYPE_SEMI_ANNUAL)) {
        monthFrom = 6 * (stockedOutReportParam.getSemiAnnualFrom() - 1);
        monthTo = 6 * stockedOutReportParam.getSemiAnnualTo() - 1;
      } else if (periodType.equals(Constants.PERIOD_TYPE_ANNUAL)) {
        monthFrom = 0;
        monthTo = 11;
      }

      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.YEAR, stockedOutReportParam.getYearFrom());
      calendar.set(Calendar.MONTH, monthFrom);
      calendar.set(Calendar.DAY_OF_MONTH, 1);
      stockedOutReportParam.setStartDate(calendar.getTime());

      calendar.set(Calendar.YEAR, stockedOutReportParam.getYearTo());
      calendar.set(Calendar.MONTH, monthTo);
      calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
      stockedOutReportParam.setEndDate(calendar.getTime());
    }
    return stockedOutReportParam;
  }

  @Override
  public String getFilterSummary(Map<String, String[]> params) {
    return getReportFilterData(params).toString();
  }

  @Override
  public HashMap<String, String> getAdditionalReportData(Map params) {
    HashMap<String, String> result = new HashMap<String, String>();

    // spit out the summary section on the report.
    String totalFacilities = reportMapper.getTotalFacilities(params).get(0).toString();
    String stockedOut = reportMapper.getStockedoutTotalFacilities(params).get(0).toString();
    result.put("TOTAL_FACILITIES", totalFacilities);
    result.put("TOTAL_STOCKEDOUT", stockedOut);

    // Assume by default that the 100% of facilities didn't report
    Long percent = Long.parseLong("100");
    if (totalFacilities != "0") {
      percent = Math.round((Double.parseDouble(stockedOut) / Double.parseDouble(totalFacilities)) * 100);

    }
    result.put("PERCENTAGE_STOCKEDOUT", percent.toString());
    return result;
  }


}
