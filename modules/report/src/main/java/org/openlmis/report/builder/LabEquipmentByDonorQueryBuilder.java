/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.builder;


import org.openlmis.report.model.params.LabEquipmentListReportParam;
import org.openlmis.report.util.StringHelper;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;

public class LabEquipmentByDonorQueryBuilder {

    public static String SelectFilteredSortedPagedRecords(Map params){

        LabEquipmentListReportParam filter = (LabEquipmentListReportParam) params.get("filterCriteria");
        BEGIN();
        SELECT("*");
        FROM("vw_equipment_list_by_donor");
        writePredicates(filter);
        ORDER_BY("facilityName, equipment_name");
        return SQL();
    }

    private static void writePredicates(LabEquipmentListReportParam filter) {


        WHERE("facility_id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.programId})");
        WHERE("programid  = #{filterCriteria.programId}");

        if (filter != null) {

            if (filter.getFacilityTypeId() != 0) {
                WHERE("ftype_id = #{filterCriteria.facilityTypeId}");
            }

            if (filter.getZoneId() != 0 && filter.getZoneId() != -1) {
                WHERE("(district_id = #{filterCriteria.zoneId} or zone_id = #{filterCriteria.zoneId} or region_id = #{filterCriteria.zoneId} or parent = #{filterCriteria.zoneId})");
            }

            if (filter.getFacilityId() != 0) {
                WHERE("facility_id = #{filterCriteria.facilityId}");
            }

            if (filter.getEquipmentTypeId() != 0) {
                WHERE("equipmenttype_id = #{filterCriteria.equipmentTypeId}");
            }

            if (filter.getEquipmentId() != 0) {
                WHERE("equipment_id = #{filterCriteria.equipmentId}");
            }

            if (filter.getDonor() != 0) {
                WHERE("donorid = #{filterCriteria.donor}");
            }
        }
    }

}
