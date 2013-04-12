package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.report.mapper.FacilityReportMapper;
import org.openlmis.report.model.report.FacilityReport;
import org.openlmis.report.model.ReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 */
@Component
@NoArgsConstructor
public class FacilityReportDataProvider extends ReportDataProvider {


    private FacilityService facilityService;
    private FacilityReportMapper facilityReportMapper;


    @Autowired
    public FacilityReportDataProvider(FacilityService facilityService, FacilityReportMapper facilityReportMapper) {
        this.facilityService = facilityService;
        this.facilityReportMapper = facilityReportMapper;
    }

    private ReportData getFacilityReport(Facility facility){
        if(facility == null) return null;

        return new FacilityReport(facility.getCode(),facility.getName(),facility.getFacilityType() != null ? facility.getFacilityType().getName() : null,facility.getActive(),facility.getAddress1(),facility.getOperatedBy() != null ? facility.getOperatedBy().getText() : null ,null,null,facility.getMainPhone(),null,null);
    }

    private List<ReportData> getListFacilityReport(List<Facility> facilityList){

        if (facilityList == null) return null;

        List<ReportData> facilityReportList = new ArrayList<>(facilityList.size());

        for(Facility facility: facilityList){
            facilityReportList.add(getFacilityReport(facility));
        }

        return facilityReportList;
    }

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(ReportData filterCriteria) {

        return getReportDataByFilterCriteriaAndPagingAndSorting(filterCriteria,null,RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);

    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(ReportData filterCriteria) {
        return facilityReportMapper.getAllFacilitiesReportData();
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(ReportData filterCriteria, ReportData SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page-1)*pageSize,pageSize);
        return facilityReportMapper.SelectFilteredSortedPagedFacilities(filterCriteria,SortCriteria,rowBounds);
    }

    @Override
    public int getReportDataCountByFilterCriteria(ReportData filterCriteria) {
        return (int)facilityReportMapper.SelectFilteredFacilitiesCount(filterCriteria);
        //return 100;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
