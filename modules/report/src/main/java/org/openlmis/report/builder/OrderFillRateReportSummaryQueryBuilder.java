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

import org.openlmis.report.util.StringHelper;

import java.util.Map;

public class OrderFillRateReportSummaryQueryBuilder {

    public static String getOrderFillRateReportSummaryPagedData(Map params){
        Map filterCriteria = (Map) params.get("filterCriteria");
        Long userId = (Long) params.get("userId");
        StringBuilder query = new StringBuilder();
        query.append("\n" +
                "        WITH query as (  SELECT CASE when sum(totalproductsapproved)=0 THEN 0 ELSE\n" +
                "                         ROUND(count(CASE WHEN totalproductsreceived>0 THEN 1 ELSE NULL END) * 100/\n" +
                "                         count(CASE WHEN totalproductsapproved>0 THEN 1 ELSE NULL END),0) END Order_fill_rate\n" +
                "                         FROM vw_order_fill_rate_Summary\n" +
                "                         join vw_districts gz on gz.district_id = vw_order_fill_rate_Summary.zoneId\n" +
                "                         JOIN facility_types ft on facilityTypeID=ft.id \n"
                  ).append(writePredicates(filterCriteria,userId)).append("\n"+
                "                         and totalproductsapproved>0\n" +
                "                        GROUP BY  facilityId,zonename,facilityname,ft.name,totalproductsapproved\n" +
                "                        )\n" +
                "                        select X.* FROM(\n" +
                "                        select count(*) TotalOrderFillRate ,'A' as OrderFillRateStatus from query \n" +
                "                        where order_fill_rate between 75 and 100\n" +
                "                        UNION ALL\n" +
                "                        SELECT COUNT(*) TotalOrderFillRate, 'M' as OrderFillRateStatus FROM query  \n" +
                "                        where order_fill_rate between 50 and 74.9\n" +
                "                        UNION ALL\n" +
                "                        SELECT COUNT(*) TotalOrderFillRate, 'L' as OrderFillRateStatus FROM query \n" +
                "                         where order_fill_rate between 1 and 49.9\n" +
                "                        )x");

        return query.toString();
    }

    private static String writePredicates(Map params, Long userId) {

        StringBuilder predicate = new StringBuilder(" WHERE ");

        String facilityTypeId = StringHelper.isBlank(params, ("facilityType")) ? null : ((String[]) params.get("facilityType"))[0];
        String period = StringHelper.isBlank(params, ("period")) ? null : ((String[]) params.get("period"))[0];
        String program = StringHelper.isBlank(params, ("program")) ? null : ((String[]) params.get("program"))[0];
        String schedule = StringHelper.isBlank(params, ("schedule")) ? null : ((String[]) params.get("schedule"))[0];
        String zoneId = StringHelper.isBlank(params, ("zone")) ? null : ((String[]) params.get("zone"))[0];

        predicate.append(" programId = " + program);
        predicate.append(" and periodId = " + period);
        predicate.append(" and scheduleId = " + schedule);
        predicate.append(" and facilityId in (select facility_id from vw_user_facilities where user_id = ").append(userId).append(" and program_Id = ").append(program).append(")");
        if (zoneId != null && !zoneId.equals("undefined") && !zoneId.isEmpty() && !zoneId.equals("0") && !zoneId.equals("-1")) {

            predicate.append(" and ( zone_id = ").append(zoneId).append(" or parent = ").append(zoneId).append(" or region_id = ").append(zoneId).append(" or district_id = ").append(zoneId).append(") ");
        }

        if (facilityTypeId != null && !facilityTypeId.equals("undefined") && !facilityTypeId.isEmpty() && !facilityTypeId.equals("0") && !facilityTypeId.equals("-1")) {

            predicate.append(" and facilityTypeId = ").append(facilityTypeId);
        }
        return predicate.toString();
    }
}
