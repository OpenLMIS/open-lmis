package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.RnRFeedbackReportMapper;
import org.openlmis.report.model.ReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * User: Wolde
 * Date: 8/21/13
 * Time: 4:23 AM
 */
@Service
@NoArgsConstructor
public class RnRFeedbackReportDataProvider extends ReportDataProvider {

    private RnRFeedbackReportMapper mapper;

    @Autowired
    public RnRFeedbackReportDataProvider(RnRFeedbackReportMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> params) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET,RowBounds.NO_ROW_LIMIT);
        return mapper.getFilteredRnRFeedbackReport(params, rowBounds);
    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> params) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET,RowBounds.NO_ROW_LIMIT);
        return mapper.getFilteredRnRFeedbackReport(params, rowBounds);
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(Map<String, String[]> filter, Map<String, String[]> sorter, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page-1) * pageSize,pageSize);
        return  mapper.getFilteredRnRFeedbackReport(filter, rowBounds);
    }

    @Override
    public int getReportDataCountByFilterCriteria(Map<String, String[]> filter) {
        return 0;
    }

}
