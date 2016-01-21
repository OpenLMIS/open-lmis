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

import org.openlmis.report.model.params.CCEStorageCapacityReportParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class CCEStorageCapacityQueryBuilder {

    public static String getData(Map params){

        CCEStorageCapacityReportParam filter = (CCEStorageCapacityReportParam)params.get("filterCriteria");
        BEGIN();
        SELECT("facilities.name AS siteName" +
            ", COALESCE(SUM(equipment_cold_chain_equipments.refrigeratorcapacity),0) AS refrigeratorCapacityCurrent" +
            ", 0 AS refrigeratorCapacityRequired" +
            ", 0 AS refrigeratorCapacityGap" +
            ", COALESCE(SUM(equipment_cold_chain_equipments.freezercapacity),0) AS freezerCapacityCurrent" +
            ", 0 AS freezerCapacityRequired" +
            ", 0 AS freezerCapacityGap");
        FROM("facilities");
        JOIN("facility_types ON facility_types.id = facilities.typeid");
        JOIN("equipment_inventories ON equipment_inventories.facilityid = facilities.id");
        JOIN("equipments ON equipments.id = equipment_inventories.equipmentid");
        JOIN("equipment_types ON equipment_types.id = equipments.equipmenttypeid AND equipment_types.iscoldchain = TRUE");
        JOIN("equipment_cold_chain_equipments ON equipment_cold_chain_equipments.equipmentid = equipments.id");
        writePredicates(filter);
        GROUP_BY("facilities.id");
        return SQL();
    }

    private static void writePredicates(CCEStorageCapacityReportParam filter) {
        String facilityLevel = filter.getFacilityLevel();
        if (facilityLevel.isEmpty()
            || facilityLevel.equalsIgnoreCase("cvs")
            || facilityLevel.equalsIgnoreCase("rvs")
            || facilityLevel.equalsIgnoreCase("dvs")) {
            WHERE("facility_types.code = #{filterCriteria.facilityLevel}");
        } else {
            WHERE("facility_types.code NOT IN ('cvs','rvs','dvs')");
        }

        if (!filter.getFacilityIds().isEmpty()) {
            WHERE("equipment_inventories.facilityid = ANY (#{filterCriteria.facilityIds}::INT[])");
        }

        WHERE("equipment_inventories.programid = #{filterCriteria.programId}");
    }
}