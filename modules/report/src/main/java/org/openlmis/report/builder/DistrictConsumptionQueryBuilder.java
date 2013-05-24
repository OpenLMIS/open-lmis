package org.openlmis.report.builder;

import org.openlmis.report.model.filter.DistrictConsumptionReportFilter;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

/**
 * User: Wolde
 * Date: 5/24/13
 */
public class DistrictConsumptionQueryBuilder { public static String SelectFilteredSortedPagedRecords(Map params){

    DistrictConsumptionReportFilter filter  = (DistrictConsumptionReportFilter)params.get("filterCriteria");
    Map<String, String[]> sorter = ( Map<String, String[]>)params.get("SortCriteria");
    BEGIN();

    SELECT("product productDescription, product_category_name category, facility_type_name facilityType,facility_name facilityName, adjustment_type adjustmentType, SUM(totallossesandadjustments) adjustment,supplying_facility_name supplyingFacility");
    FROM("vw_requisition_adjustment");
    writePredicates(filter);
    GROUP_BY("product, adjustment_type,product_category_name,facility_type_name,facility_name, supplying_facility_name");
    ORDER_BY(QueryHelpers.getSortOrder(params, "facility_type_name,facility_name, supplying_facility_name, product, product_category_name , adjustment_type"));

    return SQL();
}

    public static String SelectFilteredSortedPagedRecordsCount(Map params){

        DistrictConsumptionReportFilter filter  = (DistrictConsumptionReportFilter)params.get("filterCriteria");

        BEGIN();
        SELECT("COUNT(*) perCounts");
        FROM(" vw_requisition_adjustment");
        writePredicates(filter);

        GROUP_BY("product, adjustment_type,product_category_name,facility_type_name,facility_name, supplying_facility_name");
        ORDER_BY(QueryHelpers.getSortOrder(params, "facility_type_name,facility_name, supplying_facility_name, product, product_category_name , adjustment_type"));

        String subQuery = SQL().toString();

        BEGIN();
        SELECT("COUNT(*)");
        FROM("( "+ subQuery +" ) as counts");
        return SQL();
    }

    private static void writePredicates(DistrictConsumptionReportFilter filter){
        if(filter != null){

        }
    }
}
