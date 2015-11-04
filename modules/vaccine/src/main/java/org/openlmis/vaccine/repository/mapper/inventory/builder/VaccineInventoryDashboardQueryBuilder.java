package org.openlmis.vaccine.repository.mapper.inventory.builder;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/**
 * Created by chrispinus on 10/29/15.
 */
public class VaccineInventoryDashboardQueryBuilder {

    public static final String getNonFunctionalAlerts(Map params) {

        String facilities = (String) params.get("facilities");

        BEGIN();
        SELECT("*");
        FROM("alert_equipment_nonfunctional");
        WHERE("facilityid IN " + facilities);

        String sql = SQL();
        return sql;

    }
}
