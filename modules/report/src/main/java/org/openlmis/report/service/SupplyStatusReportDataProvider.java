package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.SupplyStatusReportMapper;
import org.openlmis.report.model.ReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 */
@Component
@NoArgsConstructor
public class SupplyStatusReportDataProvider extends ReportDataProvider {


    private SupplyStatusReportMapper reportMapper;


    @Autowired
    public SupplyStatusReportDataProvider(SupplyStatusReportMapper mapper) {
        this.reportMapper = mapper;
    }

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> filterCriteria) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET,RowBounds.NO_ROW_LIMIT);
        return reportMapper.getReport(filterCriteria, rowBounds);
    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.0
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page-1) * pageSize,pageSize);
        return reportMapper.getReport(filterCriteria, rowBounds);
    }

    @Override
    public int getReportDataCountByFilterCriteria(Map<String, String[]> filterCriteria) {
        return reportMapper.getTotal(filterCriteria);
    }

    @Override
    public ReportData getReportFilterData(Map<String, String[]> params) {
        String facilityTypeId =  params.get("facilityTypeId")[0];
        String rgroupId =     params.get("rgroupId")[0];
        String facilityType =  "";
        String rgroup = "";

        if(facilityTypeId != null && !facilityTypeId.isEmpty()){
            if(facilityTypeId.equals("-1") || facilityTypeId.equals("0"))
                facilityType = "All Facility Types";
            else
                facilityType = "Facility Type : " +params.get("facilityType")[0];
        }

        if(rgroupId != null && !rgroupId.isEmpty()){
            if(rgroupId.equals("-1") || rgroupId.equals("0"))
                rgroup = "All Reporting Groups";
            else
                rgroup = "Reporting Groups : " +params.get("rgroup")[0];
        }
        final String finalFacilityType = facilityType;
        final String finalRgroup = rgroup;

        return new ReportData() {
           @Override
           public String toString() {
               return finalFacilityType +"\n"+ finalRgroup +"\n";
           }
       };
    }
}
