package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.openlmis.report.mapper.AverageConsumptionReportMapper;
import org.openlmis.report.mapper.NonReportingFacilityReportMapper;
import org.openlmis.report.model.ReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 */
@Component
@NoArgsConstructor
public class AverageConsumptionReportDataProvider extends ReportDataProvider {


    private AverageConsumptionReportMapper reportMapper;


    @Autowired
    public AverageConsumptionReportDataProvider(AverageConsumptionReportMapper mapper) {
        this.reportMapper = mapper;
    }

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> filterCriteria) {

        return reportMapper.getReportData(filterCriteria);
    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
        return null;
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        return reportMapper.getReportData(filterCriteria);
    }

    @Override
    public int getReportDataCountByFilterCriteria(Map<String, String[]> filterCriteria) {
        return 0;
    }

    @Override
    public ReportData getReportFilterData(Map<String, String[]> params) {
       return new ReportData() {
                        @Override
                        public String toString() {
                            return "The Period: " ;
                        }
                    };
     }
}
