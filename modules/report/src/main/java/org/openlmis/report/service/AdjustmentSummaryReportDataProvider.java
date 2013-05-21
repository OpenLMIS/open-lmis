package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.AdjustmentSummaryReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.filter.AdjustmentSummaryReportFilter;
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
 * Date: 5/10/13
 * Time: 2:37 PM
 */
@Component
@NoArgsConstructor
public class AdjustmentSummaryReportDataProvider extends ReportDataProvider {


    private AdjustmentSummaryReportMapper reportMapper;


    @Autowired
    public AdjustmentSummaryReportDataProvider(AdjustmentSummaryReportMapper mapper) {
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
        return reportMapper.getFilteredSortedPagedAdjustmentSummaryReport(getReportFilterData(filterCriteria), SortCriteria, rowBounds);
    }

    @Override
    public int getReportDataCountByFilterCriteria(Map<String, String[]> filterCriteria) {

        return (int) reportMapper.getFilteredSortedPagedAdjustmentSummaryReportCount(getReportFilterData(filterCriteria));
    }

   /* @Override
    public ReportData getReportFilterData(final Map<String, String[]> params) {
       return new ReportData() {
                        @Override
                        public String toString() {
                            return "The Period: " + params.get("fromMonth")[0].toString() + ",  " + params.get("fromYear")[0].toString() +" - "+ params.get("toMonth")[0].toString() +" , "+ params.get("toYear")[0].toString() +"\n"

                                    ;
                        }
                    };
     }*/

    @Override
    public ReportData getReportFilterData(Map<String, String[]> filterCriteria) {
        AdjustmentSummaryReportFilter adjustmentSummaryReportFilter = null;

        if(filterCriteria != null){
            adjustmentSummaryReportFilter = new AdjustmentSummaryReportFilter();
            Date originalStart =  new Date();
            Date originalEnd =  new Date();

            adjustmentSummaryReportFilter.setZoneId(filterCriteria.get("zoneId") == null ? 0 : Integer.parseInt(filterCriteria.get("zoneId")[0]));  //defaults to 0
            adjustmentSummaryReportFilter.setFacilityTypeId(filterCriteria.get("facilityTypeId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
            adjustmentSummaryReportFilter.setFacilityType( (filterCriteria.get("facilityType") == null || filterCriteria.get("facilityType")[0].equals("")) ? "ALL Facilities" : filterCriteria.get("facilityType")[0]);
            adjustmentSummaryReportFilter.setRgroup( (filterCriteria.get("rgroup") == null || filterCriteria.get("rgroup")[0].equals("")) ? "ALL Reporting Groups" : filterCriteria.get("rgroup")[0]);


            adjustmentSummaryReportFilter.setProductCategoryId(filterCriteria.get("productCategoryId") == null ? 0 : Integer.parseInt(filterCriteria.get("productCategoryId")[0])); //defaults to 0
            adjustmentSummaryReportFilter.setProductId(filterCriteria.get("productId") == null ? 0 : Integer.parseInt(filterCriteria.get("productId")[0])); //defaults to 0
            adjustmentSummaryReportFilter.setRgroupId(filterCriteria.get("rgroupId") == null ? 0 : Integer.parseInt(filterCriteria.get("rgroupId")[0])); //defaults to 0
            adjustmentSummaryReportFilter.setProgramId(filterCriteria.get("programId") == null ? 0 : Integer.parseInt(filterCriteria.get("programId")[0])); //defaults to 0
            adjustmentSummaryReportFilter.setAdjustmentTypeId( (filterCriteria.get("adjustmentTypeId") == null || filterCriteria.get("adjustmentTypeId")[0].equals("")) ? "" : filterCriteria.get("adjustmentTypeId")[0]);
            adjustmentSummaryReportFilter.setAdjustmentType( (filterCriteria.get("adjustmentType") == null || filterCriteria.get("adjustmentType")[0].equals("")) ? "All Adjustment Types" : filterCriteria.get("adjustmentType")[0]);

            adjustmentSummaryReportFilter.setYearFrom(filterCriteria.get("fromYear") == null ? originalStart.getYear() : Integer.parseInt(filterCriteria.get("fromYear")[0])); //defaults to 0
            adjustmentSummaryReportFilter.setYearTo(filterCriteria.get("toYear") == null ? originalEnd.getYear() : Integer.parseInt(filterCriteria.get("toYear")[0])); //defaults to 0
            adjustmentSummaryReportFilter.setMonthFrom(filterCriteria.get("fromMonth") == null ? originalStart.getMonth() : Integer.parseInt(filterCriteria.get("fromMonth")[0])); //defaults to 0
            adjustmentSummaryReportFilter.setMonthTo(filterCriteria.get("toMonth") == null ? originalEnd.getMonth() : Integer.parseInt(filterCriteria.get("toMonth")[0])); //defaults to 0
            adjustmentSummaryReportFilter.setPeriodType(filterCriteria.get("periodType") == null ? "" : filterCriteria.get("periodType")[0].toString());
            adjustmentSummaryReportFilter.setQuarterFrom(filterCriteria.get("fromQuarter") == null ? 1 : Integer.parseInt(filterCriteria.get("fromQuarter")[0]));
            adjustmentSummaryReportFilter.setQuarterTo(filterCriteria.get("toQuarter") == null ? 1 : Integer.parseInt(filterCriteria.get("toQuarter")[0]));
            adjustmentSummaryReportFilter.setSemiAnnualFrom(filterCriteria.get("fromSemiAnnual") == null ? 1 : Integer.parseInt(filterCriteria.get("fromSemiAnnual")[0]));
            adjustmentSummaryReportFilter.setSemiAnnualTo(filterCriteria.get("toSemiAnnual") == null ? 1 : Integer.parseInt(filterCriteria.get("toSemiAnnual")[0]));

            int monthFrom = 0;
            int monthTo = 0;

            String periodType = adjustmentSummaryReportFilter.getPeriodType();

            if(periodType.equals(Constants.PERIOD_TYPE_QUARTERLY)){
                monthFrom = 3 *(adjustmentSummaryReportFilter.getQuarterFrom() - 1);
                monthTo =  3 * adjustmentSummaryReportFilter.getQuarterTo() - 1;

            }else if(periodType.equals(Constants.PERIOD_TYPE_MONTHLY)){
                monthFrom = adjustmentSummaryReportFilter.getMonthFrom();
                monthTo = adjustmentSummaryReportFilter.getMonthTo();

            }else if(periodType.equals(Constants.PERIOD_TYPE_SEMI_ANNUAL)){
                monthFrom = 6 * (adjustmentSummaryReportFilter.getSemiAnnualFrom() - 1);
                monthTo = 6 *adjustmentSummaryReportFilter.getSemiAnnualTo() - 1;
            }else if(periodType.equals(Constants.PERIOD_TYPE_ANNUAL)){
                monthFrom = 0;
                monthTo = 11;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, adjustmentSummaryReportFilter.getYearFrom());
            calendar.set(Calendar.MONTH, monthFrom);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            adjustmentSummaryReportFilter.setStartDate(calendar.getTime());

            calendar.set(Calendar.YEAR, adjustmentSummaryReportFilter.getYearTo());
            calendar.set(Calendar.MONTH, monthTo);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            adjustmentSummaryReportFilter.setEndDate(calendar.getTime());

        }
        return adjustmentSummaryReportFilter;

    }

    @Override
    public String filterDataToString(Map<String, String[]> filterCriteria){
        AdjustmentSummaryReportFilter  adjustmentSummaryReportFilter = (AdjustmentSummaryReportFilter) getReportFilterData(filterCriteria);
        SimpleDateFormat df = new SimpleDateFormat();
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT);
       // dateOut = dateFormatter.format(today);

        return "Period : "+  dateFormatter.format(adjustmentSummaryReportFilter.getStartDate()) +" - "+ dateFormatter.format(adjustmentSummaryReportFilter.getEndDate()) +" \n" +
                "Facility Types : "+ adjustmentSummaryReportFilter.getFacilityType() +"\n " +
                "Reporting Groups : "+ adjustmentSummaryReportFilter.getRgroup();

    }

}
