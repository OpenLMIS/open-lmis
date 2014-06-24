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

    public static String getTotalProductsOrdered(Map params) {
        OrderFillRateReportParam filter = (OrderFillRateReportParam) params.get("filterCriteria");
        String sql = " ";
        sql = "select  Case WHEN COALESCE(totalproductsapproved, 0::numeric) > 0::numeric THEN sum(totalproductsapproved):: NUMERIC END AS totalproductsapproved\n" +
                "\n" +
                "from vw_order_fill_rate\n" +
                writePredicates(filter) +
                "group by totalproductsapproved ";
        return sql;
    }

    public static String getTotalProductsReceived(Map params) {
        OrderFillRateReportParam filter = (OrderFillRateReportParam) params.get("filterCriteria");
        String sql = " ";
        sql = "select Case WHEN COALESCE(totalproductsreceived, 0::numeric) > 0::numeric THEN sum(totalproductsreceived):: NUMERIC END AS totalproductsreceived\n" +
                "from vw_order_fill_rate\n" +
                writePredicates(filter) +
                "group by totalproductsreceived ";
        return sql;
    }

    public static String getOrderFillRateQuery(Map params) {
        OrderFillRateReportParam filter = (OrderFillRateReportParam) params.get("filterCriteria");
        String sql = " ";

        sql = "select \n" +
                "      COALESCE( case when COALESCE(totalproductsapproved, 0::numeric) > 0 ::NUMERIC THEN \n" +
                "      round(((sum(totalproductsreceived)*100)/sum(totalproductsapproved)),0) :: NUMERIC ELSE 0 END ) AS ORDER_FILL_RATE\n" +
                "      from vw_order_fill_rate\n" +
                writePredicates(filter) +
                "      group by totalproductsapproved " +
                "limit 1 ";

        return sql;
    }

    public static String getQuery(Map params) {
        OrderFillRateReportParam filter = (OrderFillRateReportParam) params.get("filterCriteria");
        String sql = " ";
        sql = "select distinct facilityname facility,productcode,primaryname product,quantityapproved approved,\n" +
                "\n" +
                "                quantityreceived receipts,item_fill_rate\n" +
                "                from vw_order_fill_rate\n" +
                writePredicates(filter) +
                "                group by facilityname,productcode,primaryname,quantityapproved,quantityreceived,item_fill_rate ";

        return sql;
    }

    private static String writePredicates(OrderFillRateReportParam filter) {
        String predicate = "";

        if (filter != null) {
            if (filter.getRgroupId() != 0) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " requisitionGroupId = #{filterCriteria.rgroupId}";
            }

            if (filter.getScheduleId() != 0 && filter.getScheduleId() != -1) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " scheduleid= #{filterCriteria.scheduleId}";
            }

            if (filter.getProgramId() != 0) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " programid = #{filterCriteria.programId}";
            }
            if (filter.getPeriodId() != 0) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " periodid= #{filterCriteria.periodId}";
            }
            if (filter.getProductId() != 0 && filter.getProductId() !=-1) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " productId = #{filterCriteria.productId}";
            }
            if (filter.getProductCategoryId() != 0 && filter.getProductCategoryId() != -1) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " productCategoryId = #{filterCriteria.productCategoryId}";
            }
            if (filter.getFacilityId() != 0) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " facilityid = #{filterCriteria.facilityId}";
            }
            if (filter.getFacilityTypeId() != 0 && filter.getFacilityTypeId() != -1) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " facilitytypeid = #{filterCriteria.facilityTypeId}";
            }

        }

        return predicate;
    }

}
