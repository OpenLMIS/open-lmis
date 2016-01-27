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


import org.openlmis.core.domain.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class LabEquipmentStatusByLocationQueryBuilder {

    public static String getFacilityEquipmentStatusGeoData(Map<String, Long> params) {

        BEGIN();
        SELECT("facility_id, facility_code, facility_name , latitude, longitude, " +
                " SUM(CASE" +
                "            WHEN equipment_status = 'Partially Operational' THEN 1::int" +
                "            ELSE 0::int" +
                "        END) AS total_partially_operational," +
                "        SUM(CASE" +
                "            WHEN equipment_status = 'Not Operational' THEN 1::int" +
                "            ELSE 0::int" +
                "        END) AS total_not_operational," +
                "        SUM(CASE" +
                "            WHEN equipment_status = 'Fully Operational' THEN 1::int" +
                "            ELSE 0::int" +
                "        END) AS total_fully_operational");
        FROM("vw_lab_equipment_status");
        writePredicates(params);
        GROUP_BY("facility_id, facility_code, facility_name, latitude, longitude");
        ORDER_BY("facility_name");
        return SQL();
    }

    public static String getFacilityEquipmentStatusGeoSummaryData(Map<String, Long> filterCriteria){

        String sql = " SELECT total, equipment_status " +
                " FROM ( " +
                " SELECT count(facility_id) total," +
                "  CASE " +
                "    WHEN total_partially_operational + total_not_operational = 0 and total_fully_operational > 0 THEN 'Fully Operational' " +
                "    WHEN total_partially_operational + total_fully_operational = 0 and total_not_operational > 0 THEN 'Not Operational'" +
                "    ELSE 'Partially Operational'" +
                "  END AS equipment_status" +
                " FROM  (SELECT facility_id, facility_code, facility_name , latitude, longitude, " +
                " SUM(CASE" +
                "    WHEN equipment_status = 'Partially Operational' THEN 1::int" +
                "    ELSE 0::int" +
                " END) AS total_partially_operational," +
                " SUM(CASE" +
                "    WHEN equipment_status = 'Not Operational' THEN 1::int" +
                "    ELSE 0::int" +
                " END) AS total_not_operational," +
                " SUM(CASE" +
                "    WHEN equipment_status = 'Fully Operational' THEN 1::int" +
                "    ELSE 0::int" +
                " END) AS total_fully_operational" +
                "  FROM vw_lab_equipment_status" +
                writeSummaryPredicates(filterCriteria) +
                "  GROUP BY facility_id, facility_code, facility_name, latitude, longitude" +
                "  ORDER BY facility_name" +
                ") AS df group by equipment_status" +
                ") AS percentage";

        return sql;
    }

    public static String getFacilitiesEquipmentsData(Map<String, Long> params){

        BEGIN();
        SELECT("facility_id, facility_code, facility_name, serial_number, equipment_name, equipment_status");
        FROM("vw_lab_equipment_status");
        writePredicates(params);
        ORDER_BY("facility_name");
      return SQL();
    }

    public static String getFacilitiesByEquipmentStatus(Map<String, Object> params){

       String sql = "SELECT * FROM (  " +
                "SELECT    facility_id, facility_code, facility_name, disrict, facility_type, " +
                        "   CASE   " +
                        "     WHEN total_partially_operational + total_not_operational = 0 and total_fully_operational > 0 THEN 'Fully Operational'  " +
                        "     WHEN total_partially_operational + total_fully_operational = 0 and total_not_operational > 0 THEN 'Not Operational'  " +
                        "     ELSE 'Partially Operational'  " +
                        "   END AS equipment_status  " +
                        "FROM  (SELECT   " +
                        "  facility_id, facility_code, facility_name, disrict, facility_type, " +
                        "  SUM(CASE  WHEN equipment_status = 'Partially Operational' THEN 1::int   ELSE 0::int END) AS total_partially_operational,  " +
                        "  SUM(CASE  WHEN equipment_status = 'Not Operational' THEN 1::int  ELSE 0::int END) AS total_not_operational,  " +
                        "  SUM(CASE WHEN equipment_status = 'Fully Operational' THEN 1::int ELSE 0::int END) AS total_fully_operational  " +
                        "       FROM vw_lab_equipment_status    " +
               writeFacilitiesByEquipmentStatusPredicates(params, null) +
                        "       GROUP BY facility_id, facility_code, facility_name, latitude, longitude, disrict, facility_type  " +
                        "       ORDER BY facility_name  " +
                        "     ) AS temp ) AS FES "+
        writeFacilitiesByEquipmentStatusPredicates(params, "status") ;
        return sql;
    }

    private static String writeFacilitiesByEquipmentStatusPredicates(Map<String, Object> filterCriteria, String getStatus){

        Long facilityId = !filterCriteria.containsKey("facility") ? 0L : (Long)filterCriteria.get("facility");
        Long facilityTypeId = !filterCriteria.containsKey("facilityType") ? 0L : (Long) filterCriteria.get("facilityType");
        Long program = !filterCriteria.containsKey("program") ? 0L : (Long) filterCriteria.get("program");
        Long zone = !filterCriteria.containsKey("zone") ? 0L : (Long) filterCriteria.get("zone");
        Long userId = !filterCriteria.containsKey("userId") ? 0L : (Long) filterCriteria.get("userId");
        String status = !filterCriteria.containsKey("status") ? null : filterCriteria.get("status").toString();
        Long equipmentType = !filterCriteria.containsKey("equipmentType") ? 0L : (Long) filterCriteria.get("equipmentType");
        Long equipment = !filterCriteria.containsKey("equipment") ? 0L : (Long) filterCriteria.get("equipment");

        String sql = " WHERE 0=0 ";

        if(getStatus == null) {

            sql = sql +  " AND facility_id in (select facility_id from vw_user_facilities where user_id = " + userId + " and program_id = " + program + " )";

            sql = sql + " AND programid = " + program;

            if (zone != 0 && zone != -1)
                sql = sql + " AND (district_id =" + zone + " or zone_id = " + zone + " or region_id =" + zone + " or parent = " + zone + ")";

            if (facilityTypeId != 0)
                sql = sql + " AND ftype_id = " + facilityTypeId;

            if (facilityId != 0)
                sql = sql + " AND facility_id = " + facilityId;

            if(equipmentType != 0)
                sql = sql + " AND equipmenttype_id = " + equipmentType;

            if(equipment != 0)
                sql = sql + " AND equipment_id = "+equipment;
        }
        else
            sql = sql + " AND FES.equipment_status = '" + status+"'";

        return sql;
    }

    private static void writePredicates(Map<String, Long> filterCriteria) {

        Long facilityId = !filterCriteria.containsKey("facility") ? 0L : filterCriteria.get("facility");
        Long facilityTypeId = !filterCriteria.containsKey("facilityType") ? 0L :Integer.parseInt(filterCriteria.get("facilityType").toString());
        Long program = !filterCriteria.containsKey("program") ? 0L : Integer.parseInt(filterCriteria.get("program").toString());
        Long zone = !filterCriteria.containsKey("zone") ? 0L : Integer.parseInt(filterCriteria.get("zone").toString());
        Long userId = !filterCriteria.containsKey("userId") ? 0L : Integer.parseInt(filterCriteria.get("userId").toString());
        Long equipmentType = !filterCriteria.containsKey("equipmentType") ? 0L : (Long) filterCriteria.get("equipmentType");
        Long equipment = !filterCriteria.containsKey("equipment") ? 0L : (Long) filterCriteria.get("equipment");

        WHERE(" facility_id in (select facility_id from vw_user_facilities where user_id = "+ userId +" and program_id = "+program+" )");

        WHERE("programid = "+program);

        if(zone != 0 && zone != -1)
            WHERE("(district_id ="+zone+" or zone_id = "+zone+" or region_id ="+zone+" or parent = "+zone+")");

        if(facilityTypeId != 0)
            WHERE("ftype_id = "+facilityTypeId);

        if(facilityId != 0)
            WHERE("facility_id = "+facilityId);

        if(equipmentType != 0)
            WHERE("equipmenttype_id = "+equipmentType);

        if(equipment != 0)
            WHERE("equipment_id = "+equipment);
    }

    private static String writeSummaryPredicates(Map<String, Long> filterCriteria) {

        Long facilityId = !filterCriteria.containsKey("facility") ? 0L : filterCriteria.get("facility");
        Long facilityTypeId = !filterCriteria.containsKey("facilityType") ? 0L :Integer.parseInt(filterCriteria.get("facilityType").toString());
        Long program = !filterCriteria.containsKey("program") ? 0L : Integer.parseInt(filterCriteria.get("program").toString());
        Long zone = !filterCriteria.containsKey("zone") ? 0L : Integer.parseInt(filterCriteria.get("zone").toString());
        Long userId = !filterCriteria.containsKey("userId") ? 0L : Integer.parseInt(filterCriteria.get("userId").toString());
        Long equipmentType = !filterCriteria.containsKey("equipmentType") ? 0L : (Long) filterCriteria.get("equipmentType");
        Long equipment = !filterCriteria.containsKey("equipment") ? 0L : (Long) filterCriteria.get("equipment");

        String sql = " where facility_id in (select facility_id from vw_user_facilities where user_id = "+ userId +" and program_id = "+program+" )";

        sql = sql +" AND programid = " + program;

        if(zone != 0 && zone != -1)
            sql = sql + " AND (district_id ="+zone+" or zone_id = "+zone+" or region_id ="+zone+" or parent = "+zone+")";

        if(facilityTypeId != 0)
            sql = sql + " AND ftype_id = "+facilityTypeId;

        if(facilityId != 0)
            sql = sql + " AND facility_id = "+facilityId;

        if(equipmentType != 0)
            sql = sql + " AND equipmenttype_id = " + equipmentType;

        if(equipment != 0)
            sql = sql + " AND equipment_id = "+equipment;

        return sql;
    }

}