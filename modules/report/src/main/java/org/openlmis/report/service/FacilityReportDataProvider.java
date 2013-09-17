/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.report.mapper.FacilityReportMapper;
import org.openlmis.report.model.filter.FacilityReportFilter;
import org.openlmis.report.model.report.FacilityReport;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.sorter.FacilityReportSorter;
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

        return new FacilityReport(facility.getCode(),facility.getName(),facility.getFacilityType() != null ? facility.getFacilityType().getName() : null,facility.getActive(),facility.getAddress1(),facility.getOperatedBy() != null ? facility.getOperatedBy().getText() : null ,null,null,facility.getMainPhone(),null,null,null);
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
    protected List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> params) {

        return getReportDataByFilterCriteriaAndPagingAndSorting(params,null,RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);

    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> params) {
        return facilityReportMapper.getAllFacilitiesReportData();
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page-1)*pageSize,pageSize);

        FacilityReportSorter facilityReportSorter = null;
        if(sortCriteria != null){
            facilityReportSorter = new FacilityReportSorter();
            facilityReportSorter.setFacilityName(sortCriteria.get("facilityName") == null ? "" : sortCriteria.get("facilityName")[0]);
            facilityReportSorter.setCode(sortCriteria.get("code") == null ? "ASC" : sortCriteria.get("code")[0]);
            facilityReportSorter.setFacilityType(sortCriteria.get("facilityType") == null ? "ASC" : sortCriteria.get("facilityType")[0]);
        }


        return facilityReportMapper.SelectFilteredSortedPagedFacilities(getReportFilterData(filterCriteria),facilityReportSorter,rowBounds);
    }

    @Override
    public FacilityReportFilter getReportFilterData(Map<String, String[]> filterCriteria) {
        FacilityReportFilter facilityReportFilter = new FacilityReportFilter();

        if(filterCriteria != null){

            facilityReportFilter =  new FacilityReportFilter();
            facilityReportFilter.setZoneId(filterCriteria.get("zoneId") == null ? 0 : Integer.parseInt(filterCriteria.get("zoneId")[0]));  //defaults to 0
            facilityReportFilter.setFacilityTypeId(filterCriteria.get("facilityTypeId") == null ? 0 : Integer.parseInt(filterCriteria.get("facilityTypeId")[0])); //defaults to 0
            facilityReportFilter.setStatusId(filterCriteria.get("statusId") == null || filterCriteria.get("statusId")[0].isEmpty() ? null : Boolean.valueOf(filterCriteria.get("statusId")[0]));
            facilityReportFilter.setRgroup( (filterCriteria.get("rgroup") == null || filterCriteria.get("rgroup")[0].equals("")) ? "All Reporting Groups" : filterCriteria.get("rgroup")[0]);
            facilityReportFilter.setRgId(filterCriteria.get("rgroupId") == null ? 0 : Integer.parseInt(filterCriteria.get("rgroupId")[0])); //defaults to 0


        }

        return facilityReportFilter;
    }
}
