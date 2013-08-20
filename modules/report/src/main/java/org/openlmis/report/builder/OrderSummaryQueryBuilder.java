package org.openlmis.report.builder;


import org.openlmis.report.model.filter.OrderReportFilter;

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
        String orderType =   filter.getOrderType() == null ? null : filter.getOrderType();

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
            INNER_JOIN("program_products ON program_products.productid = products.id ");
            INNER_JOIN("programs ON  program_products.programid = programs.id   AND  programs.id = requisitions.programid ");
            INNER_JOIN("programs_supported ON  programs.id = programs_supported.programid   AND   facilities.id = programs_supported.facilityid");
            INNER_JOIN("requisition_group_members ON facilities.id = requisition_group_members.facilityid");
            INNER_JOIN("requisition_groups ON requisition_groups.id = requisition_group_members.requisitiongroupid ");
            INNER_JOIN("requisition_group_program_schedules ON requisition_group_program_schedules.programid = programs.id   AND requisition_group_program_schedules.requisitiongroupid = requisition_groups.id ");
            INNER_JOIN("processing_schedules ON processing_schedules.id = requisition_group_program_schedules.programid");
            INNER_JOIN("processing_periods ON processing_periods.scheduleid = processing_schedules.id ");
            LEFT_OUTER_JOIN("requisition_line_item_losses_adjustments on requisition_line_item_losses_adjustments.requisitionlineitemid = requisition_line_items.id");
            LEFT_OUTER_JOIN("geographic_zones  on geographic_zones.id = facilities.geographiczoneid");
            writePredicates(filter);
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
            INNER_JOIN("program_products ON program_products.productid = products.id ");
            INNER_JOIN("programs ON  program_products.programid = programs.id   AND  programs.id = requisitions.programid ");
            INNER_JOIN("programs_supported ON  programs.id = programs_supported.programid   AND   facilities.id = programs_supported.facilityid");
            INNER_JOIN("requisition_group_members ON facilities.id = requisition_group_members.facilityid");
            INNER_JOIN("requisition_groups ON requisition_groups.id = requisition_group_members.requisitiongroupid ");
            INNER_JOIN("requisition_group_program_schedules ON requisition_group_program_schedules.programid = programs.id   AND requisition_group_program_schedules.requisitiongroupid = requisition_groups.id ");
            INNER_JOIN("processing_schedules ON processing_schedules.id = requisition_group_program_schedules.programid");
            INNER_JOIN("processing_periods ON processing_periods.scheduleid = processing_schedules.id ");
            LEFT_OUTER_JOIN("requisition_line_item_losses_adjustments on requisition_line_item_losses_adjustments.requisitionlineitemid = requisition_line_items.id");
            LEFT_OUTER_JOIN("geographic_zones  on geographic_zones.id = facilities.geographiczoneid");
            writePredicates(filter);
            ORDER_BY("facilities.name asc");
            return SQL();
        }
    }

    private static void writePredicates(OrderReportFilter  filter){

        WHERE("requisitions.programid = "+filter.getProgramId());
        WHERE("facilities.id = "+filter.getFacilityId());
        WHERE("processing_periods.id = "+filter.getPeriodId());

        if (filter.getZoneId() != 0 && filter.getZoneId() != -1) {
            WHERE("facilities.geographiczoneid = "+filter.getZoneId());
        }
        if (filter.getProductId() != -1 && filter.getProductId() != 0) {
            WHERE("products.id ="+ filter.getProductId());
        }


    }
}
