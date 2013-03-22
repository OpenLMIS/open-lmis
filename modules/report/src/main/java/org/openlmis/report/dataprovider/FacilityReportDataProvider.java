package org.openlmis.report.dataprovider;

import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.report.model.FacilityReport;
import org.openlmis.report.model.ReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 */
@Component
public class FacilityReportDataProvider implements ReportDataProvider {

    private FacilityService facilityService;

    @Autowired
    public FacilityReportDataProvider(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @Override
    public List<ReportData> getReportDataByFilterCriteria(ReportData filterCriteria) {

        if(filterCriteria == null) {

            List<Facility> facilities = facilityService.getAll();
            return getListFacilityReport(facilities);
        }
        if (!(filterCriteria instanceof FacilityReport)) return null;

        FacilityReport filter = (FacilityReport) filterCriteria;
        List<Facility> facilities = facilityService.searchFacilitiesByCodeOrName(filter.getFacilityName());
        return getListFacilityReport(facilities);
    }

    private ReportData getFacilityReport(Facility facility){
        if(facility == null) return null;

        return new FacilityReport(facility.getCode(),facility.getName(),"facilityType");
    }

    private List<ReportData> getListFacilityReport(List<Facility> facilityList){

        if (facilityList == null) return null;

        List<ReportData> facilityReportList = new ArrayList<>(facilityList.size());

        for(Facility facility: facilityList){
            facilityReportList.add(getFacilityReport(facility));
        }

        return facilityReportList;
    }
}
