package org.openlmis.report.builder;

import java.util.Map;

/**
 * User: Elias
 * Date: 4/11/13
 * Time: 11:34 AM
 */
public class NonReportingFacilityQueryBuilder {

    public static String getQuery(Map params){

        String period =    ((String[])params.get("period"))[0];
        String reportingGroup = ((String[])params.get("rgroup"))[0] ;
        String facilityType =  ((String[])params.get("ftype"))[0] ;

        String reportingGroupFilter = "";
        if(reportingGroup != "" && !reportingGroup.endsWith( "undefined")){
            reportingGroupFilter = " requisitiongroupid = " + reportingGroup;
        }

        String facilityFilter = "";
        if(facilityType != "" && !facilityType.endsWith( "undefined")){
            facilityFilter = " facilities.typeid = " + facilityType;
        }

        String query = "SELECT facilities.\"id\", " +
                " facilities.code, " +
                " facilities.\"name\", " +
                " gz.\"name\" as location," +
                " ft.name as facilityType " +
                " " +
                "FROM facilities  " +
                " inner join requisition_group_members rgm on rgm.facilityid = facilities.\"id\" " +
                " inner join geographic_zones gz on gz.\"id\" = facilities.geographiczoneid " +
                " inner join facility_types ft on ft.\"id\" = facilities.typeid " +
                "WHERE facilities.\"id\" not in (select r.facilityid from requisitions r where r.periodid = " + period +")  " +
                " " + ((reportingGroupFilter != "" || facilityFilter != "") ? " and " : "") +  reportingGroupFilter +
                ((reportingGroupFilter != "" && facilityFilter != "") ? " and " : "") +
                facilityFilter +
                " ORDER BY facilities.\"id\" ASC, facilities.code ASC, facilities.\"name\" ASC";
            return query;
    }
}
