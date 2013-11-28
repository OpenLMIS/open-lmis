package org.openlmis.report.builder;

import org.openlmis.report.model.filter.OrderFillRateReportFilter;
import org.openlmis.report.model.report.OrderFillRateReport;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/**
 * Created with IntelliJ IDEA.
 * User: Hassan
 * Date: 10/28/13
 * Time: 6:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class OrderFillRateQueryBuilder {

    public static String getOrderFillRateQuery(Map params){
        return "select (count(receipts)/count(approved)) * 100 as ORDER_FILL_RATE" +
                "from vw_order_fill_rate where approved !=0";
    }




    public static String getQuery(Map params){

        OrderFillRateReportFilter filter  = (OrderFillRateReportFilter)params.get("filterCriteria");
        Map sortCriteria = (Map) params.get("SortCriteria");
        BEGIN();
        SELECT("distinct supplyingfacility as supplyingFacility ,CASE  WHEN (approved::numeric > 0) AND (err_qty_received > 0) THEN round((receipts)::numeric  / (approved)::numeric * 100::numeric, 0)\n" +
                "            ELSE 0::numeric\n" +
                "        END AS item_fill_rate,category,facility,err_qty_received ,productcode,facilitytype as facilityType ,receipts,approved , product");

        FROM("vw_order_fill_rate");
        writePredicates(filter);
        ORDER_BY(QueryHelpers.getSortOrder(sortCriteria, OrderFillRateReport.class, "supplyingfacility asc,facilitytype asc, facility asc, product asc"));
        String sql = SQL();
        return sql;

    }
    private static void writePredicates(OrderFillRateReportFilter filter){

        WHERE("periodid = cast( #{filterCriteria.periodId} as int4) ");
        WHERE("scheduleid = cast(#{filterCriteria.scheduleId} as int4) ");//required param

        if(filter != null){

            if (filter.getProgramId() != 0 && filter.getProgramId() != -1) {
                WHERE("programid = cast(#{filterCriteria.programId} as int4)");
            }
            if (filter.getFacilityTypeId() != 0 && filter.getFacilityTypeId() != -1) {
                WHERE("facilitytypeid = cast(#{filterCriteria.facilityTypeId} as int4)");
            }
            if (filter.getFacilityId() != 0 && filter.getFacilityId() != -1) {
                WHERE("facilityid = cast(#{filterCriteria.facilityId} as int4)");
            }

            if(filter.getProductCategoryId() != 0 && filter.getProductCategoryId() != -1 ){
                WHERE("categoryid = cast(#{filterCriteria.productCategoryId} as int4)");
            }
            if(filter.getRgroupId() != 0 && filter.getRgroupId() != -1){
                WHERE("rgroupid = cast(#{filterCriteria.rgroupId} as int4)");
            }
            if(filter.getProductId() != 0 && filter.getProductId() != -1){
                WHERE("productid= cast(#{filterCriteria.productId} as int4)");
            } else if (filter.getProductId() == -1){
                WHERE("indicator_product = true");
            }
        }
    }

}
