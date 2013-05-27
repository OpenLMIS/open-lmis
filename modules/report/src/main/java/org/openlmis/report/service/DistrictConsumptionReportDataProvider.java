package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.DistrictConsumptionReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.filter.DistrictConsumptionReportFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    public int getReportDataCountByFilterCriteria(Map<String, String[]> filterCriteria) {

        return (int) reportMapper.getFilteredSortedPagedAdjustmentSummaryReportCount(getReportFilterData(filterCriteria));
    }


    @Override
    public ReportData getReportFilterData(Map<String, String[]> filterCriteria) {
        DistrictConsumptionReportFilter districtConsumptionReportFilter = null;

        if(filterCriteria != null){
            districtConsumptionReportFilter = new DistrictConsumptionReportFilter();
            Date originalStart =  new Date();
            Date originalEnd =  new Date();

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
