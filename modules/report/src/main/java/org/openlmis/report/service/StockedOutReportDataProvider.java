package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.StockedOutReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.filter.StockedOutReportFilter;
import org.openlmis.report.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: mahmed
 * Date: 7/27/13
 * Time: 4:45 PM
 */
@Service
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
        return reportMapper.getReport(getReportFilterData(filterCriteria), null, rowBounds);
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
    public int getReportDataCountByFilterCriteria(Map<String, String[]> filter) {
        return 0;
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
}
