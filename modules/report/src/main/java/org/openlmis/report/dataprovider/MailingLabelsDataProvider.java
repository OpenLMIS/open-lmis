package org.openlmis.report.dataprovider;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.report.model.FacilityReport;
import org.openlmis.report.model.MailingLabelReport;
import org.openlmis.report.model.ReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 */

@Component
@NoArgsConstructor
public class MailingLabelsDataProvider extends ReportDataProvider {

    private FacilityService facilityService;

    @Autowired
    public MailingLabelsDataProvider(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    private ReportData getMailingLabelReport(Facility facility){
        if(facility == null) return null;
        return new MailingLabelReport(facility.getCode(),facility.getName(),facility.getFacilityType() != null ? facility.getFacilityType().getName() : null,facility.getActive(),facility.getOperatedBy() != null ? facility.getOperatedBy().getText() : null,facility.getLatitude(),facility.getLongitude(),facility.getAltitude(),null,facility.getMainPhone(),null, null);
    }

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(ReportData filterCriteria) {
        if(filterCriteria == null) {

            List<Facility> facilities = facilityService.getAllFacilitiesDetail();
            return getListMailingLabelsReport(facilities);
        }
        if (!(filterCriteria instanceof FacilityReport)) return null;

        FacilityReport filter = (FacilityReport) filterCriteria;
        List<Facility> facilities = facilityService.searchFacilitiesByCodeOrName(filter.getFacilityName());
        return getListMailingLabelsReport(facilities);
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
        return null;
    }

    @Override
    public int getReportDataCountByFilterCriteria(ReportData facilityReportFilter) {
        return 0;
    }
}
