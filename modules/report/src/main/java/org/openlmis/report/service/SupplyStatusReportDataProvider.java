/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.service.ConfigurationSettingService;
import org.openlmis.report.mapper.SupplyStatusReportMapper;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.util.Constants;
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
    private ConfigurationSettingService configurationService;


    @Autowired
    public SupplyStatusReportDataProvider(SupplyStatusReportMapper mapper, ConfigurationSettingService configurationService) {
        this.reportMapper = mapper;
        this.configurationService = configurationService;
    }

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> filterCriteria) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET,RowBounds.NO_ROW_LIMIT);
        return reportMapper.getSupplyStatus(filterCriteria, rowBounds);
    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.0
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page-1) * pageSize,pageSize);
        return reportMapper.getSupplyStatus(filterCriteria, rowBounds);
    }

    @Override
    public int getReportDataCountByFilterCriteria(Map<String, String[]> filterCriteria) {
        return 0;
    }

    @Override
    public ReportData getReportFilterData(Map<String, String[]> params) {
        String facilityTypeId =  params.get("facilityTypeId")[0];
        String periodId = params.get("periodId")[0];
        String facilityType =  "";
        String period = "";
        String productId = params.get("productId")[0];
        String product = params.get("product")[0];
        String program = params.get("program")[0];
        String schedule = params.get("schedule")[0];
        String facilityId = params.get("facilityId")[0];
        String facility = params.get("facility")[0];
        String zoneId = params.get("zoneId")[0];
        String zone = params.get("zone")[0];

        if(facilityTypeId != null && !facilityTypeId.isEmpty()){
            if(facilityTypeId.equals("-1") || facilityTypeId.equals("0"))
                facilityType = "All Facility Types";
            else
                facilityType = params.get("facilityType")[0];
        }

        period = params.get("period")[0];

        if(productId != null && !productId.isEmpty()){
            if(productId.equals("-1")){
                //Indicator Products
                product = configurationService.getConfigurationStringValue(Constants.CONF_INDICATOR_PRODUCTS).isEmpty() ? "Indicator Products" :
                        configurationService.getConfigurationStringValue(Constants.CONF_INDICATOR_PRODUCTS);
            }else if(productId.equals("0"))//All Products
                product = "All Products";

        }
        if(facilityId != null && facilityId.isEmpty()){
            facility = "All Facilities";
        }
        if(zoneId != null && (zoneId.isEmpty() || zoneId.equals("0"))){
            zone = "All Zones";
        }
        final String finalFacilityType = "Facility Type : " + facilityType;
        final String finalPeriod = "Report Period : " + (period.isEmpty() ? " - " : period);
        final String finalProgram = "Program : " + (program.isEmpty() ? " - " : program);
        final String finalProduct = "Product : " + product;
        final String finalSchedule = "Schedule : " + schedule;
        final String finalFacility = "Facility : " + (facility.isEmpty() ? " - " : facility);
        final String finalZone = "Zone : " + zone;

        return new ReportData() {
           @Override
           public String toString() {
               StringBuffer reportingFilter = new StringBuffer("");
               reportingFilter.append(finalPeriod).append("\n").
                       append(finalSchedule).append("\n").
                       append(finalProgram).append("\n").
                       append(finalFacilityType).append("\n").
                       append(finalFacility).append("\n").
                       append(finalProduct).append("\n").
                       append(finalZone);
               return reportingFilter.toString();
           }
       };
    }
}
