package org.openlmis.report.builder;

import org.apache.ibatis.type.JdbcType;

import java.util.Map;
import static org.apache.ibatis.jdbc.SqlBuilder.*;

 /* Date: 4/11/13
 * Time: 11:34 AM
 */
public class NonReportingFacilityQueryBuilder {

    public static String getQuery(Map params){

        String period           = ((String[])params.get("period"))[0];
        String reportingGroup   = ((String[])params.get("rgroup"))[0] ;
        String facilityType     = ((String[])params.get("ftype"))[0] ;
        String program          = ((String[])params.get("program"))[0];
        return getQueryString(params, program , period , reportingGroup, facilityType);

    }

     private static String getQueryString(Map params, String program , String period, String reportingGroup, String facilityType) {
         BEGIN();
         SELECT_DISTINCT("facilities.code, facilities.name");
         SELECT_DISTINCT("gz.name as location");
         SELECT_DISTINCT("ft.name as facilityType");

         FROM("facilities");
         INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
         INNER_JOIN("geographic_zones gz on gz.id = facilities.geographiczoneid");
         INNER_JOIN("facility_types ft on ft.id = facilities.typeid");
         INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id");
         INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.id and ps.programid = rgps.programid");
         WHERE("facilities.id not in (select r.facilityid from requisitions r where r.periodid = " + period + " and r.programid = " + program + ")");
         writePredicates(program, period, reportingGroup, facilityType);
         ORDER_BY(getSortOrder(params));
         return SQL();
     }

     private static void writePredicates(String program, String period, String reportingGroup, String facilityType) {

         if(reportingGroup != "" && !reportingGroup.endsWith( "undefined")){
             WHERE("requisitiongroupid = " + reportingGroup);
         }
         if(facilityType != "" && !facilityType.endsWith( "undefined")){
             WHERE("facilities.typeid = " + facilityType);
         }
         if(program != "" && !program.endsWith("undefined")){
            WHERE("ps.programid = " + program);
         }
     }


     private static String getSortOrder(Map params){
    	String sortOrder = "";

        for (Object entryObject : params.keySet())
        {
            String entry = entryObject.toString();
            if(entry.startsWith("sort-")){
            	if(sortOrder == ""){
            		sortOrder = entry.substring(5) + " " + ((String[])params.get(entry))[0];
            	}else{
            		sortOrder = ", " + entry.substring(5) + " " + ((String[])params.get(entry))[0];
            	}
            }
        }
        return ((sortOrder == "")?"name" : sortOrder);
    }

    public static String getTotalFacilities(Map params){
        String period           = ((String[])params.get("period"))[0];
        String reportingGroup   = ((String[])params.get("rgroup"))[0];
        String facilityType     = ((String[])params.get("ftype"))[0];
        String program          = ((String[])params.get("program"))[0];

        BEGIN();
        SELECT("COUNT (*)");
        FROM("facilities");
        INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id") ;
        INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
        INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.id and ps.programid = rgps.programid");
        writePredicates(program, period, reportingGroup, facilityType);
        return SQL();
    }

     public static String getTotalNonReportingFacilities(Map params){
         String period           = ((String[])params.get("period"))[0];
         String reportingGroup   = ((String[])params.get("rgroup"))[0];
         String facilityType     = ((String[])params.get("ftype"))[0];
         String program          = ((String[])params.get("program"))[0];

         BEGIN();
         SELECT("COUNT (*)");
         FROM("facilities");
         INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id") ;
         INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
         INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.id and ps.programid = rgps.programid");
         writePredicates(program, period, reportingGroup, facilityType);
         return SQL();
     }



    public static String getSummaryQuery(Map params){

        String period           = ((String[])params.get("period"))[0];
        String reportingGroup   = ((String[])params.get("rgroup"))[0];
        String facilityType     = ((String[])params.get("ftype"))[0];
        String program          = ((String[])params.get("program"))[0];

        BEGIN();
        SELECT("'Non Reporting Facilities' AS name");
        SELECT("COUNT (*)");
        FROM("facilities");
        INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id") ;
        INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id") ;
        INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.id and ps.programid = rgps.programid");
        WHERE("facilities.id not in (select r.facilityid from requisitions r where r.periodid = " + period + " and r.programid = " + program + ")");
        writePredicates(program, period, reportingGroup, facilityType);

        String query = SQL();
        RESET();
        BEGIN();
        SELECT("'Reporting for this Program' AS name");
        SELECT("COUNT (*)");
        FROM("facilities");
        INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id") ;
        INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
        INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.id and ps.programid = rgps.programid");
        writePredicates(program, period, reportingGroup, facilityType);
        query += " UNION " + SQL();
        return query;
//        UNION();
//        SELECT("'All Facilities in Reporting Group' AS name");
//        SELECT("COUNT (*)");
//        FROM("facilities");
//        INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
//        WHERE("rgm.requisitiongroupid = " + reportingGroup);
        //return query;
    }
}
