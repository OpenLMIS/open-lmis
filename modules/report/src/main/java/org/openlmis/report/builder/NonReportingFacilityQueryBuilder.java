/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.builder;

import org.apache.ibatis.type.JdbcType;

import java.util.Map;
import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class NonReportingFacilityQueryBuilder {



    public static String getQuery(Map params){

      Long userId = (Long) params.get("userId");
      params = (Map)( params.containsKey("filterCriteria")? params.get("filterCriteria") : params );


      String period           = ((String[])params.get("period"))[0];
      String zone   = params.containsKey("zone")? ((String[])params.get("zone"))[0]: "" ;
      String facilityType     = params.containsKey("facilityType")? ((String[])params.get("facilityType"))[0] : "" ;
      String program          = ((String[])params.get("program"))[0];
      String schedule         = ((String[])params.get("schedule"))[0];
      return getQueryString(params, program , period , zone, facilityType, schedule, userId);

    }

     private static String getQueryString(Map params, String program , String period, String zone, String facilityType, String schedule, Long userId) {
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
         WHERE("facilities.id in (select facility_id from vw_user_facilities where user_id = cast(" + userId+ " as int4) and program_id = cast(" + program + " as int4))");
         WHERE("facilities.id not in (select r.facilityid from requisitions r where r.status not in ('INITIATED', 'SUBMITTED', 'SKIPPED')  and r.periodid = cast (" + period + " as int4) and r.programid = cast(" + program + " as int4) )");
         writePredicates(program, period, zone, facilityType, schedule);
         ORDER_BY(QueryHelpers.getSortOrder(params, "name"));
         // cache the string query for debugging purposes
         String strQuery = SQL();
         return strQuery;
     }

     private static void writePredicates(String program, String period, String zone, String facilityType, String schedule) {

         if(!zone.equals("0") && !zone.isEmpty() && !zone.endsWith( "undefined")){
             WHERE(" (gz.district_id = " + zone + " or gz.zone_id = " + zone + " or gz.region_id = " + zone +" or gz.parent = " + zone + " )");
         }

         if(!facilityType.isEmpty() && !facilityType.endsWith( "undefined")){
             WHERE("facilities.typeId = cast(" + facilityType+ " as int4)");
         }

         if(!program .isEmpty() && !program.endsWith("undefined")){
            WHERE("ps.programId = cast(" + program+ " as int4)");
         }

         if(! schedule.isEmpty()  && !schedule.endsWith("undefined")){
             WHERE("rgps.scheduleId = cast(" + schedule + " as int4)");
         }
     }




    public static String getTotalFacilities(Map params){
      Long userId = (Long) params.get("userId");
      params = (Map)( params.containsKey("filterCriteria")? params.get("filterCriteria") : params );

      String period           = ((String[])params.get("period"))[0];
      String zone   = params.containsKey("zone")? ((String[])params.get("zone"))[0]: "" ;
      String facilityType     = params.containsKey("facilityType")? ((String[])params.get("facilityType"))[0] : "" ;
      String program          = ((String[])params.get("program"))[0];
      String schedule         = ((String[])params.get("schedule"))[0];
String filerterValue =("user Id "+ userId+ " period "+ period + " facilityType "+facilityType+" program "+ program +" schedule "+ schedule);

        BEGIN();
        SELECT("COUNT (*)");
        FROM("facilities");
        INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id") ;
        INNER_JOIN("vw_districts gz on gz.district_id = facilities.geographicZoneId");
        INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
        INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
        WHERE("facilities.id in (select facility_id from vw_user_facilities where user_id = " + userId+ " and program_id = " + program + ")");
        writePredicates(program, period, zone, facilityType, schedule);
        return SQL();
    }

     public static String getTotalNonReportingFacilities(Map params){
       Long userId = (Long) params.get("userId");
       params = (Map)( params.containsKey("filterCriteria")? params.get("filterCriteria") : params );

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
         INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitionGroupId = rgm.requisitionGroupId and ps.programid = rgps.programid");
         WHERE("facilities.id in (select facility_id from vw_user_facilities where user_id = " + userId+ " and program_id = " + program + ")");
         WHERE("facilities.id not in (select r.facilityid from requisitions r where  r.status not in ('INITIATED', 'SUBMITTED', 'SKIPPED') and r.periodid = cast(" + period + " as int4) and r.programid = cast(" + program + " as int4) )");
         writePredicates(program, period, reportingGroup, facilityType, schedule);
         return SQL();
     }

    public static String getSummaryQuery(Map params){
      Long userId = (Long) params.get("userId");
      params = (Map)( params.containsKey("filterCriteria")? params.get("filterCriteria") : params );


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
      WHERE("facilities.id in (select facility_id from vw_user_facilities where user_id = " + userId+ " and program_id = " + program + ")");
        WHERE("facilities.id not in (select r.facilityid from requisitions r where  r.status not in ('INITIATED', 'SUBMITTED', 'SKIPPED') and r.periodid = cast(" + period + " as int4) and r.programid = cast(" + program + " as int4) )");
        writePredicates(program, period, zone, facilityType,schedule);

        String query = SQL();
        RESET();
        BEGIN();
        SELECT("'Facilities required to report for this program' AS name");
        SELECT("COUNT (*)");
        FROM("facilities");
        INNER_JOIN("vw_districts gz on gz.district_id = facilities.geographicZoneId");
        INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id") ;
        INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
        INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
      WHERE("facilities.id in (select facility_id from vw_user_facilities where user_id = " + userId+ " and program_id = " + program + ")");
        writePredicates(program, period, zone, facilityType, schedule);
        query += " UNION " + SQL();
        return query;

    }
}
