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
import org.openlmis.core.service.ProcessingPeriodService;
import org.openlmis.core.service.ProgramService;
import org.openlmis.report.mapper.StockedOutReportMapper;
import org.openlmis.report.mapper.lookup.FacilityTypeReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.params.StockedOutReportParam;
import org.openlmis.report.util.SelectedFilterHelper;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class StockedOutReportDataProvider extends ReportDataProvider {

  @Autowired
  private StockedOutReportMapper reportMapper;

  private StockedOutReportParam stockedOutReportParam = null;

  @Autowired
  private SelectedFilterHelper selectedFilterHelper;

  @Autowired
  private ProcessingPeriodService periodService;
  
  @Autowired
  private ProgramService programService;

  @Autowired
  private FacilityTypeReportMapper facilityType;

  @Override
  protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
    RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    return reportMapper.getReport(getReportFilterData(filterCriteria), null, rowBounds,this.getUserId());
  }

  @Override
  public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
    return reportMapper.getReport(getReportFilterData(filterCriteria), SortCriteria, rowBounds, this.getUserId());
  }

  public StockedOutReportParam getReportFilterData(Map<String, String[]> filterCriteria) {
    if (filterCriteria != null) {
      stockedOutReportParam = new StockedOutReportParam();

      stockedOutReportParam.setFacilityTypeId(StringHelper.isBlank(filterCriteria,"facilityType") ? 0L : Long.parseLong(filterCriteria.get("facilityType")[0]));
      if(filterCriteria.containsKey("facility") && !StringHelper.isBlank(filterCriteria,"facility")){
        stockedOutReportParam.setFacilityId(Integer.parseInt(filterCriteria.get("facility")[0])); //defaults to 0
      }else{
        stockedOutReportParam.setFacilityId(0);
      }
      stockedOutReportParam.setZoneId(StringHelper.isBlank(filterCriteria, "zone")? 0:Integer.parseInt(filterCriteria.get("zone")[0]));
      stockedOutReportParam.setRgroupId(StringHelper.isBlank(filterCriteria,"requisitionGroup") ? 0 : Integer.parseInt(filterCriteria.get("requisitionGroup")[0]));
      stockedOutReportParam.setProductCategoryId(StringHelper.isBlank(filterCriteria,"productCategory") ? 0 : Integer.parseInt(filterCriteria.get("productCategory")[0]));
      stockedOutReportParam.setProductId(!filterCriteria.containsKey("product") ? "0" : java.util.Arrays.toString(filterCriteria.get("product")));
      stockedOutReportParam.setProgramId(StringHelper.isBlank(filterCriteria,"program") ? 0L : Long.parseLong(filterCriteria.get("program")[0]));
      stockedOutReportParam.setPeriodId(StringHelper.isBlank(filterCriteria,"period") ? 0L : Long.parseLong(filterCriteria.get("period")[0]));

    }
    return stockedOutReportParam;
  }

  @Override
  public String getFilterSummary(Map<String, String[]> params) {
    return selectedFilterHelper.getProgramPeriodGeoZone(params);
  }



}
