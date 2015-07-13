/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
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
