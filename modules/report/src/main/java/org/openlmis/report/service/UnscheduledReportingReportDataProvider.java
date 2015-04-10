package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.UnscheduledReportingMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.util.SelectedFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class UnscheduledReportingReportDataProvider extends ReportDataProvider{


    @Autowired
    private UnscheduledReportingMapper reportMapper;

    @Autowired
    private SelectedFilterHelper filterHelper;

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
        return reportMapper.getFilteredSortedUnscheduledReportingReport(filterCriteria, rowBounds, this.getUserId());
    }

    @Override
    @Transactional
    public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getFilteredSortedUnscheduledReportingReport(filterCriteria, rowBounds, this.getUserId());
    }

    @Override
    @Transactional
    public String getFilterSummary(Map<String, String[]> params) {
        return  filterHelper.getProgramPeriodGeoZone(params);
    }

}
