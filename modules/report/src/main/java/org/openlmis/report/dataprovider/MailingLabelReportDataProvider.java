package org.openlmis.report.dataprovider;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.report.mapper.FacilityReportMapper;
import org.openlmis.report.mapper.MailingLabelReportMapper;
import org.openlmis.report.model.FacilityReport;
import org.openlmis.report.model.MailingLabelReport;
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

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(ReportData filterCriteria) {
        /*if(filterCriteria == null) {

            List<Facility> facilities = facilityService.getAllFacilitiesDetail();
            return getListMailingLabelsReport(facilities);
        }
        if (!(filterCriteria instanceof FacilityReport)) return null;

        FacilityReport filter = (FacilityReport) filterCriteria;
        List<Facility> facilities = facilityService.searchFacilitiesByCodeOrName(filter.getFacilityName());
        return getListMailingLabelsReport(facilities);*/
       return getReportDataByFilterCriteriaAndPagingAndSorting(filterCriteria,null,RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
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

    private List<ReportData> getListMailingLabelsReport(List<Facility> facilityList){

        if (facilityList == null) return null;

        List<ReportData> facilityReportList = new ArrayList<>(facilityList.size());

        for(Facility facility: facilityList){
            facilityReportList.add(getMailingLabelReport(facility));
        }

        return facilityReportList;
    }
    private ReportData getMailingLabelReport(Facility facility){
       return null;
    }
}
