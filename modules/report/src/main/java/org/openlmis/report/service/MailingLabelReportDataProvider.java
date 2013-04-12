package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.report.mapper.MailingLabelReportMapper;
import org.openlmis.report.model.report.MailingLabelReport;
import org.openlmis.report.model.ReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
    protected List<? extends ReportData> getBeanCollectionReportData(ReportData filterCriteria) {

       return getReportDataByFilterCriteriaAndPagingAndSorting(filterCriteria,null,RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);

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
    protected List<? extends ReportData> getResultSetReportData(ReportData filterCriteria) {
        return null;
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(ReportData filterCriteria, ReportData SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page-1)*pageSize,pageSize);
        return mailingLabelReportMapper.SelectFilteredSortedPagedFacilities(filterCriteria,SortCriteria,rowBounds);
    }

    @Override
    public int getReportDataCountByFilterCriteria(ReportData mailingLabelReportFilter) {
        return (int)mailingLabelReportMapper.SelectFilteredFacilitiesCount(mailingLabelReportFilter);
    }

}
