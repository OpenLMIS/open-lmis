/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.StockedOutReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.filter.StockedOutReportFilter;
import org.openlmis.report.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.lang.String;

/**
 * User: mahmed
 * Date: 7/27/13
 * Time: 4:45 PM
 */
//@Service
//@NoArgsConstructor

@Component
@NoArgsConstructor
public class StockedOutReportDataProvider extends ReportDataProvider {

    private StockedOutReportMapper reportMapper;

    @Autowired
    public StockedOutReportDataProvider(StockedOutReportMapper mapper) {
        this.reportMapper = mapper;
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
        StockedOutReportFilter stockedOutReportFilter = null;

        if(filterCriteria != null){
            stockedOutReportFilter = new StockedOutReportFilter();
            //Calendar originalStart = Calendar.getInstance();
            //Calendar originalEnd = Calendar.getInstance();

            Date originalStart =  new Date();
            Date originalEnd =  new Date();

            stockedOutReportFilter.setZoneId( StringUtils.isBlank(filterCriteria.get("zoneId")[0])  ? 0 : Integer.parseInt(filterCriteria.get("zoneId")[0]));  //defaults to 0
            stockedOutReportFilter.setZone( StringUtils.isBlank( filterCriteria.get("zone")[0] ) ? "All Zones" : filterCriteria.get("zone")[0]);

            stockedOutReportFilter.setFacilityTypeId( StringUtils.isBlank( filterCriteria.get("facilityTypeId")[0] ) ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
            stockedOutReportFilter.setFacilityType( StringUtils.isBlank( filterCriteria.get("facilityType")[0] ) ? "All Facility Types" : filterCriteria.get("facilityType")[0]);

           stockedOutReportFilter.setFacilityId( StringUtils.isBlank( filterCriteria.get("facilityId")[0] ) ? 0 : Integer.parseInt(filterCriteria.get("facilityId")[0])); //defaults to 0
           stockedOutReportFilter.setFacility( StringUtils.isBlank( filterCriteria.get("facility")[0] ) ? "All Facility Types" : filterCriteria.get("facility")[0]);

            stockedOutReportFilter.setRgroupId( StringUtils.isBlank( filterCriteria.get("rgroupId")[0] ) ? 0 : Integer.parseInt(filterCriteria.get("rgroupId")[0])); //defaults to 0
            stockedOutReportFilter.setRgroup( StringUtils.isBlank(filterCriteria.get("rgroup")[0]) ? "All Reporting Groups" : filterCriteria.get("rgroup")[0]);

             stockedOutReportFilter.setProductCategoryId( StringUtils.isBlank(filterCriteria.get("productCategoryId")[0] ) ? 0 : Integer.parseInt(filterCriteria.get("productCategoryId")[0])); //defaults to 0
            stockedOutReportFilter.setProductCategory( StringUtils.isBlank(filterCriteria.get("productCategory")[0]) ? "All Product Categories" : filterCriteria.get("productCategory")[0]);

            stockedOutReportFilter.setProductId( StringUtils.isBlank( filterCriteria.get("productId") [0] ) ? 0 : Integer.parseInt(filterCriteria.get("productId")[0])); //defaults to 0
            stockedOutReportFilter.setProduct( StringUtils.isBlank(filterCriteria.get("product")[0]) ? "All Products" : filterCriteria.get("product")[0]);

            stockedOutReportFilter.setProgramId( StringUtils.isBlank( filterCriteria.get("programId")[0] ) ? 0 : Integer.parseInt(filterCriteria.get("programId")[0])); //defaults to 0
            stockedOutReportFilter.setProgram( StringUtils.isBlank(filterCriteria.get("program")[0]) ? "All Programs" : filterCriteria.get("program")[0]);

            //monthly
            stockedOutReportFilter.setYearFrom( StringUtils.isBlank( filterCriteria.get("fromYear")[0] ) ? originalStart.getYear() : Integer.parseInt(filterCriteria.get("fromYear")[0])); //defaults to 0
            stockedOutReportFilter.setYearTo( StringUtils.isBlank( filterCriteria.get("toYear")[0] ) ? originalEnd.getYear() : Integer.parseInt(filterCriteria.get("toYear")[0])); //defaults to 0
            stockedOutReportFilter.setMonthFrom( StringUtils.isBlank( filterCriteria.get("fromMonth")[0] ) ? originalStart.getMonth() : Integer.parseInt(filterCriteria.get("fromMonth")[0])); //defaults to 0
            stockedOutReportFilter.setMonthTo( StringUtils.isBlank( filterCriteria.get("toMonth")[0] ) ? originalEnd.getMonth() : Integer.parseInt(filterCriteria.get("toMonth")[0])); //defaults to 0

             stockedOutReportFilter.setPeriodType( StringUtils.isBlank( filterCriteria.get("periodType")[0] ) ? "" : filterCriteria.get("periodType")[0].toString());
            stockedOutReportFilter.setQuarterFrom( StringUtils.isBlank( filterCriteria.get("fromQuarter")[0] ) ? 1 : Integer.parseInt(filterCriteria.get("fromQuarter")[0]));
            stockedOutReportFilter.setQuarterTo( StringUtils.isBlank( filterCriteria.get("toQuarter")[0] ) ? 1 : Integer.parseInt(filterCriteria.get("toQuarter")[0]));
            stockedOutReportFilter.setSemiAnnualFrom( StringUtils.isBlank( filterCriteria.get("fromSemiAnnual")[0] ) ? 1 : Integer.parseInt(filterCriteria.get("fromSemiAnnual")[0]));
            stockedOutReportFilter.setSemiAnnualTo( StringUtils.isBlank( filterCriteria.get("toSemiAnnual")[0] ) ? 1 : Integer.parseInt(filterCriteria.get("toSemiAnnual")[0]));

            int monthFrom = 0;
            int monthTo = 0;

            String periodType = stockedOutReportFilter.getPeriodType();

            if(periodType.equals(Constants.PERIOD_TYPE_QUARTERLY)){
                monthFrom = 3 *(stockedOutReportFilter.getQuarterFrom() - 1);
                monthTo =  3 * stockedOutReportFilter.getQuarterTo() - 1;

            }else if(periodType.equals(Constants.PERIOD_TYPE_MONTHLY)){
                monthFrom = stockedOutReportFilter.getMonthFrom();
                monthTo = stockedOutReportFilter.getMonthTo();

            }else if(periodType.equals(Constants.PERIOD_TYPE_SEMI_ANNUAL)){
                monthFrom = 6 * (stockedOutReportFilter.getSemiAnnualFrom() - 1);
                monthTo = 6 *stockedOutReportFilter.getSemiAnnualTo() - 1;
            }else if(periodType.equals(Constants.PERIOD_TYPE_ANNUAL)){
                monthFrom = 0;
                monthTo = 11;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, stockedOutReportFilter.getYearFrom());
            calendar.set(Calendar.MONTH, monthFrom);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            stockedOutReportFilter.setStartDate(calendar.getTime());

            calendar.set(Calendar.YEAR, stockedOutReportFilter.getYearTo());
            calendar.set(Calendar.MONTH, monthTo);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            stockedOutReportFilter.setEndDate(calendar.getTime());

        }
        return stockedOutReportFilter;
    }

    @Override
    public HashMap<String, String> getAdditionalReportData(Map params){
        HashMap<String, String> result = new HashMap<String, String>() ;

        // spit out the summary section on the report.
        String totalFacilities = reportMapper.getTotalFacilities( params ).get(0).toString();
        String stockedOut = reportMapper.getStockedoutTotalFacilities(params).get(0).toString();
        result.put("TOTAL_FACILITIES", totalFacilities);
        result.put("TOTAL_STOCKEDOUT", stockedOut);

        // Assume by default that the 100% of facilities didn't report
        Long percent = Long.parseLong("100");
        if(totalFacilities != "0"){
            percent = Math.round((Double.parseDouble(stockedOut) /  Double.parseDouble(totalFacilities)) * 100);

        }
        result.put("PERCENTAGE_STOCKEDOUT",percent.toString());


        return    result;
    }


}
