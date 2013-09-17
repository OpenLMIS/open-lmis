/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.DistrictConsumptionReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.filter.DistrictConsumptionReportFilter;
import org.openlmis.report.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: Wolde
 * Date: 5/24/13
 */
@Component
@NoArgsConstructor
public class DistrictConsumptionReportDataProvider extends ReportDataProvider {


    private DistrictConsumptionReportMapper reportMapper;


    @Autowired
    public DistrictConsumptionReportDataProvider(DistrictConsumptionReportMapper mapper) {
        this.reportMapper = mapper;
    }

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> filterCriteria) {

        return getReportDataByFilterCriteriaAndPagingAndSorting(filterCriteria,null, RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
        // return reportMapper.getReportData(filterCriteria);
    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
        return getReportDataByFilterCriteriaAndPagingAndSorting(filterCriteria,null,RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page-1)*pageSize,pageSize);
        return reportMapper.getFilteredSortedPagedAdjustmentSummaryReport(getReportFilterData(filterCriteria), SortCriteria, rowBounds);
    }

    @Override
    public ReportData getReportFilterData(Map<String, String[]> filterCriteria) {
        DistrictConsumptionReportFilter districtConsumptionReportFilter = null;

        if(filterCriteria != null){
            districtConsumptionReportFilter = new DistrictConsumptionReportFilter();
            Date originalStart =  new Date();
            Date originalEnd =  new Date();

            districtConsumptionReportFilter.setZoneId(filterCriteria.get("zoneId") == null ? 0 : Integer.parseInt(filterCriteria.get("zoneId")[0]));  //defaults to 0
            districtConsumptionReportFilter.setRgroup( (filterCriteria.get("rgroup") == null || filterCriteria.get("rgroup")[0].equals("")) ? "ALL Reporting Groups" : filterCriteria.get("rgroup")[0]);


            districtConsumptionReportFilter.setProductCategoryId(filterCriteria.get("productCategoryId") == null ? 0 : Integer.parseInt(filterCriteria.get("productCategoryId")[0])); //defaults to 0
            districtConsumptionReportFilter.setProductId(filterCriteria.get("productId") == null ? 0 : Integer.parseInt(filterCriteria.get("productId")[0])); //defaults to 0
            districtConsumptionReportFilter.setRgroupId(filterCriteria.get("rgroupId") == null ? 0 : Integer.parseInt(filterCriteria.get("rgroupId")[0])); //defaults to 0
            districtConsumptionReportFilter.setProgramId(filterCriteria.get("programId") == null ? 0 : Integer.parseInt(filterCriteria.get("programId")[0])); //defaults to 0

            districtConsumptionReportFilter.setYearFrom(filterCriteria.get("fromYear") == null ? originalStart.getYear() : Integer.parseInt(filterCriteria.get("fromYear")[0])); //defaults to 0
            districtConsumptionReportFilter.setYearTo(filterCriteria.get("toYear") == null ? originalEnd.getYear() : Integer.parseInt(filterCriteria.get("toYear")[0])); //defaults to 0
            districtConsumptionReportFilter.setMonthFrom(filterCriteria.get("fromMonth") == null ? originalStart.getMonth() : Integer.parseInt(filterCriteria.get("fromMonth")[0])); //defaults to 0
            districtConsumptionReportFilter.setMonthTo(filterCriteria.get("toMonth") == null ? originalEnd.getMonth() : Integer.parseInt(filterCriteria.get("toMonth")[0])); //defaults to 0
            districtConsumptionReportFilter.setPeriodType(filterCriteria.get("periodType") == null ? "" : filterCriteria.get("periodType")[0].toString());
            districtConsumptionReportFilter.setQuarterFrom(filterCriteria.get("fromQuarter") == null ? 1 : Integer.parseInt(filterCriteria.get("fromQuarter")[0]));
            districtConsumptionReportFilter.setQuarterTo(filterCriteria.get("toQuarter") == null ? 1 : Integer.parseInt(filterCriteria.get("toQuarter")[0]));
            districtConsumptionReportFilter.setSemiAnnualFrom(filterCriteria.get("fromSemiAnnual") == null ? 1 : Integer.parseInt(filterCriteria.get("fromSemiAnnual")[0]));
            districtConsumptionReportFilter.setSemiAnnualTo(filterCriteria.get("toSemiAnnual") == null ? 1 : Integer.parseInt(filterCriteria.get("toSemiAnnual")[0]));

            int monthFrom = 0;
            int monthTo = 0;

            String periodType = districtConsumptionReportFilter.getPeriodType();

            if(periodType.equals(Constants.PERIOD_TYPE_QUARTERLY)){
                monthFrom = 3 *(districtConsumptionReportFilter.getQuarterFrom() - 1);
                monthTo =  3 * districtConsumptionReportFilter.getQuarterTo() - 1;

            }else if(periodType.equals(Constants.PERIOD_TYPE_MONTHLY)){
                monthFrom = districtConsumptionReportFilter.getMonthFrom();
                monthTo = districtConsumptionReportFilter.getMonthTo();

            }else if(periodType.equals(Constants.PERIOD_TYPE_SEMI_ANNUAL)){
                monthFrom = 6 * (districtConsumptionReportFilter.getSemiAnnualFrom() - 1);
                monthTo = 6 *districtConsumptionReportFilter.getSemiAnnualTo() - 1;
            }else if(periodType.equals(Constants.PERIOD_TYPE_ANNUAL)){
                monthFrom = 0;
                monthTo = 11;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, districtConsumptionReportFilter.getYearFrom());
            calendar.set(Calendar.MONTH, monthFrom);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            districtConsumptionReportFilter.setStartDate(calendar.getTime());

            calendar.set(Calendar.YEAR, districtConsumptionReportFilter.getYearTo());
            calendar.set(Calendar.MONTH, monthTo);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            districtConsumptionReportFilter.setEndDate(calendar.getTime());

        }

        return districtConsumptionReportFilter;

    }

    @Override
    public String filterDataToString(Map<String, String[]> filterCriteria){
        DistrictConsumptionReportFilter  districtConsumptionReportFilter = (DistrictConsumptionReportFilter) getReportFilterData(filterCriteria);
        SimpleDateFormat df = new SimpleDateFormat();
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT);
        // dateOut = dateFormatter.format(today);

        return "Period : "+  dateFormatter.format(districtConsumptionReportFilter.getStartDate()) +" - "+ dateFormatter.format(districtConsumptionReportFilter.getEndDate());


    }
}
