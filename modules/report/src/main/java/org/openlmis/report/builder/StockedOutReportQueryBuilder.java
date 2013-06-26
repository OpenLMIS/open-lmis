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

       StockedOutReportFilter filter = (StockedOutReportFilter) params.get("filterCriteria");

       String period           = filter.getPeriod();
       String reportingGroup   = filter.getReportingGroup();
       String facilityType     = filter.getFacilityType();
       String program          = filter.getProgram();
       String schedule         = filter.getSchedule();
       String productCategory   = filter.getProductCategory();
       String periodType = filter.getPeriodType();
       //return getQueryString(periodType, program , period , reportingGroup, facilityType, schedule, productCategory);
       String productCateogryQuery = "";
       if(productCategory != "" && !productCategory.endsWith( "undefined")){
           productCateogryQuery =  " and pc.id = " + productCategory;
       }

       BEGIN();
       SELECT_DISTINCT("facilities.code, facilities.name");
       SELECT_DISTINCT("gz.name as location");
       SELECT_DISTINCT("ft.name as facilityType");


       FROM("facilities");
       INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
       INNER_JOIN("geographic_zones gz on gz.id = facilities.geographiczoneid");
       INNER_JOIN("facility_types ft on ft.id = facilities.typeid");
       INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id");
       INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
       if(periodType.equals("predefined")){
           WHERE("facilities.id in (select r.facilityid from requisitions r join requisition_line_items li on r.id = li.rnrid join product_categories pc on pc.name = li.productcategory where li.stockinhand = 0 and r.periodid = cast (" + period + " as int4) and r.programid = cast(" + program + " as int4) " + productCateogryQuery + " )");

       }else{
           WHERE("facilities.id in \n" +
                   "(select r.facilityid from requisitions r \n" +
                   "join requisition_line_items li on r.id = li.rnrid\n" +
                   "        join processing_periods pp on pp.id = r.periodid\t\t\t\n" +
                   "join product_categories pc on pc.name = li.productcategory \n" +
                   "where li.stockinhand = 0 and \n" +
                   "pp.startdate >= #{filterCriteria.startDate, jdbcType=DATE, javaType=java.util.Date, mode=IN} and pp.enddate <= #{filterCriteria.endDate, jdbcType=DATE, javaType=java.util.Date, mode=IN} and  r.programid = cast(" + program + " as int4) " + productCateogryQuery + " )");
       }
       writePredicates(program, period, reportingGroup, facilityType, schedule);
       //ORDER_BY(QueryHelpers.getSortOrder(params, "name"));
       // cache the string query for debugging purposes
       String strQuery = SQL();
       return strQuery;

   }

    private static String getQueryString(String periodType, String program , String period, String reportingGroup, String facilityType, String schedule, String productCategory) {
        // include the product category filter for the inner query
        String productCateogryQuery = "";
        if(productCategory != "" && !productCategory.endsWith( "undefined")){
            productCateogryQuery =  " and pc.id = " + productCategory;
        }

        BEGIN();
        SELECT_DISTINCT("facilities.code, facilities.name");
        SELECT_DISTINCT("gz.name as location");
        SELECT_DISTINCT("ft.name as facilityType");


        FROM("facilities");
        INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
        INNER_JOIN("geographic_zones gz on gz.id = facilities.geographiczoneid");
        INNER_JOIN("facility_types ft on ft.id = facilities.typeid");
        INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id");
        INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
        if(periodType.equals("predefined")){
           WHERE("facilities.id in (select r.facilityid from requisitions r join requisition_line_items li on r.id = li.rnrid join product_categories pc on pc.name = li.productcategory where li.stockinhand = 0 and r.periodid = cast (" + period + " as int4) and r.programid = cast(" + program + " as int4) " + productCateogryQuery + " )");

        }else{
           WHERE("facilities.id in \n" +
                   "(select r.facilityid from requisitions r \n" +
                   "join requisition_line_items li on r.id = li.rnrid\n" +
                   "        join processing_periods pp on pp.id = r.periodid\t\t\t\n" +
                   "join product_categories pc on pc.name = li.productcategory \n" +
                   "where li.stockinhand = 0 and \n" +
                   "pp.startdate >= #{filterCriteria.startDate, jdbcType=DATE, javaType=java.util.Date, mode=IN} and pp.enddate <= #{filterCriteria.endDate, jdbcType=DATE, javaType=java.util.Date, mode=IN} and  r.programid = cast(" + program + " as int4) " + productCateogryQuery + " )");
        }
        writePredicates(program, period, reportingGroup, facilityType, schedule);
        //ORDER_BY(QueryHelpers.getSortOrder(params, "name"));
        // cache the string query for debugging purposes
        String strQuery = SQL();
        return strQuery;
    }

    private static void writePredicates(String program, String period, String reportingGroup, String facilityType, String schedule) {

        if(reportingGroup != "" && !reportingGroup.endsWith( "undefined")){
            WHERE("rgm.requisitiongroupid = cast (" + reportingGroup + " as int4)");
        }

        if(facilityType != "" && !facilityType.endsWith( "undefined")){
            WHERE("facilities.typeid = cast(" + facilityType+ " as int4)");
        }

        if(program != "" && !program.endsWith("undefined")){
           WHERE("ps.programid = cast(" + program+ " as int4)");
        }

        if(schedule != "" && !schedule.endsWith("undefined")){
            WHERE("rgps.scheduleid = cast(" + schedule + " as int4)");
        }
    }




   public static String getTotalFacilities(Map params){
       String period           = ((String[])params.get("period"))[0];
       String reportingGroup   = ((String[])params.get("rgroup"))[0];
       String facilityType     = ((String[])params.get("ftype"))[0];
       String program          = ((String[])params.get("program"))[0];
       String schedule         = ((String[])params.get("schedule"))[0];
       BEGIN();
       SELECT("COUNT (*)");
       FROM("facilities");
       INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id") ;
       INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
       INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
       writePredicates(program, period, reportingGroup, facilityType, schedule);
       return SQL();
   }

    public static String getStockedOutFacilitiesCount(Map params){
        String period           = ((String[])params.get("period"))[0];
        String reportingGroup   = ((String[])params.get("rgroup"))[0];
        String facilityType     = ((String[])params.get("ftype"))[0];
        String program          = ((String[])params.get("program"))[0];
        String schedule         = ((String[])params.get("schedule"))[0];
        String productCategory  = ((String[])params.get("productCategory"))[0];

        // include the product category filter for the inner query
        String productCateogryQuery = "";
        if(productCategory != "" && !productCategory.endsWith( "undefined")){
            productCateogryQuery =  " and pc.id = " + productCategory;
        }
        BEGIN();
        SELECT("COUNT (*)");
        FROM("facilities");
        INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id") ;
        INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
        INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
        WHERE("facilities.id in (select r.facilityid from requisitions r join requisition_line_items li on r.id = li.rnrid join product_categories pc on pc.name = li.productcategory where li.stockinhand = 0 and r.periodid = cast (" + period + " as int4) and r.programid = cast(" + program + " as int4) " + productCateogryQuery + " )");
        writePredicates(program, period, reportingGroup, facilityType, schedule);
        return SQL();
    }



   public static String getSummaryQuery(Map params){

       String period           = ((String[])params.get("period"))[0];
       String reportingGroup   = ((String[])params.get("rgroup"))[0];
       String facilityType     = ((String[])params.get("ftype"))[0];
       String program          = ((String[])params.get("program"))[0];
       String schedule         = ((String[])params.get("schedule"))[0];
       String productCategory  = ((String[])params.get("productCategory"))[0];

       // include the product category filter for the inner query
       String productCateogryQuery = "";
       if(productCategory != "" && !productCategory.endsWith( "undefined")){
           productCateogryQuery =  " and pc.id = " + productCategory;
       }

       BEGIN();
       SELECT("'Stocked Out Facilities' AS name");
       SELECT("COUNT (*)");
       FROM("facilities");
       INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id") ;
       INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id") ;
       INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
       WHERE("facilities.id in (select r.facilityid from requisitions r join requisition_line_items li on r.id = li.rnrid join product_categories pc on pc.name = li.productcategory where li.stockinhand = 0 and r.periodid = cast (" + period + " as int4) and r.programid = cast(" + program + " as int4) " + productCateogryQuery + " )");
       writePredicates(program, period, reportingGroup, facilityType,schedule);

       String query = SQL();
       RESET();
       BEGIN();
       SELECT("'Reporting for this Program' AS name");
       SELECT("COUNT (*)");
       FROM("facilities");
       INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id") ;
       INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
       INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
       writePredicates(program, period, reportingGroup, facilityType, schedule);
       query += " UNION " + SQL();
       return query;

   }
}
