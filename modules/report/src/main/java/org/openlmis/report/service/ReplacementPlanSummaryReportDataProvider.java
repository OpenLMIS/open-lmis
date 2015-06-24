package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.ReplacementPlanSummaryMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.util.SelectedFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Service
public class ReplacementPlanSummaryReportDataProvider extends ReportDataProvider {

    @Autowired
    private ReplacementPlanSummaryMapper mapper;

    @Autowired
    private SelectedFilterHelper filterHelper;


    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
        return mapper.getReport(filterCriteria, rowBounds);
    }
    @Override
    public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> sorter, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return mapper.getReport(filterCriteria, rowBounds);
    }

    @Override
    public String getFilterSummary(Map<String, String[]> params) {
        return filterHelper.getProgramGeoZoneFacility(params);
    }

}
