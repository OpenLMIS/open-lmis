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

public class OrderFillRateQueryBuilder {

    public static String getQuery(Map params) {

        Long userId = (Long) params.get("userId");

        params = (Map) (params.containsKey("param1") ? params.get("param1") : params);
        String zone = params.containsKey("zone") ? ((String[]) params.get("zone"))[0] : "";
        String period = params.containsKey("period") ? ((String[]) params.get("period"))[0] : "";
        String facility = params.containsKey("facility") ? ((String[]) params.get("facility"))[0] : "";
        String facilityType = params.containsKey("facilityType") ? ((String[]) params.get("facilityType"))[0] : "";
        String program = params.containsKey("program") ? ((String[]) params.get("program"))[0] : "";
        String schedule = params.containsKey("schedule") ? ((String[]) params.get("schedule"))[0] : "";
        String product = params.containsKey("products") ? java.util.Arrays.toString((String[]) params.get("products")) : "0";//.replace("]", "}").replace("]", "{").replaceAll("\"","") : "";
        String productCategory = params.containsKey("productCategory") ? ((String[]) params.get("productCategory"))[0] : "";

        return getQueryString(params, zone, program, period, schedule, facility, product, facilityType, productCategory, userId);

    }

    private static void writePredicates(String zone, String program, String period, String schedule, String facility, String product, String facilityType, String productCategory) {

            WHERE("programid = cast(" + program + " as int4)");

            WHERE("periodid = cast(" + period + " as int4)");

            WHERE("scheduleid = cast(" + schedule + " as int4)");

        if (zone != null && !zone.equals("undefined") && !zone.isEmpty() && !zone.equals("0") && !zone.equals("-1")) {
            WHERE(" (gz.district_id = " + zone + " or gz.zone_id = " + zone + " or gz.region_id = " + zone + " or gz.parent = " + zone + " )");
        }

        if (!facility.equals("0") &&facility != "" && !facility.endsWith("undefined")) {
            WHERE("facilityid = cast(" + facility + " as int4)");
        }

        if (!product.equals("0") && !product.equals("-1") && product != null && !product.isEmpty() && !product.equals("[0]") && !product.equals("{0}")&& !product.equals("[-1]") && !product.endsWith("undefined") && !product.isEmpty()) {
            WHERE("productId =  ANY(array" + product + "::INT[]) ");
        }

        if (facilityType != null && !facilityType.equals("undefined") && !facilityType.isEmpty() && !facilityType.equals("0") && !facilityType.equals("-1")) {
            WHERE("facilityTypeId = cast(" + facilityType + " as int4)");
        }

        if (productCategory != null && !productCategory.equals("undefined") && !productCategory.isEmpty() && !productCategory.equals("0") && !productCategory.equals("-1")) {
            WHERE("productCategoryId = cast(" + productCategory + " as int4)");
        }
    }

    private static String getQueryString(Map params, String zone, String program, String period, String schedule, String facility, String product, String facilityType, String productCategory, Long userId) {
        BEGIN();
        SELECT_DISTINCT("facilityname facility,quantityapproved as Approved,quantityreceived receipts ,productcode, product, " +
                " CASE WHEN COALESCE(quantityapproved, 0::numeric) = 0::numeric THEN 0::numeric\n" +
                "    ELSE COALESCE(quantityreceived,0 )/ COALESCE(quantityapproved,0) * 100::numeric\n" +
                "                                     END AS item_fill_rate ");
        FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = vw_order_fill_rate.zoneId");
        WHERE("facilityid in (select facility_id from vw_user_facilities where user_id = cast(" + userId + " as int4) and program_id = cast(" + program + " as int4))");
        WHERE(" status in ('RELEASED') and totalproductsapproved > 0 ");

        writePredicates(zone, program, period, schedule, facility, product, facilityType, productCategory);
        GROUP_BY("product, approved, \n" +
                "  quantityreceived,  productcode, \n" +
                "  facilityname ");
        ORDER_BY(QueryHelpers.getSortOrder(params, "facilityname"));
        String sql = SQL();

        return sql;
    }

    public static String getTotalProductsReceived(Map params) {

        Long userId = (Long) params.get("userId");
        params = (Map) (params.containsKey("param1") ? params.get("param1") : params);
        String program = params.containsKey("program") ? ((String[]) params.get("program"))[0] : "";
        String period = params.containsKey("period") ? ((String[]) params.get("period"))[0] : "";
        String zone = params.containsKey("zone") ? ((String[]) params.get("zone"))[0] : "";
        String facility = params.containsKey("facility") ? ((String[]) params.get("facility"))[0] : "";
        String facilityType = params.containsKey("facilityType") ? ((String[]) params.get("facilityType"))[0] : "";
        String schedule = params.containsKey("schedule") ? ((String[]) params.get("schedule"))[0] : "";
        String product = "";
        String productCategory = "";
        BEGIN();
        SELECT("count(totalproductsreceived) quantityreceived");
        FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = zoneId");
        WHERE("facilityid in (select facility_id from vw_user_facilities where user_id = cast(" + userId + " as int4) and program_id = cast(" + program + " as int4))");
        WHERE("totalproductsreceived>0 and totalproductsapproved >0  and status in ('RELEASED') and periodId= cast(" + period + " as int4) and programId= cast(" + program + " as int4) and facilityId= cast(" + facility + " as int4)");
        writePredicates(zone, program, period, schedule, facility, product, facilityType, productCategory);
        GROUP_BY("totalproductsreceived");
        String sql = SQL();
        return sql;
    }


    public static String getTotalProductsOrdered(Map params) {
        Long userId = (Long) params.get("userId");

        params = (Map) (params.containsKey("param1") ? params.get("param1") : params);
        String program = params.containsKey("program") ? ((String[]) params.get("program"))[0] : "";

        String period = params.containsKey("period") ? ((String[]) params.get("period"))[0] : "";
        String zone = params.containsKey("zone") ? ((String[]) params.get("zone"))[0] : "";
        String facility = params.containsKey("facility") ? ((String[]) params.get("facility"))[0] : "";
        String facilityType = params.containsKey("facilityType") ? ((String[]) params.get("facilityType"))[0] : "";
        String schedule = params.containsKey("schedule") ? ((String[]) params.get("schedule"))[0] : "";
        String product = "";
        String productCategory = "";
        BEGIN();
        SELECT("count(totalproductsapproved) quantityapproved");
        FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = zoneId");
        WHERE("facilityid in (select facility_id from vw_user_facilities where user_id = cast(" + userId + " as int4) and program_id = cast(" + program + " as int4))");
        WHERE("totalproductsapproved > 0  and status in ('RELEASED') and periodId= cast(" + period + " as int4) and programId= cast(" + program + " as int4) and facilityId= cast(" + facility + " as int4) ");
        writePredicates(zone, program, period, schedule, facility, product, facilityType, productCategory);
        String sql = SQL();
        return sql;

    }


    public static String getSummaryQuery(Map params) {

        Long userId = (Long) params.get("userId");
        params = (Map) (params.containsKey("param1") ? params.get("param1") : params);
        String program = params.containsKey("program") ? ((String[]) params.get("program"))[0] : "";

        String period = params.containsKey("period") ? ((String[]) params.get("period"))[0] : "";
        String zone = params.containsKey("zone") ? ((String[]) params.get("zone"))[0] : "";
        String facility = params.containsKey("facility") ? ((String[]) params.get("facility"))[0] : "";
        String facilityType = params.containsKey("facilityType") ? ((String[]) params.get("facilityType"))[0] : "";
        String schedule = params.containsKey("schedule") ? ((String[]) params.get("schedule"))[0] : "";
        String product = "";
        String productCategory = "";

        BEGIN();

        SELECT("count(totalproductsreceived) quantityreceived");
        FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = zoneId");
        WHERE("facilityid in (select facility_id from vw_user_facilities where user_id = cast(" + userId + " as int4) and program_id = cast(" + program + " as int4))");
        WHERE("totalproductsreceived>0 and totalproductsapproved > 0 and  status in ('RELEASED') and periodId= cast(" + period + " as int4) and programId= cast(" + program + " as int4) and facilityId= cast(" + facility + " as int4)");
        writePredicates(zone, program, period, schedule, facility, product, facilityType, productCategory);
        GROUP_BY("totalproductsreceived");
        String query = SQL();
        RESET();
        BEGIN();
        SELECT("count(totalproductsapproved) quantityapproved");
        FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = zoneId");
        WHERE("status in ('RELEASED') and totalproductsapproved > 0 and periodId= cast(" + period + " as int4) and programId= cast(" + program + " as int4) and facilityId= cast(" + facility + " as int4)");
        writePredicates(zone, program, period, schedule, facility, product, facilityType, productCategory);
        query += " UNION " + SQL();
        return query;
    }
}