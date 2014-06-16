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

import org.openlmis.report.model.params.OrderFillRateReportParam;
import org.openlmis.report.model.report.OrderFillRateReport;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class OrderFillRateQueryBuilder {

    public static String getOrderFillRateQuery(Map params){
        return "select (count(receipts)/count(approved)) * 100 as ORDER_FILL_RATE" +
                "from vw_order_fill_rate where approved !=0";
    }

    public static String getQuery(Map params) {
        OrderFillRateReportParam filter = (OrderFillRateReportParam) params.get("filterCriteria");
        String sql = " ";
        sql = "select facilityname facility,productcode,primaryname product,quantityapproved approved,quantityreceived receipts,item_fill_rate\n" +
                " from vw_order_fill_rate\n " +
                writePredicates(filter) ;

        return sql;
    }


    /*public static String getQuery(Map params){

        OrderFillRateReportParam filter  = (OrderFillRateReportParam)params.get("filterCriteria");
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

    }*/

    private static String writePredicates(OrderFillRateReportParam filter) {
        String predicate = "";

        if (filter != null) {
            if (filter.getRgroupId() != 0 && filter.getRgroupId() !=-1) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " requisitionGroupId = #{filterCriteria.rgroupId}";
            }

            if (filter.getScheduleId() != 0 && filter.getScheduleId() != -1) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " scheduleid= #{filterCriteria.scheduleId}";
            }

            if (filter.getProgramId() != 0 && filter.getProgramId() != -1) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " programid = #{filterCriteria.programId}";
            }
            if (filter.getPeriodId() != 0 && filter.getPeriodId() != -1) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " periodid= #{filterCriteria.periodId}";
            }
            if (filter.getProductId() != 0 && filter.getProductId() != -1) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " productId = #{filterCriteria.productId}";
            }
            if (filter.getProductCategoryId() != 0 && filter.getProductCategoryId() != -1) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " productCategoryId = #{filterCriteria.productCategoryId}";
            }
            if (filter.getFacilityId() != 0 && filter.getFacilityId() != -1) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " facilityid = #{filterCriteria.facilityId}";
            }
            if (filter.getFacilityTypeId() != 0 && filter.getFacilityTypeId() !=-1) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " facilitytypeid = #{filterCriteria.facilityTypeId}";
            }

        }

        return predicate;
    }




/*

    private static void writePredicates1(OrderFillRateReportParam filter){

        WHERE("periodid = cast( #{filterCriteria.periodId} as int4) ");
        WHERE("scheduleid = cast(#{filterCriteria.scheduleId} as int4) ");//required param

        if(filter != null){

            if (filter.getProgramId() != 0 && filter.getProgramId() != -1) {
                WHERE("programId = cast(#{filterCriteria.programId} as int4)");
            }
            if (filter.getFacilityTypeId() != 0 && filter.getFacilityTypeId() != -1) {
                WHERE("facilitytypeId = cast(#{filterCriteria.facilityTypeId} as int4)");
            }
            if (filter.getFacilityId() != 0 && filter.getFacilityId() != -1) {
                WHERE("facilityId = cast(#{filterCriteria.facilityId} as int4)");
            }

            if(filter.getProductCategoryId() != 0 && filter.getProductCategoryId() != -1 ){
                WHERE("productCategoryId = cast(#{filterCriteria.productCategoryId} as int4)");
            }
            if(filter.getRgroupId() != 0 && filter.getRgroupId() != -1){
                WHERE("requisitionGroupId = cast(#{filterCriteria.rgroupId} as int4)");
            }
            if(filter.getProductId() != 0 && filter.getProductId() != -1){
                WHERE("productId= cast(#{filterCriteria.productId} as int4)");
            } else if (filter.getProductId() == -1){
                WHERE("indicator_product = true");
            }
        }
    }*/

}
