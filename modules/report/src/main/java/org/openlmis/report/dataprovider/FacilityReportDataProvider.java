package org.openlmis.report.dataprovider;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.report.mapper.FacilityReportMapper;
import org.openlmis.report.mapper.ResultSetMapper;
import org.openlmis.report.model.FacilityReport;
import org.openlmis.report.model.FacilityReportFilter;
import org.openlmis.report.model.FacilityReportSorter;
import org.openlmis.report.model.ReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        return  null;
        //return new FacilityReport(facility.getCode(),facility.getName(),facility.getFacilityType() != null ? facility.getFacilityType().getName() : null,facility.getActive(),facility.getOperatedBy().getText(),facility.getLatitude(),facility.getLongitude(),facility.getAltitude(),null,facility.getMainPhone(),null, null);
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

        if(filterCriteria == null) {

            List<Facility> facilities = facilityService.getAllFacilitiesDetail();
            return getListFacilityReport(facilities);
        }
        if (!(filterCriteria instanceof FacilityReport)) return null;

        FacilityReport filter = (FacilityReport) filterCriteria;
        List<Facility> facilities = facilityService.searchFacilitiesByCodeOrName(filter.getFacilityName());
        return getListFacilityReport(facilities);
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

  //  @Override
  //   public List<? extends ReportData> getReportDataByFilterCriteriaAndPaging(ReportData filterCriteria, int page, int pageSize) {
  //       //return facilityReportMapper.SelectFilteredPagedFacilities(filterCriteria,page,pageSize);
  //      return null;  //To change body of implemented methods use File | Settings | File Templates.
  //  }

    @Override
    public int getReportDataCountByFilterCriteria(ReportData filterCriteria) {
        return (int)facilityReportMapper.SelectFilteredFacilitiesCount(filterCriteria);
        //return 100;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
