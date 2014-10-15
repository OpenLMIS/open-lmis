package org.openlmis.report.builder;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class PushedProductsQueryBuilder {


    public static String getQueryForPushedItems(Map params) {

        Long userId = (Long) params.get("userId");

        params = (Map) (params.containsKey("param1") ? params.get("param1") : params);
        String zone = params.containsKey("zone") ? ((String[]) params.get("zone"))[0] : "";
        String period = params.containsKey("period") ? ((String[]) params.get("period"))[0] : "";
        String facility = params.containsKey("facility") ? ((String[]) params.get("facility"))[0] : "";
        String facilityType = params.containsKey("facilityType") ? ((String[]) params.get("facilityType"))[0] : "";
        String program = params.containsKey("program") ? ((String[]) params.get("program"))[0] : "";
        String schedule = params.containsKey("schedule") ? ((String[]) params.get("schedule"))[0] : "";
        String product = params.containsKey("product") ? java.util.Arrays.toString((String[]) params.get("product")) : "0";//.replace("]", "}").replace("]", "{").replaceAll("\"","") : "";
        String productCategory = params.containsKey("productCategory") ? ((String[]) params.get("productCategory"))[0] : "";
        return getProductsPushedQuery(params, zone, program, period, schedule, facility, product, facilityType, productCategory, userId);

    }

    private static String getProductsPushedQuery(Map params, String zone, String program, String period, String schedule, String facility, String product, String facilityType, String productCategory, Long userId) {

        BEGIN();
        SELECT_DISTINCT("product,productcode,sum(fn_previous_period(programid,facilityid,periodid,productcode)) approved,sum(quantityreceived) receipts");
        FROM("vw_order_fill_rate join vw_districts gz on gz.district_id = zoneId");
        WHERE("facilityid in (select facility_id from vw_user_facilities where user_id = cast(" + userId + " as int4) and program_id = cast(" + program + " as int4)) ");
        WHERE(" fn_previous_period(programid,facilityid,periodid,productcode) = 0 and quantityreceived > 1\n" +
                " and  status in ('RELEASED') and periodid = cast (" + period + " as int4) " +
                "and facilityId= cast(" + facility + " as int4) and programid = cast(" + program + " as int4)");
        writePredicates(zone, program, period, schedule, facility, product, facilityType, productCategory);
        GROUP_BY("product,productcode");
        ORDER_BY(QueryHelpers.getSortOrder(params, "product"));
        String sql = SQL();
        return sql;
    }

    private static void writePredicates(String zone, String program, String period, String schedule, String facility, String product, String facilityType, String productCategory) {

        if (zone != null && !zone.equals("undefined") && !zone.isEmpty() && !zone.equals("0") && !zone.equals("-1")) {
            WHERE(" (gz.district_id = " + zone + " or gz.zone_id = " + zone + " or gz.region_id = " + zone + " or gz.parent = " + zone + " )");
        }

        if (facility != "" && !facility.endsWith("undefined")) {
            WHERE("facilityid = cast(" + facility + " as int4)");
        }

        if (!product.equals("0") && !product.equals("-1") && product != null && !product.isEmpty() && !product.equals("{}") && !product.equals("{0}") && !product.endsWith("undefined")) {
            WHERE("productId =  ANY(array" + product + "::INT[]) ");
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

}
