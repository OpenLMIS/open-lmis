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

        BEGIN();

        SELECT("requisition_line_items.productcode::text ProductCode,products.description,requisition_line_items.packstoship UnitSize,requisition_line_items.packstoship UnitQuantity, requisition_line_items.packsize PackQuantity");
        FROM("orders");
        INNER_JOIN("requisitions on requisitions.id = orders.rnrid ");
        INNER_JOIN("requisition_line_items on requisition_line_items.rnrid = requisitions.id");
        INNER_JOIN("products on products.code::text = requisition_line_items.productcode::text");

        return SQL();
    }

    public static String SelectFilteredSortedPagedRecordsCount(Map params){

        BEGIN();
        SELECT("COUNT(*) perCounts");
        FROM("orders");
        INNER_JOIN("requisitions on requisitions.id = orders.rnrid ");
        INNER_JOIN("requisition_line_items on requisition_line_items.rnrid = requisitions.id");
        INNER_JOIN("products on products.code::text = requisition_line_items.productcode::text");

        String subQuery = SQL().toString();

        BEGIN();
        SELECT("COUNT(*)");
        FROM("( "+ subQuery +" ) as counts");
        return SQL();
    }

    private static void writePredicates(Map params){

    }
}
