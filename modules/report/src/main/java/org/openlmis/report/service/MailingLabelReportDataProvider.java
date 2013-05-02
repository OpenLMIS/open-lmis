package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.report.mapper.MailingLabelReportMapper;
import org.openlmis.report.model.filter.MailingLabelReportFilter;
import org.openlmis.report.model.report.MailingLabelReport;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.sorter.MailingLabelReportSorter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class MailingLabelReportDataProvider extends ReportDataProvider {

    private FacilityService facilityService;
    private MailingLabelReportMapper mailingLabelReportMapper;

    @Autowired
    public MailingLabelReportDataProvider(FacilityService facilityService, MailingLabelReportMapper mailingLabelReportMapper) {
        this.facilityService = facilityService;
        this.mailingLabelReportMapper = mailingLabelReportMapper;
    }

    private ReportData getMailingLabelReport(Facility facility){
        if(facility == null) return null;
        return new MailingLabelReport();
       //return new MailingLabelReport(facility.getCode(),facility.getName(),facility.getFacilityType().getName(),facility.getActive(),facility.getAddress1(),facility.getOperatedBy().getText(),facility.getLongitude(), null,);

       // return new MailingLabelReport(facility.getCode(),facility.getName(),facility.getFacilityType() != null ? facility.getFacilityType().getName() : null,facility.getActive(),facility.getOperatedBy() != null ? facility.getOperatedBy().getText() : null,facility.getLatitude(),facility.getLongitude(),facility.getAltitude(),null,facility.getMainPhone(),null, null);
    }

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> params) {

        return getReportDataByFilterCriteriaAndPagingAndSorting(params,null,RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> params) {

       return getReportDataByFilterCriteriaAndPagingAndSorting(params,null,RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);

    }

    private List<ReportData> getListMailingLabelsReport(List<Facility> facilityList){

        if (facilityList == null) return null;

        List<ReportData> facilityReportList = new ArrayList<>(facilityList.size());

        for(Facility facility: facilityList){
            facilityReportList.add(getMailingLabelReport(facility));
        }

        return facilityReportList;
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(Map<String, String[]> filterCriteria, Map<String, String[]> sorterCriteria, int page, int pageSize) {

        RowBounds rowBounds = new RowBounds((page-1)*pageSize,pageSize);

        MailingLabelReportSorter mailingLabelReportSorter = null;

        if(sorterCriteria != null){
            mailingLabelReportSorter = new MailingLabelReportSorter();
            mailingLabelReportSorter.setFacilityName(sorterCriteria.get("facilityName") == null ? "" : sorterCriteria.get("facilityName")[0]);
            mailingLabelReportSorter.setCode( sorterCriteria.get("code") == null ? "" :  sorterCriteria.get("code")[0]);
            mailingLabelReportSorter.setFacilityType(sorterCriteria.get("facilityType") == null ? "ASC" : sorterCriteria.get("facilityType")[0]);
        }

        return mailingLabelReportMapper.SelectFilteredSortedPagedFacilities(getReportFilterData(filterCriteria),mailingLabelReportSorter,rowBounds);
    }

    @Override
    public int getReportDataCountByFilterCriteria(Map<String, String[]> filterCriteria) {

        return (int)mailingLabelReportMapper.SelectFilteredFacilitiesCount(getReportFilterData(filterCriteria));
    }

    @Override
    public ReportData getReportFilterData(Map<String, String[]> filterCriteria) {
        MailingLabelReportFilter mailingLabelReportFilter = new MailingLabelReportFilter();
        if(filterCriteria != null){

            mailingLabelReportFilter.setFacilityCode(filterCriteria.get("facilityCodeFilter") == null ? "" : filterCriteria.get("facilityCodeFilter")[0]);
            mailingLabelReportFilter.setFacilityTypeId((filterCriteria.get("facilityTypeId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])));
            mailingLabelReportFilter.setFacilityName(filterCriteria.get("facilityNameFilter") == null ? "" : filterCriteria.get("facilityNameFilter")[0]);
        }

        return mailingLabelReportFilter;
    }

}
