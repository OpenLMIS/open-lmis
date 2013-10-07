/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
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

            districtConsumptionReportFilter.setZoneId( StringUtils.isBlank( filterCriteria.get("zoneId")[0] ) ? 0 : Integer.parseInt(filterCriteria.get("zoneId")[0]));  //defaults to 0
            districtConsumptionReportFilter.setRgroup( StringUtils.isBlank( filterCriteria.get("rgroup")[0] ) ? "ALL Reporting Groups" : filterCriteria.get("rgroup")[0]);


            districtConsumptionReportFilter.setProductCategoryId( StringUtils.isBlank(filterCriteria.get("productCategoryId")[0] ) ? 0 : Integer.parseInt(filterCriteria.get("productCategoryId")[0])); //defaults to 0
            districtConsumptionReportFilter.setProductId( StringUtils.isBlank(filterCriteria.get("productId")[0] ) ? 0 : Integer.parseInt(filterCriteria.get("productId")[0])); //defaults to 0
            districtConsumptionReportFilter.setRgroupId( StringUtils.isBlank(filterCriteria.get("rgroupId")[0] ) ? 0 : Integer.parseInt(filterCriteria.get("rgroupId")[0])); //defaults to 0
            districtConsumptionReportFilter.setProgramId( StringUtils.isBlank(filterCriteria.get("programId")[0] ) ? 0 : Integer.parseInt(filterCriteria.get("programId")[0])); //defaults to 0

            districtConsumptionReportFilter.setYearFrom( StringUtils.isBlank(filterCriteria.get("fromYear")[0] ) ? originalStart.getYear() : Integer.parseInt(filterCriteria.get("fromYear")[0])); //defaults to 0
            districtConsumptionReportFilter.setYearTo( StringUtils.isBlank( filterCriteria.get("toYear")[0] ) ? originalEnd.getYear() : Integer.parseInt(filterCriteria.get("toYear")[0])); //defaults to 0
            districtConsumptionReportFilter.setMonthFrom( StringUtils.isBlank(filterCriteria.get("fromMonth")[0] ) ? originalStart.getMonth() : Integer.parseInt(filterCriteria.get("fromMonth")[0])); //defaults to 0
            districtConsumptionReportFilter.setMonthTo( StringUtils.isBlank( filterCriteria.get("toMonth")[0] ) ? originalEnd.getMonth() : Integer.parseInt(filterCriteria.get("toMonth")[0])); //defaults to 0
            districtConsumptionReportFilter.setPeriodType( StringUtils.isBlank( filterCriteria.get("periodType")[0] ) ? "" : filterCriteria.get("periodType")[0].toString());
            districtConsumptionReportFilter.setQuarterFrom(StringUtils.isBlank(filterCriteria.get("fromQuarter")[0] ) ? 1 : Integer.parseInt(filterCriteria.get("fromQuarter")[0]));
            districtConsumptionReportFilter.setQuarterTo( StringUtils.isBlank( filterCriteria.get("toQuarter")[0] ) ? 1 : Integer.parseInt(filterCriteria.get("toQuarter")[0]));
            districtConsumptionReportFilter.setSemiAnnualFrom( StringUtils.isBlank( filterCriteria.get("fromSemiAnnual")[0] ) ? 1 : Integer.parseInt(filterCriteria.get("fromSemiAnnual")[0]));
            districtConsumptionReportFilter.setSemiAnnualTo( StringUtils.isBlank( filterCriteria.get("toSemiAnnual")[0] ) ? 1 : Integer.parseInt(filterCriteria.get("toSemiAnnual")[0]));

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
