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
