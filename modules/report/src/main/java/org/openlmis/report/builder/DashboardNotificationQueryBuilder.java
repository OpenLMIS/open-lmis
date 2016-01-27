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
