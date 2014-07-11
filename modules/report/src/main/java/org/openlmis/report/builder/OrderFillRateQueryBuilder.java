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

public class OrderFillRateQueryBuilder {

    public static String getQuery(Map params) {

        Long userId = (Long) params.get("userId");

        params = (Map) (params.containsKey("param1") ? params.get("param1") : params);
        String zone   = params.containsKey("zone")? ((String[])params.get("zone"))[0]: "" ;
        String period = ((String[]) params.get("period"))[0];
        String facility = params.containsKey("facility") ? ((String[]) params.get("facility"))[0] : "";
        String facilityType = params.containsKey("facilityType") ? ((String[]) params.get("facilityType"))[0] : "";
        String program = ((String[]) params.get("program"))[0];
        String schedule = ((String[]) params.get("schedule"))[0];
        String product = ((String[]) params.get("product"))[0];
      //  String requisitionGroup = params.containsKey("requisitionGroup") ? ((String[]) params.get("requisitionGroup"))[0] : "";
        String productCategory = params.containsKey("productCategory") ? ((String[]) params.get("productCategory"))[0] : "";

        return getQueryString(params,zone, program, period, schedule, facility, product, facilityType, productCategory,userId);

    }

    private static void writePredicates(String zone, String program, String period, String schedule, String facility, String product, String facilityType, String productCategory) {

        if(zone != "" && !zone.endsWith( "undefined")){
            WHERE(" (gz.district_id = " + zone + " or gz.zone_id = " + zone + " or gz.region_id = " + zone +" or gz.parent = " + zone + " )");
        }

        if (facility != "" && !facility.endsWith("undefined")) {
            WHERE("facilityid = cast(" + facility + " as int4)");
        }
        if (product != null && !product.equals("undefined") && !product.isEmpty() && !product.equals("0") && !product.equals("-1")) {
            WHERE("productid = cast(" + product + " as int4)");
        }

        if (period != "" && !period.endsWith("undefined")) {
            WHERE("periodid = cast(" + period + " as int4)");
        }


        if (program != "" && !program.endsWith("undefined")) {
            WHERE("programid = cast(" + program + " as int4)");
        }

        if (schedule != "" && !schedule.endsWith("undefined")) {
            WHERE("scheduleid = cast(" + schedule + " as int4)");
        }
        if (facilityType != null && !facilityType.equals("undefined") && !facilityType.isEmpty() && !facilityType.equals("0") && !facilityType.equals("-1")) {
            WHERE("facilityTypeId = cast(" + facilityType + " as int4)");
        }
        if (productCategory != null && !productCategory.equals("undefined") && !productCategory.isEmpty() && !productCategory.equals("0") && !productCategory.equals("-1")) {
            WHERE("productCategoryId = cast(" + productCategory + " as int4)");
        }
    }

    private static String getQueryString(Map params, String zone,String program, String period, String schedule, String facility, String product, String facilityType, String productCategory,Long userId) {
        BEGIN();
        SELECT_DISTINCT("facilityname facility,fn_previous_period(programid,facilityid,periodid,\n" +
                "    productcode) as Approved,quantityreceived receipts ,productcode, product, CASE\n" +
                "    WHEN COALESCE(fn_previous_period(programid,facilityid,periodid,\n" +
                "    productcode), 0::numeric) = 0::numeric THEN 0::numeric\n" +
                "    ELSE COALESCE(quantityreceived,0 )/ COALESCE(fn_previous_period(programid,facilityid,periodid,\n" +
                "    productcode),0) * 100::numeric\n" +
                "                                     END AS item_fill_rate ");
        FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = vw_order_fill_rate.zoneId");
        WHERE("facilityid in (select facility_id from vw_user_facilities where user_id = cast(" + userId+ " as int4) and program_id = cast(" + program + " as int4))");
        WHERE(" status in ('RELEASED') and fn_previous_period(programid,facilityid,periodid,productcode)>0 and periodid = cast (" + period + " as int4)" +
                "and facilityid= cast(" + facility + " as int4) and programid = cast(" + program + " as int4)");

        writePredicates( zone,program, period, schedule, facility, product, facilityType, productCategory);
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
        String program = ((String[]) params.get("program"))[0];
        String period = ((String[]) params.get("period"))[0];
        String zone = params.containsKey("zone") ? ((String[]) params.get("zone"))[0] : "";
        String facility = params.containsKey("facility") ? ((String[]) params.get("facility"))[0] : "";
        String facilityType = params.containsKey("facilityType") ? ((String[]) params.get("facilityType"))[0] : "";
        String schedule = ((String[]) params.get("schedule"))[0];
        String product = ((String[]) params.get("product"))[0];
        String productCategory = ((String[]) params.get("productCategory"))[0];
       // String requisitionGroup = ((String[]) params.get("requisitionGroup"))[0];
        BEGIN();
        SELECT("count(totalproductsreceived) quantityreceived");
        FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = zoneId");
        WHERE("facilityid in (select facility_id from vw_user_facilities where user_id = cast(" + userId+ " as int4) and program_id = cast(" + program + " as int4))");
        WHERE("totalproductsreceived>0 and fn_previous_period(programid,facilityid,periodid,productcode) >0  and status in ('RELEASED') and periodId= cast(" + period + " as int4) and programId= cast(" + program + " as int4) and facilityId= cast(" + facility + " as int4)");
        writePredicates(zone, program, period, schedule, facility, product, facilityType,  productCategory);
        GROUP_BY("totalproductsreceived");
        String sql = SQL();
        return sql;
    }


    public static String getTotalProductsOrdered(Map params) {
        Long userId = (Long) params.get("userId");

        params = (Map) (params.containsKey("param1") ? params.get("param1") : params);
        String program = ((String[]) params.get("program"))[0];
        String period = ((String[]) params.get("period"))[0];
        String zone = params.containsKey("zone") ? ((String[]) params.get("zone"))[0] : "";
        String facility = params.containsKey("facility") ? ((String[]) params.get("facility"))[0] : "";
        String facilityType = params.containsKey("facilityType") ? ((String[]) params.get("facilityType"))[0] : "";
        String schedule = ((String[]) params.get("schedule"))[0];
        String product = ((String[]) params.get("product"))[0];
        String productCategory = ((String[]) params.get("productCategory"))[0];
        //String requisitionGroup = ((String[]) params.get("requisitionGroup"))[0];

        BEGIN();
        SELECT("count(fn_previous_period(programid,facilityid,periodid,productcode)) quantityapproved");
        FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = zoneId");
        WHERE("facilityid in (select facility_id from vw_user_facilities where user_id = cast(" + userId+ " as int4) and program_id = cast(" + program + " as int4))");
        WHERE("fn_previous_period(programid,facilityid,periodid,productcode) > 0  and status in ('RELEASED') and periodId= cast(" + period + " as int4) and programId= cast(" + program + " as int4) and facilityId= cast(" + facility + " as int4) ");
        writePredicates(zone, program, period, schedule, facility, product, facilityType,  productCategory);
        String sql = SQL();
        return sql;

    }


    public static String getSummaryQuery(Map params) {

        Long userId = (Long)params.get("userId");
        params = (Map) (params.containsKey("param1") ? params.get("param1") : params);
        String program = ((String[]) params.get("program"))[0];
        String period = ((String[]) params.get("period"))[0];
        String zone = params.containsKey("zone") ? ((String[]) params.get("zone"))[0] : "";
        String facility = params.containsKey("facility") ? ((String[]) params.get("facility"))[0] : "";
        String facilityType = params.containsKey("facilityType") ? ((String[]) params.get("facilityType"))[0] : "";
        String schedule = ((String[]) params.get("schedule"))[0];
        String product = ((String[]) params.get("product"))[0];
        String productCategory = ((String[]) params.get("productCategory"))[0];
        //String requisitionGroup = ((String[]) params.get("requisitionGroup"))[0];
        BEGIN();

        SELECT("count(totalproductsreceived) quantityreceived");
        FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = zoneId");
        WHERE("facilityid in (select facility_id from vw_user_facilities where user_id = cast(" + userId+ " as int4) and program_id = cast(" + program + " as int4))");
        WHERE("totalproductsreceived>0 and fn_previous_period(programid,facilityid,periodid,productcode)>0 and  status in ('RELEASED') and periodId= cast(" + period + " as int4) and programId= cast(" + program + " as int4) and facilityId= cast(" + facility + " as int4)");
        writePredicates(zone, program, period, schedule, facility, product, facilityType, productCategory);
        GROUP_BY("totalproductsreceived");
        String query = SQL();
        RESET();
        BEGIN();
        SELECT("count(fn_previous_period(programid,facilityid,periodid,productcode)) quantityapproved");
        FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = zoneId");
        WHERE("status in ('RELEASED') and fn_previous_period(programid,facilityid,periodid,productcode)>0 and periodId= cast(" + period + " as int4) and programId= cast(" + program + " as int4) and facilityId= cast(" + facility + " as int4)");
        writePredicates(zone, program, period, schedule, facility, product, facilityType, productCategory);
        query += " UNION " + SQL();
        return query;
    }
}
