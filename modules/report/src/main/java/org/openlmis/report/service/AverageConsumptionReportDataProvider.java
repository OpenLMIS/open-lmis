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
import org.openlmis.core.service.*;
import org.openlmis.report.mapper.AverageConsumptionReportMapper;
import org.openlmis.report.mapper.lookup.FacilityTypeReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.params.AverageConsumptionReportParam;
import org.openlmis.report.service.lookup.ReportLookupService;
import org.openlmis.report.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.lang.String;

@Component
@NoArgsConstructor
public class AverageConsumptionReportDataProvider extends ReportDataProvider {

  @Autowired
  private AverageConsumptionReportMapper reportMapper;

  private AverageConsumptionReportParam averageConsumptionReportParam;

  @Autowired
  private ProductCategoryService productCategoryService;

  @Autowired
  private ReportLookupService reportLookupService;

  @Autowired
  private GeographicZoneService geographicZoneService;

  @Autowired
  private RequisitionGroupService requisitionGroupService;

  @Autowired
  private FacilityTypeReportMapper facilityTypeService;

  @Autowired
  private ProgramService programService;

  @Autowired
  private FacilityService facilityService;


  @Override
  protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
    return getMainReportData(filterCriteria, null, RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
  }

  @Override
  public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
    return reportMapper.getFilteredSortedPagedAverageConsumptionReport(getReportFilterData(filterCriteria), SortCriteria, rowBounds);
  }

  public AverageConsumptionReportParam getReportFilterData(Map<String, String[]> filterCriteria) {
   averageConsumptionReportParam = new AverageConsumptionReportParam();
   if (filterCriteria != null) {

      Date originalStart = new Date();
      Date originalEnd = new Date();

      averageConsumptionReportParam.setZoneId(StringUtils.isBlank(filterCriteria.get("zoneId")[0]) ? 0 : Integer.parseInt(filterCriteria.get("zoneId")[0]));  //defaults to 0
      averageConsumptionReportParam.setFacilityTypeId(StringUtils.isBlank(filterCriteria.get("facilityTypeId")[0]) ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
      averageConsumptionReportParam.setFacilityType(StringUtils.isBlank(filterCriteria.get("facilityType")[0]) ? "All Facility Types" : filterCriteria.get("facilityType")[0]);


      averageConsumptionReportParam.setProductCategoryId(StringUtils.isBlank(filterCriteria.get("productCategoryId")[0]) ? 0 : Integer.parseInt(filterCriteria.get("productCategoryId")[0])); //defaults to 0
      averageConsumptionReportParam.setProductId(StringUtils.isBlank(filterCriteria.get("productId")[0]) ? "0" : (filterCriteria.get("productId")[0]).toString().replace("]", "}").replace("[", "{").replaceAll("\"", ""));
      averageConsumptionReportParam.setRgroupId(StringUtils.isBlank(filterCriteria.get("rgroupId")[0]) ? 0 : Long.parseLong(filterCriteria.get("rgroupId")[0])); //defaults to 0
      averageConsumptionReportParam.setProgramId(StringUtils.isBlank(filterCriteria.get("programId")[0]) ? 0 : Long.parseLong(filterCriteria.get("programId")[0])); //defaults to 0

      //monthly
      averageConsumptionReportParam.setYearFrom(StringUtils.isBlank(filterCriteria.get("fromYear")[0]) ? originalStart.getYear() : Integer.parseInt(filterCriteria.get("fromYear")[0])); //defaults to 0
      averageConsumptionReportParam.setYearTo(StringUtils.isBlank(filterCriteria.get("toYear")[0]) ? originalEnd.getYear() : Integer.parseInt(filterCriteria.get("toYear")[0])); //defaults to 0
      averageConsumptionReportParam.setMonthFrom(StringUtils.isBlank(filterCriteria.get("fromMonth")[0]) ? originalStart.getMonth() : Integer.parseInt(filterCriteria.get("fromMonth")[0])); //defaults to 0
      averageConsumptionReportParam.setMonthTo(StringUtils.isBlank(filterCriteria.get("toMonth")[0]) ? originalEnd.getMonth() : Integer.parseInt(filterCriteria.get("toMonth")[0])); //defaults to 0

      averageConsumptionReportParam.setPdformat(StringUtils.isBlank(filterCriteria.get("pdformat")[0]) ? 0 : Integer.parseInt(filterCriteria.get("pdformat")[0]));  //defaults to 0
      averageConsumptionReportParam.setPeriodType(StringUtils.isBlank(filterCriteria.get("periodType")[0]) ? "" : filterCriteria.get("periodType")[0].toString());
      averageConsumptionReportParam.setQuarterFrom(StringUtils.isBlank(filterCriteria.get("fromQuarter")[0]) ? 1 : Integer.parseInt(filterCriteria.get("fromQuarter")[0]));
      averageConsumptionReportParam.setQuarterTo(StringUtils.isBlank(filterCriteria.get("toQuarter")[0]) ? 1 : Integer.parseInt(filterCriteria.get("toQuarter")[0]));
      averageConsumptionReportParam.setSemiAnnualFrom(StringUtils.isBlank(filterCriteria.get("fromSemiAnnual")[0]) ? 1 : Integer.parseInt(filterCriteria.get("fromSemiAnnual")[0]));
      averageConsumptionReportParam.setSemiAnnualTo(StringUtils.isBlank(filterCriteria.get("toSemiAnnual")[0]) ? 1 : Integer.parseInt(filterCriteria.get("toSemiAnnual")[0]));

      int monthFrom = 0;
      int monthTo = 0;

      String periodType = averageConsumptionReportParam.getPeriodType();

      if (periodType.equals(Constants.PERIOD_TYPE_QUARTERLY)) {
        monthFrom = 3 * (averageConsumptionReportParam.getQuarterFrom() - 1);
        monthTo = 3 * averageConsumptionReportParam.getQuarterTo() - 1;

      } else if (periodType.equals(Constants.PERIOD_TYPE_MONTHLY)) {
        monthFrom = averageConsumptionReportParam.getMonthFrom();
        monthTo = averageConsumptionReportParam.getMonthTo();

      } else if (periodType.equals(Constants.PERIOD_TYPE_SEMI_ANNUAL)) {
        monthFrom = 6 * (averageConsumptionReportParam.getSemiAnnualFrom() - 1);
        monthTo = 6 * averageConsumptionReportParam.getSemiAnnualTo() - 1;
      } else if (periodType.equals(Constants.PERIOD_TYPE_ANNUAL)) {
        monthFrom = 0;
        monthTo = 11;
      }

      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.YEAR, averageConsumptionReportParam.getYearFrom());
      calendar.set(Calendar.MONTH, monthFrom);
      calendar.set(Calendar.DAY_OF_MONTH, 1);
      averageConsumptionReportParam.setStartDate(calendar.getTime());

      calendar.set(Calendar.YEAR, averageConsumptionReportParam.getYearTo());
      calendar.set(Calendar.MONTH, monthTo);
      calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
      averageConsumptionReportParam.setEndDate(calendar.getTime());

    }
    return averageConsumptionReportParam;

  }

  @Override
  public String getFilterSummary(Map<String, String[]> params) {
    if(averageConsumptionReportParam != null){
      averageConsumptionReportParam.setProgramService(programService);
      averageConsumptionReportParam.setFacilityService(facilityService);
      averageConsumptionReportParam.setFacilityTypeService(facilityTypeService);
      averageConsumptionReportParam.setProductCategoryService(productCategoryService);
      averageConsumptionReportParam.setReportLookupService(reportLookupService);
      averageConsumptionReportParam.setGeographicZoneService(geographicZoneService);
      averageConsumptionReportParam.setRequisitionGroupService(requisitionGroupService);
      return averageConsumptionReportParam.toString();
    }
    // read the params and try again.
    getReportFilterData(params);
    return getFilterSummary(params);
  }

}
