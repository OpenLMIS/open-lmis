/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.report.mapper.OrderFillRateReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.filter.OrderFillRateReportFilter;
import org.openlmis.report.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * User: Hassan
 * Date: 7/27/13
 * Time: 4:45 PM
 */
@Service
@NoArgsConstructor
public class OrderFillRateReportDataProvider extends ReportDataProvider {

    private OrderFillRateReportMapper reportMapper;
    private ConfigurationSettingService configurationService;

    @Autowired
    public OrderFillRateReportDataProvider(OrderFillRateReportMapper mapper, ConfigurationSettingService configurationService) {
        this.reportMapper = mapper;
        this.configurationService = configurationService;
    }

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> filterCriteria) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET,RowBounds.NO_ROW_LIMIT);
        return reportMapper.getReport(getReportFilterData(filterCriteria), filterCriteria, rowBounds);
    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET,RowBounds.NO_ROW_LIMIT);
        return reportMapper.getReport(getReportFilterData(filterCriteria), null, rowBounds);
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page-1) * pageSize,pageSize);
        return reportMapper.getReport(getReportFilterData(filterCriteria), SortCriteria, rowBounds);
    }

    @Override
    public ReportData getReportFilterData(Map<String, String[]> filterCriteria) {
        OrderFillRateReportFilter orderFillRateReportFilter = null;

        if(filterCriteria != null){
            orderFillRateReportFilter = new OrderFillRateReportFilter();

            orderFillRateReportFilter.setFacilityTypeId(filterCriteria.get("facilityTypeId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
            orderFillRateReportFilter.setFacilityType( (filterCriteria.get("facilityType") == null || filterCriteria.get("facilityType")[0].equals("")) ? "All Facilities" : filterCriteria.get("facilityType")[0]);
            orderFillRateReportFilter.setFacility(filterCriteria.get("facility") == null ? "" : filterCriteria.get("facility")[0]);

            orderFillRateReportFilter.setRgroup( (filterCriteria.get("rgroup") == null || filterCriteria.get("rgroup")[0].equals("")) ? "All Reporting Groups" : filterCriteria.get("rgroup")[0]);


            orderFillRateReportFilter.setProductCategoryId(filterCriteria.get("productCategoryId") == null ? 0 : Integer.parseInt(filterCriteria.get("productCategoryId")[0])); //defaults to 0
            orderFillRateReportFilter.setProductCategory( (filterCriteria.get("productCategory") == null || filterCriteria.get("productCategory")[0].equals("")) ? "All Product Categories" : filterCriteria.get("productCategory")[0]);
            orderFillRateReportFilter.setProductId(filterCriteria.get("productId") == null ? 0 : Integer.parseInt(filterCriteria.get("productId")[0])); //defaults to 0

            if(orderFillRateReportFilter.getProductId() == 0)
                orderFillRateReportFilter.setProduct("All Products");
            else if(orderFillRateReportFilter.getProductId() == -1)//Indicator Products
                orderFillRateReportFilter.setProduct(configurationService.getConfigurationStringValue(Constants.CONF_INDICATOR_PRODUCTS).isEmpty() ? "Indicator Products" : configurationService.getConfigurationStringValue(Constants.CONF_INDICATOR_PRODUCTS));
            else
                orderFillRateReportFilter.setProduct(filterCriteria.get("product")[0]);

            orderFillRateReportFilter.setRgroupId(filterCriteria.get("rgroupId") == null ? 0 : Integer.parseInt(filterCriteria.get("rgroupId")[0])); //defaults to 0
            orderFillRateReportFilter.setProgramId(filterCriteria.get("programId") == null ? 0 : Integer.parseInt(filterCriteria.get("programId")[0]));

            if(orderFillRateReportFilter.getProgramId() == 0 || orderFillRateReportFilter.getProgramId() == -1)
                orderFillRateReportFilter.setProgram("All Programs");
            else
                orderFillRateReportFilter.setProgram(filterCriteria.get("program")[0]);

            orderFillRateReportFilter.setScheduleId(filterCriteria.get("scheduleId") == null ? 0 : Integer.parseInt(filterCriteria.get("scheduleId")[0]));
            orderFillRateReportFilter.setSchedule(filterCriteria.get("schedule") == null ? "" : filterCriteria.get("schedule")[0]);
            orderFillRateReportFilter.setPeriod(filterCriteria.get("period") == null ? "" : filterCriteria.get("period")[0]);
            orderFillRateReportFilter.setPeriodId(filterCriteria.get("periodId") == null ? 0 : Integer.parseInt(filterCriteria.get("periodId")[0]));
            orderFillRateReportFilter.setYear(filterCriteria.get("year") == null ? 0 : Integer.parseInt(filterCriteria.get("year")[0]));
        }
        return orderFillRateReportFilter;
    }
}
