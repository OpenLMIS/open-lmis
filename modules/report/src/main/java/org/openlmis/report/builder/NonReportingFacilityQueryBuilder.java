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

import org.openlmis.report.model.params.NonReportingFacilityParam;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import static org.openlmis.report.builder.helpers.RequisitionPredicateHelper.*;

public class NonReportingFacilityQueryBuilder {

  public static final String FILTER_CRITERIA = "filterCriteria";

  public static String getQuery(Map params) {
    NonReportingFacilityParam nonReportingFacilityParam = (NonReportingFacilityParam) params.get(FILTER_CRITERIA);
    return getQueryString(nonReportingFacilityParam);
  }

  private static String getQueryString(NonReportingFacilityParam filterParam) {
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
    WHERE("facilities.id in (select facility_id from vw_user_facilities where user_id = cast( #{userId} as int4) and program_id = cast( #{filterCriteria.program} as int4))");
    WHERE("facilities.id not in (select r.facilityid from requisitions r where r.status not in ('INITIATED', 'SUBMITTED', 'SKIPPED')  and r.periodid = cast (#{filterCriteria.period} as int4) and r.programid = cast( #{filterCriteria.program} as int4) )");
    writePredicates(filterParam);
    ORDER_BY("name");
    return SQL();
  }

  private static void writePredicates(NonReportingFacilityParam filterParams) {

    WHERE(programIsFilteredBy("ps.programId"));
    if (filterParams.getZone() != 0) {
      WHERE(geoZoneIsFilteredBy("gz"));
    }

    if (filterParams.getFacilityType() != 0) {
      WHERE(facilityTypeIsFilteredBy("facilities.typeId"));
    }

  }


  public static String getTotalFacilities(Map params) {
    NonReportingFacilityParam filterParams = (NonReportingFacilityParam) params.get(FILTER_CRITERIA);

    BEGIN();
    SELECT("COUNT (*)");
    FROM("facilities");
    INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id");
    INNER_JOIN("vw_districts gz on gz.district_id = facilities.geographicZoneId");
    INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
    INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
    WHERE("facilities.id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.program})");
    writePredicates(filterParams);
    return SQL();
  }

  public static String getTotalNonReportingFacilities(Map params) {

    NonReportingFacilityParam filterParams = (NonReportingFacilityParam) params.get(FILTER_CRITERIA);
    BEGIN();
    SELECT("COUNT (*)");
    FROM("facilities");
    INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id");
    INNER_JOIN("vw_districts gz on gz.district_id = facilities.geographicZoneId");
    INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
    INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitionGroupId = rgm.requisitionGroupId and ps.programid = rgps.programid");
    WHERE("facilities.id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.program})");
    WHERE("facilities.id not in (select r.facilityid from requisitions r where  r.status not in ('INITIATED', 'SUBMITTED', 'SKIPPED') and r.periodid = cast( #{filterCriteria.period} as int4) and r.programid = cast(#{filterCriteria.program} as int4) )");
    writePredicates(filterParams);
    return SQL();
  }

  public static String getSummaryQuery(Map params) {
    NonReportingFacilityParam filterParams = (NonReportingFacilityParam) params.get(FILTER_CRITERIA);

    BEGIN();
    SELECT("'Non Reporting Facilities' AS name");
    SELECT("COUNT (*)");
    FROM("facilities");
    INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id");
    INNER_JOIN("vw_districts gz on gz.district_id = facilities.geographicZoneId");
    INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
    INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
    WHERE("facilities.id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.program})");
    WHERE("facilities.id not in (select r.facilityid from requisitions r where  r.status not in ('INITIATED', 'SUBMITTED', 'SKIPPED') and r.periodid = cast( #{filterCriteria.period} as int4) and r.programid = cast(#{filterCriteria.program} as int4) )");
    writePredicates(filterParams);

    String query = SQL();
    RESET();
    BEGIN();
    SELECT("'Facilities required to report for this program' AS name");
    SELECT("COUNT (*)");
    FROM("facilities");
    INNER_JOIN("vw_districts gz on gz.district_id = facilities.geographicZoneId");
    INNER_JOIN("programs_supported ps on ps.facilityid = facilities.id");
    INNER_JOIN("requisition_group_members rgm on rgm.facilityid = facilities.id");
    INNER_JOIN("requisition_group_program_schedules rgps on rgps.requisitiongroupid = rgm.requisitiongroupid and ps.programid = rgps.programid");
    WHERE("facilities.id in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = #{filterCriteria.program})");
    writePredicates(filterParams);
    query += " UNION " + SQL();
    return query;
  }
}
