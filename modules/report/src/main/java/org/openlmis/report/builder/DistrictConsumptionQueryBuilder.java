package org.openlmis.report.builder;

import org.openlmis.report.model.filter.DistrictConsumptionReportFilter;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

/**
 * User: Wolde
 * Date: 5/24/13
 */
public class DistrictConsumptionQueryBuilder {

    public static String SelectFilteredSortedPagedRecords(Map params){

     DistrictConsumptionReportFilter filter  = (DistrictConsumptionReportFilter)params.get("filterCriteria");

     String query = "WITH temp AS (select product,zone_name, SUM(normalizedconsumption) normalizedconsumption "+

    "from vw_district_consumption_summary "+
     writePredicates(filter)+
    "group by product,zone_name "+
    "order by product) "+

    "select t.product, t.zone_name district, t.normalizedconsumption consumption, case when temp2.total > 0 THEN round(((t.normalizedconsumption*100)/temp2.total),1) ELSE temp2.total END totalPercentage  "+
    "from temp t "+
    "INNER JOIN ( select product,SUM(normalizedconsumption) total "+
    "from temp "+
    "group by product "+
    "order by product) temp2 ON t.product = temp2.product ";


    return query;
}
    private static String writePredicates(DistrictConsumptionReportFilter filter){
        String predicate = "";
        if(filter != null){
            if (filter.getStartDate() != null) {
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " processing_periods_start_date >= #{filterCriteria.startDate, jdbcType=DATE, javaType=java.util.Date, mode=IN}";
                            }
            if (filter.getEndDate() != null) {
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " processing_periods_end_date <= #{filterCriteria.endDate, jdbcType=DATE, javaType=java.util.Date, mode=IN}";
            }
            if(filter.getProductCategoryId() != 0 ){
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " product_category_id = #{filterCriteria.productCategoryId}";
            }
            if(filter.getRgroupId() != 0){
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " requisition_group_id = #{filterCriteria.rgroupId}";
            }
            if(filter.getProductId() != 0){
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " product_id= #{filterCriteria.productId}";
            }
            if(filter.getProgramId() != 0){
                predicate = predicate.isEmpty() ?" where " : predicate +  " and ";
                predicate = predicate + " program_id = #{filterCriteria.programId}";
            }

        }

        return predicate;
    }

    public static String SelectFilteredSortedPagedRecordsCount(Map params){

        DistrictConsumptionReportFilter filter  = (DistrictConsumptionReportFilter)params.get("filterCriteria");

        String query = "WITH temp AS (select product,zone_name, SUM(normalizedconsumption) normalizedconsumption "+

                "from vw_district_consumption_summary "+
                writePredicates(filter)+
                "group by product,zone_name "+
                "order by product) "+

                "select count(*) "+
                "from temp t "+
                "INNER JOIN ( select product,SUM(normalizedconsumption) total "+
                "from temp "+
                "group by product "+
                "order by product) temp2 ON t.product = temp2.product ";
        return query;
    }


}
