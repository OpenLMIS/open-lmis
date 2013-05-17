package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.AdjustmentSummaryReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.filter.AdjustmentSummaryReportFilter;
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

            //monthly
            adjustmentSummaryReportFilter.setYearFrom(filterCriteria.get("fromYear") == null ? originalStart.getYear() : Integer.parseInt(filterCriteria.get("fromYear")[0])); //defaults to 0
            adjustmentSummaryReportFilter.setYearTo(filterCriteria.get("toYear") == null ? originalEnd.getYear() : Integer.parseInt(filterCriteria.get("toYear")[0])); //defaults to 0
            adjustmentSummaryReportFilter.setMonthFrom(filterCriteria.get("fromMonth") == null ? originalStart.getMonth() : Integer.parseInt(filterCriteria.get("fromMonth")[0])); //defaults to 0
            adjustmentSummaryReportFilter.setMonthTo(filterCriteria.get("toMonth") == null ? originalEnd.getMonth() : Integer.parseInt(filterCriteria.get("toMonth")[0])); //defaults to 0

             //quarterly


            //first day of the selected/default month
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(originalStart);

            calendar1.set(Calendar.YEAR, adjustmentSummaryReportFilter.getYearFrom()); //originalStart.setYear(consumptionReportFilter.getYearFrom());
            calendar1.set(Calendar.MONTH, adjustmentSummaryReportFilter.getMonthFrom());//originalStart.setMonth(consumptionReportFilter.getMonthFrom());
            calendar1.set(Calendar.DAY_OF_MONTH, 1);//originalStart.setDate(1);
            originalStart = calendar1.getTime();
            adjustmentSummaryReportFilter.setStartDate(originalStart);

            //last day of the selected/default month
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(originalEnd);

            calendar.set(Calendar.YEAR, adjustmentSummaryReportFilter.getYearTo());//originalEnd.setYear(consumptionReportFilter.getYearTo());
            calendar.set(Calendar.MONTH, adjustmentSummaryReportFilter.getMonthTo());//originalEnd.setMonth(consumptionReportFilter.getMonthTo());

            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DATE, -1);
            originalEnd = calendar.getTime();

            adjustmentSummaryReportFilter.setEndDate(originalEnd);

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
