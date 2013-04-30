package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.ConsumptionReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.filter.ConsumptionReportFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
    protected List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> filterCriteria) {

        return consumptionReportMapper.getReport(null);
    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.0
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page-1)*pageSize,pageSize);
        ConsumptionReportFilter consumptionReportFilter = null;

        if(filterCriteria != null){
            consumptionReportFilter = new ConsumptionReportFilter();

            consumptionReportFilter.setZoneId(filterCriteria.get("zoneId") == null ? 0 : Integer.parseInt(filterCriteria.get("zoneId")[0]));  //defaults to 0
            consumptionReportFilter.setFacilityTypeId(filterCriteria.get("facilityTypeId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
            //ConsumptionReportFilter.setStatusId(filterCriteria.get("statusId") == null || filterCriteria.get("statusId")[0].isEmpty() ? null : Boolean.valueOf(filterCriteria.get("statusId")[0]));

        }
        return consumptionReportMapper.getFilteredSortedPagedConsumptionReport(consumptionReportFilter,null,rowBounds);
    }

    @Override
    public int getReportDataCountByFilterCriteria(Map<String, String[]> filterCriteria) {
        ConsumptionReportFilter consumptionReportFilter = null;

        if(filterCriteria != null){
            consumptionReportFilter = new ConsumptionReportFilter();

             consumptionReportFilter.setZoneId(filterCriteria.get("zoneId") == null ? 0 : Integer.parseInt(filterCriteria.get("zoneId")[0]));  //defaults to 0
             consumptionReportFilter.setFacilityTypeId(filterCriteria.get("facilityTypeId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
            //ConsumptionReportFilter.setStatusId(filterCriteria.get("statusId") == null || filterCriteria.get("statusId")[0].isEmpty() ? null : Boolean.valueOf(filterCriteria.get("statusId")[0]));

        }
        return (int)consumptionReportMapper.getFilteredSortedPagedConsumptionReportCount(consumptionReportFilter);  //To change body of implemented methods use File | Settings | File Templates.
    }


}
