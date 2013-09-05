/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.builder;

import org.apache.ibatis.type.JdbcType;

import java.util.Map;
import static org.apache.ibatis.jdbc.SqlBuilder.*;

 /* Date: 4/11/13
 * Time: 11:34 AM
 */
public class NonReportingFacilityQueryBuilder {



    public static String getQuery(Map params){

       params = (Map)( params.containsKey("param1")? params.get("param1") : params );

        String period           = ((String[])params.get("period"))[0];
        String reportingGroup   = params.containsKey("rgroup")? ((String[])params.get("rgroup"))[0]: "" ;
        String facilityType     = params.containsKey("ftype")? ((String[])params.get("ftype"))[0] : "" ;
        String program          = ((String[])params.get("program"))[0];
        String schedule         = ((String[])params.get("schedule"))[0];
        return getQueryString(params, program , period , reportingGroup, facilityType, schedule);

    }

     private static String getQueryString(Map params, String program , String period, String reportingGroup, String facilityType, String schedule) {
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
         WHERE("facilities.id not in (select r.facilityid from requisitions r where r.status in ('RELEASED','APPROVED') and r.periodid = cast (" + period + " as int4) and r.programid = cast(" + program + " as int4) )");
         writePredicates(program, period, reportingGroup, facilityType, schedule);
         ORDER_BY(QueryHelpers.getSortOrder(params, "name"));
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
      params = (Map)( params.containsKey("param1")? params.get("param1") : params );

      String period           = ((String[])params.get("period"))[0];
      String reportingGroup   = params.containsKey("rgroup")? ((String[])params.get("rgroup"))[0]: "" ;
      String facilityType     = params.containsKey("ftype")? ((String[])params.get("ftype"))[0] : "" ;
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

     public static String getTotalNonReportingFacilities(Map params){
       params = (Map)( params.containsKey("param1")? params.get("param1") : params );

       String period           = ((String[])params.get("period"))[0];
       String reportingGroup   = params.containsKey("rgroup")? ((String[])params.get("rgroup"))[0]: "" ;
       String facilityType     = params.containsKey("ftype")? ((String[])params.get("ftype"))[0] : "" ;
       String program          = ((String[])params.get("program"))[0];
       String schedule         = ((String[])params.get("schedule"))[0];

         BEGIN();
         SELECT("COUNT (*)");
         FROM("facilities");
         INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id") ;
         INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
         INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
         WHERE("facilities.id not in (select r.facilityid from requisitions r where  r.status in ('RELEASED','APPROVED') and r.periodid = cast(" + period + " as int4) and r.programid = cast(" + program + " as int4) )");
         writePredicates(program, period, reportingGroup, facilityType, schedule);
         return SQL();
     }



    public static String getSummaryQuery(Map params){
      params = (Map)( params.containsKey("param1")? params.get("param1") : params );

      String period           = ((String[])params.get("period"))[0];
      String reportingGroup   = params.containsKey("rgroup")? ((String[])params.get("rgroup"))[0]: "" ;
      String facilityType     = params.containsKey("ftype")? ((String[])params.get("ftype"))[0] : "" ;
      String program          = ((String[])params.get("program"))[0];
      String schedule         = ((String[])params.get("schedule"))[0];

        BEGIN();
        SELECT("'Non Reporting Facilities' AS name");
        SELECT("COUNT (*)");
        FROM("facilities");
        INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id") ;
        INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id") ;
        INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
        WHERE("facilities.id not in (select r.facilityid from requisitions r where  r.status in ('RELEASED','APPROVED') and r.periodid = cast(" + period + " as int4) and r.programid = cast(" + program + " as int4) )");
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
