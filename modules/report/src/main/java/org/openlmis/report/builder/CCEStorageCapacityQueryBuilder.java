package org.openlmis.report.builder;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class CCEStorageCapacityQueryBuilder {

    public static String getData(Map params){

        BEGIN();
        SELECT("facilities.name AS siteName" +
            " ,COALESCE(SUM(equipment_cold_chain_equipments.refrigeratorcapacity),0) AS refrigeratorCapacityCurrent" +
            " ,0 AS refrigeratorCapacityRequired" +
            " ,0 AS refrigeratorCapacityGap" +
            " ,COALESCE(SUM(equipment_cold_chain_equipments.freezercapacity),0) AS freezerCapacityCurrent" +
            " ,0 AS freezerCapacityRequired" +
            " ,0 AS freezerCapacityGap");
        FROM("facilities");
        JOIN("facility_types ON facility_types.id = facilities.typeid");
        JOIN("equipment_inventories ON equipment_inventories.facilityid = facilities.id");
        JOIN("equipments ON equipments.id = equipment_inventories.equipmentid");
        JOIN("equipment_cold_chain_equipments ON equipment_cold_chain_equipments.equipmentid = equipments.id");
        WHERE("facility_types.code = 'cvs'");
        GROUP_BY("facilities.id");
        return SQL();
    }
}
