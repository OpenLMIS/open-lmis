/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.builder;


import org.openlmis.report.model.filter.OrderReportFilter;
import org.openlmis.report.model.report.OrderSummaryReport;

import java.util.Calendar;
import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/**
 * User: Wolde
 * Date: 6/02/13
 * Time: 3:20 PM
 */
public class OrderSummaryQueryBuilder {
    public static String SelectFilteredSortedPagedRecords(Map params){


        OrderReportFilter filter  = (OrderReportFilter)params.get("filterCriteria");
        Map sortCriteria = (Map) params.get("sortCriteria");
        String orderType =   filter.getOrderType() == null ? null : filter.getOrderType();

        //Regular Orders
        if(orderType == null || orderType.isEmpty() || orderType.equals("Regular")){

           BEGIN();

            SELECT("facility_name AS facilityName, facility_code AS facilityCode, region, product_code AS productCode, product_primaryname AS description, packstoship AS unitSize, packstoship AS unitQuantity, packsize AS packQuantity, requisition_line_item_losses_adjustments.quantity AS discrepancy");
            FROM("vw_requisition_detail");
            INNER_JOIN("orders ON orders.id = vw_requisition_detail.req_id ");
            LEFT_OUTER_JOIN("requisition_line_item_losses_adjustments ON vw_requisition_detail.req_line_id = requisition_line_item_losses_adjustments.requisitionlineitemid");

            writePredicates(filter);
            ORDER_BY(QueryHelpers.getSortOrder(sortCriteria, OrderSummaryReport.class,"facility_name asc"));
            return SQL();

        } else{  //Emergency orders


            BEGIN();

            SELECT("facility_name AS facilityName, facility_code AS facilityCode, region, product_code AS productCode, product_primaryname AS description, packstoship AS unitSize, packstoship AS unitQuantity, packsize AS packQuantity, requisition_line_item_losses_adjustments.quantity AS discrepancy");
            FROM("vw_requisition_detail");
            INNER_JOIN("orders ON orders.id = vw_requisition_detail.req_id ");
            LEFT_OUTER_JOIN("requisition_line_item_losses_adjustments ON vw_requisition_detail.req_line_id = requisition_line_item_losses_adjustments.requisitionlineitemid");

            writePredicates(filter);
            ORDER_BY(QueryHelpers.getSortOrder(sortCriteria,OrderSummaryReport.class,"facility_name asc"));
            return SQL();
        }
    }

    private static void writePredicates(OrderReportFilter  filter){
        WHERE("req_status = 'RELEASED'");
        WHERE("program_id = "+filter.getProgramId());
        WHERE("facility_id = "+filter.getFacilityId());
        WHERE("processing_periods_id = "+filter.getPeriodId());

        if (filter.getZoneId() != 0 && filter.getZoneId() != -1) {
            WHERE("zone_id = "+filter.getZoneId());
        }
        if (filter.getProductId() != -1 && filter.getProductId() != 0) {
            WHERE("product_id ="+ filter.getProductId());
        }else if(filter.getProductId()== -1){
            WHERE("indicator_product = true");
        }


    }
}
