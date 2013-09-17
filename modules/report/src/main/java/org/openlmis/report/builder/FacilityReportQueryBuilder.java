/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */
package org.openlmis.report.builder;

import org.openlmis.report.model.filter.FacilityReportFilter;
import org.openlmis.report.model.sorter.FacilityReportSorter;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class FacilityReportQueryBuilder {

    public static String SelectFilteredSortedPagedFacilitiesSql(Map params){//ReportData filterCriteria,ReportData SortCriteria ,int page,int pageSize) {

        FacilityReportFilter filter  = (FacilityReportFilter)params.get("filterCriteria");
        FacilityReportSorter sorter = (FacilityReportSorter)params.get("SortCriteria");
        BEGIN();
        SELECT("F.id, F.code, F.name, F.active as active, FT.name as facilityType, GZ.name as region, FO.code as owner,F.latitude::text ||',' ||  F.longitude::text  ||', ' || F.altitude::text gpsCoordinates,F.mainphone as phoneNumber, F.fax as fax, U.firstName || ' ' || U.lastName contact ");
        //FROM("facility_types FT");
        FROM("facilities F");
        JOIN("facility_types FT on FT.id = F.typeid");
        LEFT_OUTER_JOIN("geographic_zones GZ on GZ.id = F.geographiczoneid");
        LEFT_OUTER_JOIN("facility_operators FO on FO.id = F.operatedbyid");
        LEFT_OUTER_JOIN("requisition_group_members ON f.id = requisition_group_members.facilityid");
        LEFT_OUTER_JOIN("requisition_groups ON requisition_groups.id = requisition_group_members.requisitiongroupid");
        LEFT_OUTER_JOIN("Users U on U.facilityId = F.id ");
        if(filter != null){
            if (filter.getStatusId() != null) {
                WHERE("F.active = " + filter.getStatusId().toString());
            }
            if (filter.getZoneId() != 0) {
                WHERE("F.geographiczoneid = #{filterCriteria.zoneId}");
            }
            if (filter.getFacilityTypeId() != 0) {
                WHERE("F.typeid = " + filter.getFacilityTypeId());
            }
            if(filter.getRgId() != 0){
                WHERE("requisition_groups.id = "+ filter.getRgId());
            }
        }

        if(sorter != null){

            if(sorter.getFacilityName().equalsIgnoreCase("asc")){
                ORDER_BY("F.name asc");
            }
            if(sorter.getFacilityName().equalsIgnoreCase("desc")){
                ORDER_BY("F.name desc");
            }

            if(sorter.getCode().equalsIgnoreCase("asc")){
                ORDER_BY("F.code asc");
            }
            if(sorter.getCode().equalsIgnoreCase("desc")){
                ORDER_BY("F.code desc");
            }

            if(sorter.getFacilityType().equalsIgnoreCase("asc")){
                ORDER_BY("F.typeid asc");
            }
            if(sorter.getFacilityType().equalsIgnoreCase("desc")){
                ORDER_BY("F.typeid desc");
            }
        }


        return SQL();
    }

}
