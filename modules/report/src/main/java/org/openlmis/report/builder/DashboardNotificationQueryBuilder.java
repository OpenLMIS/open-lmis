package org.openlmis.report.builder;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/**
 * User: Issa
 * Date: 4/22/14
 * Time: 2:35 PM
 */
public class DashboardNotificationQueryBuilder {

    public static final String getNotificationDetails(Map params){

        Long userId = (Long) params.get("userId");
        Long programId = (Long) params.get("programId");
        Long periodId = (Long) params.get("periodId");
        Long zoneId = (Long) params.get("zoneId");
        String detailTable = (String) params.get("tableName");

        BEGIN();
        SELECT("*");
        FROM("fn_get_notification_details(NULL::"+detailTable+","+userId+","+programId+","+periodId+","+zoneId+")");

        String sql = SQL();

        return sql;

    }

    public static final String getStockedOutNotificationDetails(Map params){
        Long userId = (Long) params.get("userId");
        Long programId = (Long) params.get("programId");
        Long periodId = (Long) params.get("periodId");
        Long zoneId = (Long) params.get("zoneId");
        Long productId = (Long) params.get("productId");
        String detailTable = (String) params.get("tableName");

        BEGIN();
        SELECT("*");
        FROM("fn_get_stocked_out_notification_details(NULL::"+detailTable+","+userId+","+programId+","+periodId+","+zoneId+","+productId+")");

        String sql = SQL();

        return sql;

    }

}
