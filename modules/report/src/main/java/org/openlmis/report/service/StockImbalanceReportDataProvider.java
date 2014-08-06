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
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.report.mapper.StockImbalanceReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.params.StockImbalanceReportParam;
import org.openlmis.report.util.Constants;
import org.openlmis.report.util.SelectedFilterHelper;
import org.openlmis.report.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@NoArgsConstructor
public class StockImbalanceReportDataProvider extends ReportDataProvider {


    @Autowired
    private SelectedFilterHelper filterHelper;

  @Autowired
  private StockImbalanceReportMapper reportMapper;

  @Autowired
  private ConfigurationSettingService configurationService;

  private StockImbalanceReportParam stockImbalanceReportParam = null;

  @Override
  protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
    RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    return reportMapper.getReport(getReportFilterData(filterCriteria), null, rowBounds, this.getUserId());
  }

  @Override
  public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
    RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
    return reportMapper.getReport(getReportFilterData(filterCriteria), SortCriteria, rowBounds, this.getUserId());
  }

  public StockImbalanceReportParam getReportFilterData(Map<String, String[]> filterCriteria) {

    if (filterCriteria != null) {
      stockImbalanceReportParam = new StockImbalanceReportParam();

      stockImbalanceReportParam.setFacilityTypeId(StringHelper.isBlank(filterCriteria,"facilityType")? 0 : Integer.parseInt(filterCriteria.get("facilityType")[0])); //defaults to 0

      stockImbalanceReportParam.setProductCategoryId(StringHelper.isBlank(filterCriteria,"productCategory") ? 0 : Integer.parseInt(filterCriteria.get("productCategory")[0])); //defaults to 0

      stockImbalanceReportParam.setProductId(StringHelper.isBlank(filterCriteria,"productId") ? 0 : Integer.parseInt(filterCriteria.get("productId")[0])); //defaults to 0


      //stockImbalanceReportParam.setRgroupId(StringHelper.isBlank( filterCriteria,"requisitionGroup") ? 0 : Integer.parseInt(filterCriteria.get("requisitionGroup")[0])); //defaults to 0
      stockImbalanceReportParam.setProgramId(StringHelper.isBlank(filterCriteria, "program")  ? 0 : Integer.parseInt(filterCriteria.get("program")[0]));
      stockImbalanceReportParam.setScheduleId(StringHelper.isBlank(filterCriteria, "schedule") ? 0 : Integer.parseInt(filterCriteria.get("schedule")[0]));
      stockImbalanceReportParam.setPeriodId(StringHelper.isBlank(filterCriteria,"period") ? 0 : Integer.parseInt(filterCriteria.get("period")[0]));
      stockImbalanceReportParam.setYear(StringHelper.isBlank(filterCriteria, "year") ? 0 : Integer.parseInt(filterCriteria.get("year")[0]));
      stockImbalanceReportParam.setSchedule(StringHelper.isBlank(filterCriteria, "schedule") ? "" : filterCriteria.get("schedule")[0]);
      stockImbalanceReportParam.setPeriod(StringHelper.isBlank(filterCriteria,"period") ? "" : filterCriteria.get("period")[0]);
      stockImbalanceReportParam.setZoneId(StringHelper.isBlank(filterCriteria,"zone") ? 0 : Long.parseLong(filterCriteria.get("zone")[0]));

      if (stockImbalanceReportParam.getProductId() == 0) {
        stockImbalanceReportParam.setProduct("All Products");
      }else if (stockImbalanceReportParam.getProductId() == -1) {//Indicator Products
        stockImbalanceReportParam.setProduct(configurationService.getConfigurationStringValue(Constants.CONF_INDICATOR_PRODUCTS).isEmpty() ? "Indicator Products" : configurationService.getConfigurationStringValue(Constants.CONF_INDICATOR_PRODUCTS));
      }else {
        stockImbalanceReportParam.setProduct(filterCriteria.get("product")[0]);
      }
      stockImbalanceReportParam.setProductCategory((StringHelper.isBlank(filterCriteria, "productCategory") ) ? "All Product Categories" : filterCriteria.get("productCategory")[0]);
      stockImbalanceReportParam.setFacilityType((StringHelper.isBlank(filterCriteria,"facilityType") ) ? "All Facilities" : filterCriteria.get("facilityType")[0]);
      stockImbalanceReportParam.setFacility(StringHelper.isBlank(filterCriteria,"facility")? "" : filterCriteria.get("facility")[0]);
      //stockImbalanceReportParam.setRgroup(StringHelper.isBlank(filterCriteria, "requisitionGroup") ? "All Reporting Groups" : filterCriteria.get("requisitionGroup")[0]);
      if (stockImbalanceReportParam.getProgramId() == 0 || stockImbalanceReportParam.getProgramId() == -1) {
        stockImbalanceReportParam.setProgram("All Programs");
      }else {
        stockImbalanceReportParam.setProgram(filterCriteria.get("program")[0]);
      }

    }
    return stockImbalanceReportParam;
  }

  @Override
  public String getFilterSummary(Map<String, String[]> params) {
   // return getReportFilterData(params).toString();
      return filterHelper.getProgramPeriodGeoZone(params);
  }
}
