package org.openlmis.report.builder;


import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/**
 * User: Wolde
 * Date: 6/02/13
 * Time: 3:20 PM
 */
public class OrderSummaryQueryBuilder {
    public static String SelectFilteredSortedPagedRecords(Map params){

        Map<String, String []> filter = (Map<String, String[]>) params.get("filterCriteria");

        String orderType =   filter.get("orderType") == null ? null : filter.get("orderType")[0];

        //Regular Orders
        if(orderType == null || orderType.isEmpty() || orderType.equals("Regular")){

            BEGIN();

            SELECT("facilities.name facilityName, facilities.code facilityCode,geographic_zones.name as region,requisition_line_items.productcode::text ProductCode,products.description,requisition_line_items.packstoship UnitSize,requisition_line_items.packstoship UnitQuantity, requisition_line_items.packsize PackQuantity,requisition_line_item_losses_adjustments.quantity Discrepancy");
            FROM("orders");
            INNER_JOIN("requisitions on requisitions.id = orders.rnrid ");
            INNER_JOIN("facilities on facilities.id = requisitions.facilityid");
            INNER_JOIN("facility_types on facility_types.id = facilities.typeid ");
            INNER_JOIN("requisition_line_items on requisition_line_items.rnrid = requisitions.id");
            INNER_JOIN("products on products.code::text = requisition_line_items.productcode::text");
            LEFT_OUTER_JOIN("requisition_line_item_losses_adjustments on requisition_line_item_losses_adjustments.requisitionlineitemid = requisition_line_items.id");
            LEFT_OUTER_JOIN("geographic_zones  on geographic_zones.id = facilities.geographiczoneid");
            writePredicates(params);
            ORDER_BY("facilities.name asc");
            return SQL();

        } else{  //Emergency orders


            BEGIN();

            SELECT("facilities.name facilityName, facilities.code facilityCode,geographic_zones.name as region,requisition_line_items.productcode::text ProductCode,products.description,requisition_line_items.packstoship UnitSize,requisition_line_items.packstoship UnitQuantity, requisition_line_items.packsize PackQuantity,requisition_line_item_losses_adjustments.quantity Discrepancy");
            FROM("orders");
            INNER_JOIN("requisitions on requisitions.id = orders.rnrid ");
            INNER_JOIN("facilities on facilities.id = requisitions.facilityid");
            INNER_JOIN("facility_types on facility_types.id = facilities.typeid ");
            INNER_JOIN("requisition_line_items on requisition_line_items.rnrid = requisitions.id");
            INNER_JOIN("products on products.code::text = requisition_line_items.productcode::text");
            LEFT_OUTER_JOIN("requisition_line_item_losses_adjustments on requisition_line_item_losses_adjustments.requisitionlineitemid = requisition_line_items.id");
            LEFT_OUTER_JOIN("geographic_zones  on geographic_zones.id = facilities.geographiczoneid");
            writePredicates(params);
            ORDER_BY("facilities.name asc");
            return SQL();
        }
    }

    public static String SelectFilteredSortedPagedRecordsCount(Map params){

        BEGIN();
        SELECT("COUNT(*) perCounts");
        FROM("orders");
        INNER_JOIN("requisitions on requisitions.id = orders.rnrid ");
        INNER_JOIN("facilities on facilities.id = requisitions.facilityid");
        INNER_JOIN("facility_types on facility_types.id = facilities.typeid ");
        INNER_JOIN("requisition_line_items on requisition_line_items.rnrid = requisitions.id");
        INNER_JOIN("products on products.code::text = requisition_line_items.productcode::text");
        LEFT_OUTER_JOIN("requisition_line_item_losses_adjustments on requisition_line_item_losses_adjustments.requisitionlineitemid = requisition_line_items.id");
        LEFT_OUTER_JOIN("geographic_zones  on geographic_zones.id = facilities.geographiczoneid");
        writePredicates(params);
        String subQuery = SQL().toString();

        BEGIN();
        SELECT("COUNT(*)");
        FROM("( "+ subQuery +" ) as counts");
        return SQL();
    }

    private static void writePredicates(Map params){
        Map<String, String []> filter = (Map<String, String[]>) params.get("filterCriteria");

        String facilityTypeId =  filter.get("facilityTypeId") == null ? null : filter.get("facilityTypeId")[0];
        String facilityName = filter.get("facilityName") == null ? null : filter.get("facilityName")[0];
        String product =   filter.get("productId") == null ? null : filter.get("productId")[0];
        String zone =     filter.get("zoneId") == null ? null : filter.get("zoneId")[0];

        if (zone != null &&  !zone.equals("undefined") && !zone.isEmpty() && !zone.equals("0")  && !zone.equals("-1")) {
            WHERE("facilities.geographiczoneid = "+zone);
        }
       // if (product != null &&  !product.equals("undefined") && !product.isEmpty() && !product.equals("0") &&  !product.equals("-1")) {
            WHERE("products.id ="+ product);
       // }

        if (facilityTypeId != null &&  !facilityTypeId.equals("undefined") && !facilityTypeId.isEmpty() && !facilityTypeId.equals("0") &&  !facilityTypeId.equals("-1")) {
            WHERE("facility_types.id = "+ facilityTypeId);
        }
        if (facilityName != null &&  !facilityName.equals("undefined") && !facilityName.isEmpty() ) {
            WHERE("facilities.name = '"+ facilityName +"'");
        }

    }
}
