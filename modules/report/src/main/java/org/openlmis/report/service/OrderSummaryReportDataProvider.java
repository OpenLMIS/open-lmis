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
import org.openlmis.report.mapper.OrderSummaryReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.filter.OrderReportFilter;
import org.openlmis.report.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class OrderSummaryReportDataProvider extends ReportDataProvider {

  @Autowired
  private OrderSummaryReportMapper reportMapper;

  @Autowired
  private ConfigurationSettingService configurationService;

  @Autowired
  private ReportLookupService reportLookupService;

  private OrderReportFilter orderReportFilter;

  @Override
  protected List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> filterCriteria) {
      return getReportDataByFilterCriteriaAndPagingAndSorting(filterCriteria,filterCriteria,RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
  }

  @Override
  protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
      return getReportDataByFilterCriteriaAndPagingAndSorting(filterCriteria,filterCriteria,RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
  }

  @Override
  public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
      RowBounds rowBounds = new RowBounds((page-1)*pageSize,pageSize);
      return reportMapper.getFilteredSortedPagedOrderSummaryReport(getReportFilterData(filterCriteria),SortCriteria, rowBounds);
  }

  public OrderReportFilter getReportFilterData(Map<String, String[]> filterCriteria) {

    if(filterCriteria != null ){
      orderReportFilter = new OrderReportFilter();

      if(filterCriteria.containsKey("orderId")){
        orderReportFilter.setOrderId( Long.parseLong(filterCriteria.get("orderId")[0]) );

        orderReportFilter.setFacility(reportLookupService.getFacilityNameForRnrId(orderReportFilter.getOrderId()));
        orderReportFilter.setPeriod(reportLookupService.getPeriodTextForRnrId(orderReportFilter.getOrderId()));
        orderReportFilter.setProgram(reportLookupService.getProgramNameForRnrId(orderReportFilter.getOrderId()));
        orderReportFilter.setProduct("All products");
      }else{

        orderReportFilter.setFacilityTypeId(filterCriteria.get("facilityTypeId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
        orderReportFilter.setFacilityId(filterCriteria.get("facilityId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityId")[0])); //defaults to 0
        orderReportFilter.setFacilityType( (filterCriteria.get("facilityType") == null || filterCriteria.get("facilityType")[0].equals("")) ? "ALL Facilities" : filterCriteria.get("facilityType")[0]);

        orderReportFilter.setScheduleId(filterCriteria.get("scheduleId") == null ? 0 : Integer.parseInt(filterCriteria.get("scheduleId")[0])); //defaults to 0
        orderReportFilter.setSchedule(filterCriteria.get("schedule")[0]);

        orderReportFilter.setProductId(filterCriteria.get("productId") == null ? 0 : Integer.parseInt(filterCriteria.get("productId")[0])); //defaults to 0
        if(orderReportFilter.getProductId() == 0){
            orderReportFilter.setProduct("All Products");
        }else if(orderReportFilter.getProductId() == -1){
            orderReportFilter.setProduct(configurationService.getConfigurationStringValue(Constants.CONF_INDICATOR_PRODUCTS).isEmpty() ? "Indicator Products" : configurationService.getConfigurationStringValue(Constants.CONF_INDICATOR_PRODUCTS));
        }
        orderReportFilter.setOrderType(filterCriteria.get("orderType") == null ? "" : filterCriteria.get("orderType")[0]);
        orderReportFilter.setPeriodId(filterCriteria.get("periodId") == null ? 0 : Integer.parseInt(filterCriteria.get("periodId")[0])); //defaults to 0
        orderReportFilter.setPeriod(filterCriteria.get("period")[0]);
        orderReportFilter.setProgramId(filterCriteria.get("programId") == null ? 0 : Integer.parseInt(filterCriteria.get("programId")[0])); //defaults to 0
        orderReportFilter.setProgram(filterCriteria.get("program")[0]);
      }
    }
      return orderReportFilter;
  }

  @Override
  public String getFilterSummary(Map<String, String[]> params){
    return getReportFilterData(params).toString();
  }

  @Override
  public HashMap<String, String> getAdditionalReportData(Map params){
    HashMap<String, String> result = new HashMap<String, String>() ;
    result.put("ADDRESS", configurationService.getConfigurationStringValue("ORDER_REPORT_ADDRESS"));
    result.put("CUSTOM_REPORT_TITLE", configurationService.getConfigurationStringValue("ORDER_REPORT_TITLE"));
    result.put("ORDER_SUMMARY_SHOW_SIGNATURE_SPACE_FOR_CUSTOMER", configurationService.getConfigurationStringValue("ORDER_SUMMARY_SHOW_SIGNATURE_SPACE_FOR_CUSTOMER"));
    result.put("ORDER_SUMMARY_SHOW_DISCREPANCY_SECTION", configurationService.getConfigurationStringValue("ORDER_SUMMARY_SHOW_DISCREPANCY_SECTION"));
    return result;
  }
}
