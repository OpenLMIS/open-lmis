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
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.openlmis.report.mapper.ConsumptionReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.filter.ConsumptionReportFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class ConsumptionReportDataProvider extends ReportDataProvider {

  @Autowired
  private ConsumptionReportMapper consumptionReportMapper;

  @Override
  protected List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> filterCriteria) {

      return consumptionReportMapper.getReport(null);
  }

  @Override
  protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.0
  }

  @Override
  public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
      RowBounds rowBounds = new RowBounds((page-1)*pageSize,pageSize);
      ConsumptionReportFilter consumptionReportFilter = null;

      if(filterCriteria != null){
          consumptionReportFilter = new ConsumptionReportFilter();
          Date originalStart =  new Date();
          Date originalEnd =  new Date();

          consumptionReportFilter.setZoneId(filterCriteria.get("zoneId") == null ? 0 : Integer.parseInt(filterCriteria.get("zoneId")[0]));  //defaults to 0
          consumptionReportFilter.setFacilityTypeId(filterCriteria.get("facilityTypeId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
          //ConsumptionReportFilter.setStatusId(filterCriteria.get("statusId") == null || filterCriteria.get("statusId")[0].isEmpty() ? null : Boolean.valueOf(filterCriteria.get("statusId")[0]));
          consumptionReportFilter.setYearFrom(filterCriteria.get("fromYear") == null ? originalStart.getYear() : Integer.parseInt(filterCriteria.get("fromYear")[0])); //defaults to 0
          consumptionReportFilter.setYearTo(filterCriteria.get("toYear") == null ? originalEnd.getYear() : Integer.parseInt(filterCriteria.get("toYear")[0])); //defaults to 0
          consumptionReportFilter.setMonthFrom(filterCriteria.get("fromMonth") == null ? originalStart.getMonth() : Integer.parseInt(filterCriteria.get("fromMonth")[0])); //defaults to 0
          consumptionReportFilter.setMonthTo(filterCriteria.get("toMonth") == null ? originalEnd.getMonth() : Integer.parseInt(filterCriteria.get("toMonth")[0])); //defaults to 0


          //first day of the selected/default month
          Calendar calendar1 = Calendar.getInstance();
          calendar1.setTime(originalStart);

          calendar1.set(Calendar.YEAR, consumptionReportFilter.getYearFrom()); //originalStart.setYear(consumptionReportFilter.getYearFrom());
          calendar1.set(Calendar.MONTH, consumptionReportFilter.getMonthFrom());//originalStart.setMonth(consumptionReportFilter.getMonthFrom());
          calendar1.set(Calendar.DAY_OF_MONTH, 1);//originalStart.setDate(1);
          originalStart = calendar1.getTime();
          consumptionReportFilter.setStartDate(originalStart);

          //last day of the selected/default month
          Calendar calendar = Calendar.getInstance();
          calendar.setTime(originalEnd);

          calendar.set(Calendar.YEAR, consumptionReportFilter.getYearTo());//originalEnd.setYear(consumptionReportFilter.getYearTo());
          calendar.set(Calendar.MONTH, consumptionReportFilter.getMonthTo());//originalEnd.setMonth(consumptionReportFilter.getMonthTo());

          calendar.add(Calendar.MONTH, 1);
          calendar.set(Calendar.DAY_OF_MONTH, 1);
          calendar.add(Calendar.DATE, -1);
          originalEnd = calendar.getTime();

          consumptionReportFilter.setEndDate(originalEnd);

      }
      return consumptionReportMapper.getFilteredSortedPagedConsumptionReport(consumptionReportFilter, null, rowBounds);
  }

}
