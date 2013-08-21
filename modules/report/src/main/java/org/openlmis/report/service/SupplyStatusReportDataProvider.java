/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

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
        String periodId = params.get("periodId")[0];
        String facilityType =  "";
        String rgroup = "";
        String period = "";

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

        if(periodId != null && !periodId.isEmpty()){
            period = (periodId.equals("-1") || periodId.equals("0") )? "All Reporting Periods" : "Reporting Period : " + params.get("period")[0];

        }
        final String finalFacilityType = facilityType;
        final String finalRgroup = rgroup;
        final String finalPeriod = period;

        return new ReportData() {
           @Override
           public String toString() {
               StringBuffer reportingFilter = new StringBuffer("");
               reportingFilter.append(finalPeriod).append("\n").append(finalFacilityType).append("\n").append(finalRgroup).append("\n");
               return reportingFilter.toString();
           }
       };
    }
}
