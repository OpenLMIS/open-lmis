package org.openlmis.report.builder;

import org.openlmis.report.model.filter.StockedOutReportFilter;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/* Date: 4/11/13
* Time: 11:34 AM
*/
public class StockedOutReportQueryBuilder {

    public static String getQuery(Map params){


        StockedOutReportFilter filter  = (StockedOutReportFilter)params.get("filterCriteria");
        BEGIN();
        SELECT("facilitycode,  facility,  product,  facilitytypename,  location");
        FROM("vw_stock_status");
        writePredicates(filter);
        ORDER_BY("facility");
        return SQL();

    }
    private static void writePredicates(StockedOutReportFilter filter){
        WHERE("status = 'SO'");
        if(filter != null){
            if (filter.getFacilityTypeId() != 0 && filter.getFacilityTypeId() != -1) {
                WHERE("facility_type_id = #{filterCriteria.facilityTypeId}");
            }
            if(filter.getFacility() != null && !filter.getFacility().isEmpty()){
                WHERE("facility = #{filterCriteria.facility}");
            }
            if (filter.getStartDate() != null) {
                WHERE("processing_periods_start_date >= #{filterCriteria.startDate, jdbcType=DATE, javaType=java.util.Date, mode=IN}");
            }
            if (filter.getEndDate() != null) {
                WHERE("processing_periods_end_date <= #{filterCriteria.endDate, jdbcType=DATE, javaType=java.util.Date, mode=IN}");
            }
            if(filter.getProductCategoryId() != 0 && filter.getProductCategoryId() != -1 ){
                WHERE("product_category_id = #{filterCriteria.productCategoryId}");
            }
            if(filter.getRgroupId() != 0 && filter.getRgroupId() != -1){
                WHERE("requisition_group_id = #{filterCriteria.rgroupId}");
            }
            if(filter.getProductId() != -1){
                WHERE("product_id= #{filterCriteria.productId}");
            }
        }
    }
}
