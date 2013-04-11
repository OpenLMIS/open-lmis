package org.openlmis.report.dataprovider;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.report.dataprovider.ReportDataProvider;
import org.openlmis.report.mapper.ConsumptionReportMapper;
import org.openlmis.report.mapper.FacilityReportMapper;
import org.openlmis.report.model.FacilityReport;
import org.openlmis.report.model.ReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 */
@Component
@NoArgsConstructor
public class ConsumptionReportDataProvider extends ReportDataProvider {


    private ConsumptionReportMapper consumptionReportMapper;


    @Autowired
    public ConsumptionReportDataProvider(ConsumptionReportMapper mapper) {
        this.consumptionReportMapper = mapper;
    }

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(ReportData criteria) {

        return consumptionReportMapper.getReport(null);
    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(ReportData filterCriteria) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.0
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(ReportData filterCriteria, ReportData SortCriteria, int page, int pageSize) {
        return consumptionReportMapper.getReport(null);
    }

    @Override
    public int getReportDataCountByFilterCriteria(ReportData facilityReportFilter) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }


}
