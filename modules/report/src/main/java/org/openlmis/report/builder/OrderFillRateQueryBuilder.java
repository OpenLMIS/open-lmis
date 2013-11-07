package org.openlmis.report.builder;

import org.openlmis.report.model.filter.OrderFillRateReportFilter;

import org.openlmis.report.model.report.OrderFillRateReport;
import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

/**
 * Created with IntelliJ IDEA.
 * User: Hassan
 * Date: 10/28/13
 * Time: 6:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class OrderFillRateQueryBuilder {
    public static String getQuery(Map params){

        OrderFillRateReportFilter filter  = (OrderFillRateReportFilter)params.get("filterCriteria");
        Map sortCriteria = (Map) params.get("SortCriteria");
        BEGIN();
        SELECT("distinct supplyingfacility as supplyingFacility ,facility,productcode,facilitytype as facilityType ,receipts,approved , product,item_fill_rate");
        FROM("vw_order_fill_rate");
        writePredicates(filter);
        ORDER_BY(QueryHelpers.getSortOrder(sortCriteria, OrderFillRateReport.class, "supplyingfacility asc,facilitytype asc, facility asc, product asc"));
        return SQL();

    }


    private static void writePredicates(OrderFillRateReportFilter filter){

        WHERE("periodid = #{filterCriteria.periodId}");//required param
        WHERE("scheduleid = #{filterCriteria.scheduleId}");//required param

        if(filter != null){

            if (filter.getProgramId() != 0 && filter.getProgramId() != -1) {
                WHERE("programid = #{filterCriteria.programId}");
            }
            if (filter.getFacilityTypeId() != 0 && filter.getFacilityTypeId() != -1) {
                WHERE("facilitytypeid = #{filterCriteria.facilityTypeId}");
            }
            if(filter.getFacility() != null && !filter.getFacility().isEmpty()){
                WHERE("facility = #{filterCriteria.facility}");
            }

            if(filter.getProductCategoryId() != 0 && filter.getProductCategoryId() != -1 ){
                WHERE("categoryid = #{filterCriteria.productCategoryId}");
            }
            if(filter.getRgroupId() != 0 && filter.getRgroupId() != -1){
                WHERE("rgroupid = #{filterCriteria.rgroupId}");
            }
            if(filter.getProductId() != 0 && filter.getProductId() != -1){
                WHERE("productid= #{filterCriteria.productId}");
            } else if (filter.getProductId() == -1){
                WHERE("indicator_product = true");
            }
        }
    }
}
