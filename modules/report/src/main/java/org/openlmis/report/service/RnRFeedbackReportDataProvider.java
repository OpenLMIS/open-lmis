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
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.report.mapper.RnRFeedbackReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.filter.RnRFeedbackReportFilter;
import org.openlmis.report.util.Constants;
import org.openlmis.report.util.InteractiveReportPeriodFilterParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class RnRFeedbackReportDataProvider extends ReportDataProvider {


    private RnRFeedbackReportMapper reportMapper;
    private ConfigurationSettingService configurationService;


    @Autowired
    public RnRFeedbackReportDataProvider(RnRFeedbackReportMapper mapper, ConfigurationSettingService configurationService) {
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
        return reportMapper.getFilteredSortedPagedRnRFeedbackReport(getReportFilterData(filterCriteria),SortCriteria, rowBounds);
    }

    @Override
    public ReportData getReportFilterData(Map<String, String[]> filterCriteria) {

        RnRFeedbackReportFilter feedbackReportFilter = null;

        if(filterCriteria != null){
            feedbackReportFilter = new RnRFeedbackReportFilter();
            Calendar originalStart = Calendar.getInstance();
            Calendar originalEnd = Calendar.getInstance();

            feedbackReportFilter.setFacilityTypeId(filterCriteria.get("facilityTypeId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
            feedbackReportFilter.setFacilityId(filterCriteria.get("facilityId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityId")[0])); //defaults to 0
            feedbackReportFilter.setFacilityType((filterCriteria.get("facilityType") == null || filterCriteria.get("facilityType")[0].equals("")) ? "All Facilities" : filterCriteria.get("facilityType")[0]);
            feedbackReportFilter.setProductId(filterCriteria.get("productId") == null ? 0 : Integer.parseInt(filterCriteria.get("productId")[0])); //defaults to 0
            if(feedbackReportFilter.getProductId() == 0){
                feedbackReportFilter.setProduct("All Products");
            }else if(feedbackReportFilter.getProductId() == -1){
            //Indicator Products
                feedbackReportFilter.setProduct(configurationService.getConfigurationStringValue(Constants.CONF_INDICATOR_PRODUCTS).isEmpty() ? "Indicator Products" : configurationService.getConfigurationStringValue(Constants.CONF_INDICATOR_PRODUCTS));
            }else {
                feedbackReportFilter.setProduct(filterCriteria.get("product")[0]);
            }

            feedbackReportFilter.setOrderType(filterCriteria.get("orderType") == null ? "" : filterCriteria.get("orderType")[0]);
            feedbackReportFilter.setPeriodId(filterCriteria.get("periodId") == null ? 0 : Integer.parseInt(filterCriteria.get("periodId")[0])); //defaults to 0
            feedbackReportFilter.setScheduleId(filterCriteria.get("scheduleId") == null ? 0 : Integer.parseInt(filterCriteria.get("scheduleId")[0])); //defaults to 0
            feedbackReportFilter.setSchedule(filterCriteria.get("schedule")[0]);
            feedbackReportFilter.setRgroupId(filterCriteria.get("rgroupId") == null ? 0 : Integer.parseInt(filterCriteria.get("rgroupId")[0])); //defaults to 0
            feedbackReportFilter.setRgroup( StringUtils.isBlank(filterCriteria.get("rgroup")[0]) ? "All Reporting Groups" : filterCriteria.get("rgroup")[0]);
            feedbackReportFilter.setPeriod(filterCriteria.get("period")[0]);
            feedbackReportFilter.setProgramId(filterCriteria.get("programId") == null ? 0 : Integer.parseInt(filterCriteria.get("programId")[0])); //defaults to 0
            feedbackReportFilter.setProgram(filterCriteria.get("program")[0]);


        }
        return feedbackReportFilter;
    }
}
