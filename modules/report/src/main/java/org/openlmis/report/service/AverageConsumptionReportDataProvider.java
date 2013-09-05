/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.AverageConsumptionReportMapper;
import org.openlmis.report.mapper.NonReportingFacilityReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.filter.AverageConsumptionReportFilter;
import org.openlmis.report.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.lang.String;

/**
 */
@Component
@NoArgsConstructor
public class AverageConsumptionReportDataProvider extends ReportDataProvider {


    private AverageConsumptionReportMapper reportMapper;


    @Autowired
    public AverageConsumptionReportDataProvider(AverageConsumptionReportMapper mapper) {
        this.reportMapper = mapper;
    }

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> filterCriteria) {

        return getReportDataByFilterCriteriaAndPagingAndSorting(filterCriteria,null,RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
       // return reportMapper.getReportData(filterCriteria);
    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
        return getReportDataByFilterCriteriaAndPagingAndSorting(filterCriteria,null,RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page-1)*pageSize,pageSize);
        return reportMapper.getFilteredSortedPagedAverageConsumptionReport(getReportFilterData(filterCriteria), SortCriteria, rowBounds);
    }

    @Override
    public int getReportDataCountByFilterCriteria(Map<String, String[]> filterCriteria) {

        return (int) reportMapper.getFilteredSortedPagedAverageConsumptionReportCount(getReportFilterData(filterCriteria));
    }

    @Override
    public AverageConsumptionReportFilter getReportFilterData(Map<String, String[]> filterCriteria) {
        AverageConsumptionReportFilter averageConsumptionReportFilter = null;

        if(filterCriteria != null){
            averageConsumptionReportFilter = new AverageConsumptionReportFilter();
            Date originalStart =  new Date();
            Date originalEnd =  new Date();

            averageConsumptionReportFilter.setZoneId( StringUtils.isBlank(filterCriteria.get("zoneId")[0])  ? 0 : Integer.parseInt(filterCriteria.get("zoneId")[0]));  //defaults to 0
            averageConsumptionReportFilter.setFacilityTypeId( StringUtils.isBlank( filterCriteria.get("facilityTypeId")[0] ) ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
            averageConsumptionReportFilter.setFacilityType( StringUtils.isBlank( filterCriteria.get("facilityType")[0] ) ? "ALL Facilities" : filterCriteria.get("facilityType")[0]);
            averageConsumptionReportFilter.setRgroup( StringUtils.isBlank(filterCriteria.get("rgroup")[0]) ? "ALL Reporting Groups" : filterCriteria.get("rgroup")[0]);


            averageConsumptionReportFilter.setProductCategoryId( StringUtils.isBlank(filterCriteria.get("productCategoryId")[0] ) ? 0 : Integer.parseInt(filterCriteria.get("productCategoryId")[0])); //defaults to 0
            averageConsumptionReportFilter.setProductId( StringUtils.isBlank( filterCriteria.get("productId") [0] ) ? 0 : Integer.parseInt(filterCriteria.get("productId")[0])); //defaults to 0
            averageConsumptionReportFilter.setRgroupId( StringUtils.isBlank( filterCriteria.get("rgroupId")[0] ) ? 0 : Integer.parseInt(filterCriteria.get("rgroupId")[0])); //defaults to 0
            averageConsumptionReportFilter.setProgramId( StringUtils.isBlank( filterCriteria.get("programId")[0] ) ? 0 : Integer.parseInt(filterCriteria.get("programId")[0])); //defaults to 0

            //monthly
            averageConsumptionReportFilter.setYearFrom( StringUtils.isBlank( filterCriteria.get("fromYear")[0] ) ? originalStart.getYear() : Integer.parseInt(filterCriteria.get("fromYear")[0])); //defaults to 0
            averageConsumptionReportFilter.setYearTo( StringUtils.isBlank( filterCriteria.get("toYear")[0] ) ? originalEnd.getYear() : Integer.parseInt(filterCriteria.get("toYear")[0])); //defaults to 0
            averageConsumptionReportFilter.setMonthFrom( StringUtils.isBlank( filterCriteria.get("fromMonth")[0] ) ? originalStart.getMonth() : Integer.parseInt(filterCriteria.get("fromMonth")[0])); //defaults to 0
            averageConsumptionReportFilter.setMonthTo( StringUtils.isBlank( filterCriteria.get("toMonth")[0] ) ? originalEnd.getMonth() : Integer.parseInt(filterCriteria.get("toMonth")[0])); //defaults to 0

            averageConsumptionReportFilter.setPdformat( StringUtils.isBlank( filterCriteria.get("pdformat")[0] ) ? 0 : Integer.parseInt(filterCriteria.get("pdformat")[0]));  //defaults to 0
            averageConsumptionReportFilter.setPeriodType( StringUtils.isBlank( filterCriteria.get("periodType")[0] ) ? "" : filterCriteria.get("periodType")[0].toString());
            averageConsumptionReportFilter.setQuarterFrom( StringUtils.isBlank( filterCriteria.get("fromQuarter")[0] ) ? 1 : Integer.parseInt(filterCriteria.get("fromQuarter")[0]));
            averageConsumptionReportFilter.setQuarterTo( StringUtils.isBlank( filterCriteria.get("toQuarter")[0] ) ? 1 : Integer.parseInt(filterCriteria.get("toQuarter")[0]));
            averageConsumptionReportFilter.setSemiAnnualFrom( StringUtils.isBlank( filterCriteria.get("fromSemiAnnual")[0] ) ? 1 : Integer.parseInt(filterCriteria.get("fromSemiAnnual")[0]));
            averageConsumptionReportFilter.setSemiAnnualTo( StringUtils.isBlank( filterCriteria.get("toSemiAnnual")[0] ) ? 1 : Integer.parseInt(filterCriteria.get("toSemiAnnual")[0]));

            int monthFrom = 0;
            int monthTo = 0;

            String periodType = averageConsumptionReportFilter.getPeriodType();

            if(periodType.equals(Constants.PERIOD_TYPE_QUARTERLY)){
                monthFrom = 3 *(averageConsumptionReportFilter.getQuarterFrom() - 1);
                monthTo =  3 * averageConsumptionReportFilter.getQuarterTo() - 1;

            }else if(periodType.equals(Constants.PERIOD_TYPE_MONTHLY)){
                monthFrom = averageConsumptionReportFilter.getMonthFrom();
                monthTo = averageConsumptionReportFilter.getMonthTo();

            }else if(periodType.equals(Constants.PERIOD_TYPE_SEMI_ANNUAL)){
                monthFrom = 6 * (averageConsumptionReportFilter.getSemiAnnualFrom() - 1);
                monthTo = 6 *averageConsumptionReportFilter.getSemiAnnualTo() - 1;
            }else if(periodType.equals(Constants.PERIOD_TYPE_ANNUAL)){
                monthFrom = 0;
                monthTo = 11;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, averageConsumptionReportFilter.getYearFrom());
            calendar.set(Calendar.MONTH, monthFrom);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            averageConsumptionReportFilter.setStartDate(calendar.getTime());

            calendar.set(Calendar.YEAR, averageConsumptionReportFilter.getYearTo());
            calendar.set(Calendar.MONTH, monthTo);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            averageConsumptionReportFilter.setEndDate(calendar.getTime());

        }
        return averageConsumptionReportFilter;

    }

    @Override
    public String filterDataToString(Map<String, String[]> filterCriteria){
        AverageConsumptionReportFilter  averageConsumptionReportFilter = (AverageConsumptionReportFilter) getReportFilterData(filterCriteria);
        SimpleDateFormat df = new SimpleDateFormat();
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT);
       // dateOut = dateFormatter.format(today);

        return "Period : "+  dateFormatter.format(averageConsumptionReportFilter.getStartDate()) +" - "+ dateFormatter.format(averageConsumptionReportFilter.getEndDate()) +" \n" +
                "Facility Types : "+ averageConsumptionReportFilter.getFacilityType() +"\n " +
                "Reporting Groups : "+ averageConsumptionReportFilter.getRgroup();

    }

}
