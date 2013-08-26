/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.StockedOutReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.filter.StockedOutReportFilter;
import org.openlmis.report.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

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
            Calendar originalStart = Calendar.getInstance();
            Calendar originalEnd = Calendar.getInstance();

            stockedOutReportFilter.setFacilityTypeId(filterCriteria.get("facilityTypeId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
            stockedOutReportFilter.setFacilityType( (filterCriteria.get("facilityType") == null || filterCriteria.get("facilityType")[0].equals("")) ? "ALL Facilities" : filterCriteria.get("facilityType")[0]);
            stockedOutReportFilter.setFacility(filterCriteria.get("facility") == null ? "" : filterCriteria.get("facility")[0]);

            stockedOutReportFilter.setRgroup( (filterCriteria.get("rgroup") == null || filterCriteria.get("rgroup")[0].equals("")) ? "ALL Reporting Groups" : filterCriteria.get("rgroup")[0]);


            stockedOutReportFilter.setProductCategoryId(filterCriteria.get("productCategoryId") == null ? 0 : Integer.parseInt(filterCriteria.get("productCategoryId")[0])); //defaults to 0
            stockedOutReportFilter.setProductId(filterCriteria.get("productId") == null ? 0 : Integer.parseInt(filterCriteria.get("productId")[0])); //defaults to 0
            stockedOutReportFilter.setRgroupId(filterCriteria.get("rgroupId") == null ? 0 : Integer.parseInt(filterCriteria.get("rgroupId")[0])); //defaults to 0

            stockedOutReportFilter.setYearFrom(filterCriteria.get("fromYear") == null ? originalStart.get(Calendar.YEAR) : Integer.parseInt(filterCriteria.get("fromYear")[0])); //defaults to 0
            stockedOutReportFilter.setYearTo(filterCriteria.get("toYear") == null ? originalEnd.get(Calendar.YEAR) : Integer.parseInt(filterCriteria.get("toYear")[0])); //defaults to 0
            stockedOutReportFilter.setMonthFrom(filterCriteria.get("fromMonth") == null ? originalStart.get(Calendar.MONTH) : Integer.parseInt(filterCriteria.get("fromMonth")[0])); //defaults to 0
            stockedOutReportFilter.setMonthTo(filterCriteria.get("toMonth") == null ? originalEnd.get(Calendar.MONTH) : Integer.parseInt(filterCriteria.get("toMonth")[0])); //defaults to 0
            stockedOutReportFilter.setPeriodType(filterCriteria.get("periodType") == null ? "" : filterCriteria.get("periodType")[0].toString());
            stockedOutReportFilter.setQuarterFrom(filterCriteria.get("fromQuarter") == null ? 1 : Integer.parseInt(filterCriteria.get("fromQuarter")[0]));
            stockedOutReportFilter.setQuarterTo(filterCriteria.get("toQuarter") == null ? 1 : Integer.parseInt(filterCriteria.get("toQuarter")[0]));
            stockedOutReportFilter.setSemiAnnualFrom(filterCriteria.get("fromSemiAnnual") == null ? 1 : Integer.parseInt(filterCriteria.get("fromSemiAnnual")[0]));
            stockedOutReportFilter.setSemiAnnualTo(filterCriteria.get("toSemiAnnual") == null ? 1 : Integer.parseInt(filterCriteria.get("toSemiAnnual")[0]));

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
    public int getReportDataCountByFilterCriteria(Map<String, String[]> filterCriteria) {
        return reportMapper.getStockedoutTotalFacilities(filterCriteria).get(0);
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
