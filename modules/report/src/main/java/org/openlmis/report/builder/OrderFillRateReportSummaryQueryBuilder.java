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

import org.openlmis.report.util.StringHelper;

import java.util.Map;

public class OrderFillRateReportSummaryQueryBuilder {

    public static String getOrderFillRateReportSummaryPagedData(Map params) {
        Map filterCriteria = (Map) params.get("filterCriteria");
        Long userId = (Long) params.get("userId");
        StringBuilder query = new StringBuilder();
        query.append("\n" +
                        "        WITH query as (  SELECT CASE when sum(totalproductsapproved)=0 THEN 0 ELSE\n" +
                        "                         ROUND(count(CASE WHEN totalproductsreceived>0 THEN 1 ELSE NULL END) * 100/\n" +
                        "                         count(CASE WHEN totalproductsapproved>0 THEN 1 ELSE NULL END),0) END Order_fill_rate\n" +
                        "                         FROM vw_order_fill_rate\n" +
                        "                         join vw_districts gz on gz.district_id = vw_order_fill_rate.zoneId\n" +
                        "                         JOIN facility_types ft on facilityTypeID=ft.id \n"
        ).append(writePredicates(filterCriteria, userId)).append("\n" +
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

        StringBuilder predicate = new StringBuilder();

        String facilityTypeId = StringHelper.isBlank(params, ("facilityType")) ? null : ((String[]) params.get("facilityType"))[0];
        String period = StringHelper.isBlank(params, ("period")) ? null : ((String[]) params.get("period"))[0];
        String program = StringHelper.isBlank(params, ("program")) ? null : ((String[]) params.get("program"))[0];
        String schedule = StringHelper.isBlank(params, ("schedule")) ? null : ((String[]) params.get("schedule"))[0];
        String zoneId = StringHelper.isBlank(params, ("zone")) ? null : ((String[]) params.get("zone"))[0];
        if (program != null && !program.equals("undefined") && !program.isEmpty() && !program.equals("0") && !program.equals("-1")) {
            predicate.append(" WHERE ");
            predicate.append(" programId = " + program);
        }

            if (predicate.length() > 0) {
                predicate.append(" and periodId = " + period);
            } else {
                predicate.append(" where periodId = " + period);
            }

        if (schedule != null && !schedule.equals("undefined") && !schedule.isEmpty() && !schedule.equals("0") && !schedule.equals("-1")) {
            if (predicate.length() > 0) {
                predicate.append(" and scheduleId = " + schedule);
            } else {
                predicate.append(" where scheduleId = " + period);
            }
        }
        if (predicate.length() > 0) {
            predicate.append(" and ");
        } else {
            predicate.append(" where ");
        }
        predicate.append(" facilityId in (select facility_id from vw_user_facilities where user_id = ").append(userId);
        if (program != null && !program.equals("undefined") && !program.isEmpty() && !program.equals("0") && !program.equals("-1")) {
            predicate.append(" and program_Id = ").append(program);
        }
        predicate.append(")");
        if (zoneId != null && !zoneId.equals("undefined") && !zoneId.isEmpty() && !zoneId.equals("0") && !zoneId.equals("-1")) {

            predicate.append(" and ( zone_id = ").append(zoneId).append(" or parent = ").append(zoneId).append(" or region_id = ").append(zoneId).append(" or district_id = ").append(zoneId).append(") ");
        }

        if (facilityTypeId != null && !facilityTypeId.equals("undefined") && !facilityTypeId.isEmpty() && !facilityTypeId.equals("0") && !facilityTypeId.equals("-1")) {

            predicate.append(" and facilityTypeId = ").append(facilityTypeId);
        }
        return predicate.toString();
    }
}
