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
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;


public class ColdChainEquipmentReportQueryBuilder
{
    public static String getQuery(Map params)
    {
        Map filterCriteria = (Map)params.get("filterCriteria");
        Long userId = (Long) params.get("userId");

        BEGIN();
        SELECT("*");
        FROM("vw_cold_chain_equipment");

        //TODO: Obviate the need for the below code (which is hokey).
        if(filterCriteria.containsKey("sortBy"))
        {
            String sortBy = ((String[])filterCriteria.get("sortBy"))[0];
            String order = ((String[])filterCriteria.get("order"))[0];
            if(sortBy != null && !sortBy.trim().isEmpty())
            {
                if(sortBy.contains("Capacity") || sortBy.equals("yearOfInstallation"))
                    sortBy = sortBy.toLowerCase();
                else if(sortBy.contains("geozoneHierarchy"))
                    sortBy = "geozoneHierarchy";
                ORDER_BY("\"" + sortBy + "\"" + " " + order);
            }
        }

        return SQL();
    }

}
