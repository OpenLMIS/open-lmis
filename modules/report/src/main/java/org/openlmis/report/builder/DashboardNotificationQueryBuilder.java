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
        Long alertId = (Long) params.get("alertId");
        String detailTable = (String) params.get("tableName");
        BEGIN();
        SELECT("*");
        FROM("fn_get_notification_details(NULL::"+detailTable+","+alertId+")");

        String sql = SQL();

        return sql;

    }

}
