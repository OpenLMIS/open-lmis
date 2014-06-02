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
import org.openlmis.report.mapper.DistrictConsumptionReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.params.DistrictConsumptionReportParam;
import org.openlmis.report.util.Constants;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class DistrictConsumptionReportDataProvider extends ReportDataProvider {

  @Autowired
  private DistrictConsumptionReportMapper reportMapper;

  private DistrictConsumptionReportParam districtConsumptionReportParam = null;

  @Override
  protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
    return getMainReportData(filterCriteria, null, RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
  }

  @Override
  public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
    return reportMapper.getFilteredSortedPagedAdjustmentSummaryReport(getReportFilterData(filterCriteria), SortCriteria, rowBounds);
  }

  public DistrictConsumptionReportParam getReportFilterData(Map<String, String[]> filterCriteria) {

    if (filterCriteria != null) {
      districtConsumptionReportParam = new DistrictConsumptionReportParam();


      //districtConsumptionReportParam.setZoneId(StringUtils.isBlank(filterCriteria.get("zoneId")[0]) ? 0 : Integer.parseInt(filterCriteria.get("zoneId")[0]));  //defaults to 0
//      districtConsumptionReportParam.setRgroup(StringUtils.isBlank(filterCriteria.get("requisitionGroup")[0]) ? "All Requisition Groups" : filterCriteria.get("requisitionGroup")[0]);
//
//      districtConsumptionReportParam.setProduct(StringUtils.isBlank(filterCriteria.get("product")[0]) ? "All Products" : filterCriteria.get("product")[0]);
//      districtConsumptionReportParam.setZone(StringUtils.isBlank(filterCriteria.get("zone")[0]) ? "All Geographic Zones" : filterCriteria.get("zone")[0]);
//      districtConsumptionReportParam.setProductCategory(StringUtils.isBlank(filterCriteria.get("productCategory")[0]) ? "All Product Categories" : filterCriteria.get("productCategory")[0]);

      districtConsumptionReportParam.setProductCategoryId(StringHelper.isBlank(filterCriteria,("productCategory")) ? 0 : Integer.parseInt(filterCriteria.get("productCategory")[0])); //defaults to 0
      districtConsumptionReportParam.setProductId(StringHelper.isBlank(filterCriteria,("product")) ? 0 : Integer.parseInt(filterCriteria.get("product")[0])); //defaults to 0
      districtConsumptionReportParam.setRgroupId(StringHelper.isBlank(filterCriteria,("requisitionGroup")) ? 0 : Integer.parseInt(filterCriteria.get("requisitionGroup")[0])); //defaults to 0
      districtConsumptionReportParam.setProgramId(StringHelper.isBlank(filterCriteria,("program")) ? 0 : Integer.parseInt(filterCriteria.get("program")[0])); //defaults to 0
      // a required field
      districtConsumptionReportParam.setPeriod(Long.parseLong(filterCriteria.get("period")[0]));
    }

    return districtConsumptionReportParam;

  }

  @Override
  public String getFilterSummary(Map<String, String[]> params) {
    return getReportFilterData(params).toString();
  }

}
