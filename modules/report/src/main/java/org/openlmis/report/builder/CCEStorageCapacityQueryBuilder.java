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
        JOIN("equipment_cold_chain_equipments ON equipment_cold_chain_equipments.equipmentid = equipments.id");
        writePredicates(filter);
        GROUP_BY("facilities.id");
        return SQL();
    }

    private static void writePredicates(CCEStorageCapacityReportParam filter) {
        String facilityLevel = filter.getFacilityLevel();
        if (!facilityLevel.isEmpty()) {
            if (facilityLevel.equalsIgnoreCase("cvs")
                || facilityLevel.equalsIgnoreCase("rvs")
                || facilityLevel.equalsIgnoreCase("dvs")) {
                WHERE("facility_types.code = #{filterCriteria.facilityLevel}");
            } else {
                WHERE("facility_types.code NOT IN ('cvs','rvs','dvs')");
            }
        }
        if (!filter.getFacilityIds().isEmpty()) {
            WHERE("equipment_inventories.facilityid = ANY (#{filterCriteria.facilityIds}::INT[])");
        }
    }
}