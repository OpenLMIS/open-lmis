/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */
package org.openlmis.report.builder;

import org.openlmis.report.model.params.FacilityReportParam;
import org.openlmis.report.model.sorter.FacilityReportSorter;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class FacilityReportQueryBuilder {

    public static String SelectFilteredSortedPagedFacilitiesSql(Map params){//ReportData filterCriteria,ReportData SortCriteria ,int page,int pageSize) {

        FacilityReportParam filter  = (FacilityReportParam)params.get("filterCriteria");
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
