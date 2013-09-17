/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.report.mapper.OrderSummaryReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.filter.OrderReportFilter;
import org.openlmis.report.util.Constants;
import org.openlmis.report.util.InteractiveReportPeriodFilterParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * User: Wolde
 * Date: 6/02/13
 * Time: 2:37 PM
 */
@Component
@NoArgsConstructor
public class OrderSummaryReportDataProvider extends ReportDataProvider {


    private OrderSummaryReportMapper reportMapper;
    private ConfigurationSettingService configurationService;


    @Autowired
    public OrderSummaryReportDataProvider(OrderSummaryReportMapper mapper, ConfigurationSettingService configurationService) {
        this.reportMapper = mapper;
        this.configurationService = configurationService;
    }

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> filterCriteria) {

        return getReportDataByFilterCriteriaAndPagingAndSorting(filterCriteria,filterCriteria,RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
       // return reportMapper.getReportData(filterCriteria);
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

    @Override
    public ReportData getReportFilterData(Map<String, String[]> filterCriteria) {

        OrderReportFilter orderReportFilter = null;

        if(filterCriteria != null){
            orderReportFilter = new OrderReportFilter();

            orderReportFilter.setFacilityTypeId(filterCriteria.get("facilityTypeId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
            orderReportFilter.setFacilityId(filterCriteria.get("facilityId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityId")[0])); //defaults to 0
            orderReportFilter.setFacilityType( (filterCriteria.get("facilityType") == null || filterCriteria.get("facilityType")[0].equals("")) ? "ALL Facilities" : filterCriteria.get("facilityType")[0]);

            orderReportFilter.setScheduleId(filterCriteria.get("scheduleId") == null ? 0 : Integer.parseInt(filterCriteria.get("scheduleId")[0])); //defaults to 0
            orderReportFilter.setSchedule(filterCriteria.get("schedule")[0]);
            //orderReportFilter.setProductCategoryId(filterCriteria.get("productCategoryId") == null ? 0 : Integer.parseInt(filterCriteria.get("productCategoryId")[0])); //defaults to 0

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
        return orderReportFilter;
    }
}
