package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.openlmis.report.mapper.SummaryReportMapper;
import org.openlmis.report.model.ReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 */
@Component
@NoArgsConstructor
public class SummaryReportDataProvider extends ReportDataProvider {


    private SummaryReportMapper reportMapper;


    @Autowired
    public SummaryReportDataProvider(SummaryReportMapper mapper) {
        this.reportMapper = mapper;
    }

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(ReportData criteria) {

        return reportMapper.getReport(null);
    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(ReportData filterCriteria) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.0
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(ReportData filterCriteria, ReportData SortCriteria, int page, int pageSize) {
        return reportMapper.getReport(null);
    }

    @Override
    public int getReportDataCountByFilterCriteria(ReportData facilityReportFilter) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }


}
