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
        String zone   = params.containsKey("zone")? ((String[])params.get("zone"))[0]: "" ;
        String facilityType     = params.containsKey("facilityType")? ((String[])params.get("facilityType"))[0] : "" ;
        String program          = ((String[])params.get("program"))[0];
        String schedule         = ((String[])params.get("schedule"))[0];
        return getQueryString(params, program , period , zone, facilityType, schedule);

    }

     private static String getQueryString(Map params, String program , String period, String zone, String facilityType, String schedule) {
         BEGIN();
         SELECT_DISTINCT("facilities.code, facilities.name");
         SELECT_DISTINCT("gz.district_name as location");
         SELECT_DISTINCT("ft.name as facilityType");

         FROM("facilities");
         INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
         INNER_JOIN("vw_districts gz on gz.district_id = facilities.geographiczoneid");
         INNER_JOIN("facility_types ft on ft.id = facilities.typeid");
         INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id");
         INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
         WHERE("facilities.id not in (select r.facilityid from requisitions r where r.status in ('RELEASED','APPROVED') and r.periodid = cast (" + period + " as int4) and r.programid = cast(" + program + " as int4) )");
         writePredicates(program, period, zone, facilityType, schedule);
         ORDER_BY(QueryHelpers.getSortOrder(params, "name"));
         // cache the string query for debugging purposes
         String strQuery = SQL();
         return strQuery;
     }

     private static void writePredicates(String program, String period, String zone, String facilityType, String schedule) {

         if(zone != "" && !zone.endsWith( "undefined")){
             WHERE(" (gz.district_id = " + zone + " or gz.zone_id = " + zone + " or gz.region_id = " + zone +" or gz.parent = " + zone + " )");
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
      String reportingGroup   = params.containsKey("zone")? ((String[])params.get("zone"))[0]: "" ;
      String facilityType     = params.containsKey("facilityType")? ((String[])params.get("facilityType"))[0] : "" ;
      String program          = ((String[])params.get("program"))[0];
      String schedule         = ((String[])params.get("schedule"))[0];

        BEGIN();
        SELECT("COUNT (*)");
        FROM("facilities");
        INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id") ;
        INNER_JOIN("vw_districts gz on gz.district_id = facilities.geographicZoneId");
        INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
        INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
        writePredicates(program, period, reportingGroup, facilityType, schedule);
        return SQL();
    }

     public static String getTotalNonReportingFacilities(Map params){
       params = (Map)( params.containsKey("param1")? params.get("param1") : params );

       String period           = ((String[])params.get("period"))[0];
       String reportingGroup   = params.containsKey("zone")? ((String[])params.get("zone"))[0]: "" ;
       String facilityType     = params.containsKey("facilityType")? ((String[])params.get("facilityType"))[0] : "" ;
       String program          = ((String[])params.get("program"))[0];
       String schedule         = ((String[])params.get("schedule"))[0];

         BEGIN();
         SELECT("COUNT (*)");
         FROM("facilities");
         INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id");
         INNER_JOIN("vw_districts gz on gz.district_id = facilities.geographicZoneId");
         INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
         INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
         WHERE("facilities.id not in (select r.facilityid from requisitions r where  r.status in ('RELEASED','APPROVED') and r.periodid = cast(" + period + " as int4) and r.programid = cast(" + program + " as int4) )");
         writePredicates(program, period, reportingGroup, facilityType, schedule);
         return SQL();
     }



    public static String getSummaryQuery(Map params){
      params = (Map)( params.containsKey("param1")? params.get("param1") : params );

      String period           = ((String[])params.get("period"))[0];
      String zone             = params.containsKey("zone")? ((String[])params.get("zone"))[0]: "" ;
      String facilityType     = params.containsKey("facilityType")? ((String[])params.get("ftype"))[0] : "" ;
      String program          = ((String[])params.get("program"))[0];
      String schedule         = ((String[])params.get("schedule"))[0];

        BEGIN();
        SELECT("'Non Reporting Facilities' AS name");
        SELECT("COUNT (*)");
        FROM("facilities");
        INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id") ;
        INNER_JOIN("vw_districts gz on gz.district_id = facilities.geographicZoneId");
        INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id") ;
        INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
        WHERE("facilities.id not in (select r.facilityid from requisitions r where  r.status in ('RELEASED','APPROVED') and r.periodid = cast(" + period + " as int4) and r.programid = cast(" + program + " as int4) )");
        writePredicates(program, period, zone, facilityType,schedule);

        String query = SQL();
        RESET();
        BEGIN();
        SELECT("'Reporting for this Program' AS name");
        SELECT("COUNT (*)");
        FROM("facilities");
        INNER_JOIN("vw_districts gz on gz.district_id = facilities.geographicZoneId");
        INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id") ;
        INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
        INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
        writePredicates(program, period, zone, facilityType, schedule);
        query += " UNION " + SQL();
        return query;

    }
}
