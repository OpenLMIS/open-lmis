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

import org.openlmis.report.model.params.RegimenSummaryReportParam;

import java.util.Map;

public class RegimenSummaryQueryBuilder {

    public static String getRegimenSummaryData(Map params) {

        RegimenSummaryReportParam filter = (RegimenSummaryReportParam) params.get("filterCriteria");
        String sql = "";

        sql = "  SELECT facilitycode,facilityType facilityTypeName,facilityname,district,region,zone, regimen, " +
                "                SUM(patientsontreatment) patientsontreatment, " +
                "                SUM(patientstoinitiatetreatment) patientstoinitiatetreatment, " +
                "                SUM(patientsstoppedtreatment) patientsstoppedtreatment " +
                "                FROM vw_regimen_district_distribution " +
                writePredicates(filter) +
                "                GROUP BY regimen,district,facilityName,facilityType,facilitycode,region,zone  " +
                "                ORDER BY region,regimen";
        return sql;
    }




    public static String getRegimenDistributionData(Map params) {
        RegimenSummaryReportParam filter = (RegimenSummaryReportParam) params.get("filterCriteria");
        String sql = "";

        sql = "WITH temp as (select regimen,district,\n" +
                "SUM(patientsontreatment) patientsontreatment,\n" +
                "SUM(patientstoinitiatetreatment) patientstoinitiatetreatment,\n" +
                "SUM(patientsstoppedtreatment) patientsstoppedtreatment\n" +
                "from vw_regimen_district_distribution\n" +
                writePredicates(filter) +
                "group by regimen,district\n" +
                "order by regimen,district ) \n" +
                "select  t.district,t.regimen,\n" +
                "t.patientsontreatment patientsontreatment,\n" +
                "t.patientstoinitiatetreatment patientsToInitiateTreatment,\n" +
                "t.patientsstoppedtreatment patientsstoppedtreatment,\n" +
                "COALESCE( case when temp2.total > 0 THEN round(((t.patientsontreatment*100)/temp2.total),0) ELSE temp2.total END ) \n" +
                " totalOnTreatmentPercentage, \n" +
                " COALESCE( case when temp2.total2 > 0 THEN round(((t.patientstoinitiatetreatment*100)/temp2.total2),0) ELSE temp2.total2 END ) \n" +
                " totalpatientsToInitiateTreatmentPercentage,\n" +
                " COALESCE( case when temp2.total3 > 0 THEN round(((t.patientsstoppedtreatment*100)/temp2.total3),0) \n" +
                " ELSE temp2.total3 END ) stoppedTreatmentPercentage from temp t\n" +
                "INNER JOIN (select regimen ,SUM(patientsontreatment) total,SUM(patientstoinitiatetreatment) total2,\n" +
                " SUM(patientsstoppedtreatment) total3 from  temp GROUP BY regimen ) temp2 ON t.regimen= temp2.regimen  ";

        return sql;
    }


    private static String writePredicates(RegimenSummaryReportParam filter) {
        String predicate = "";
        predicate = " WHERE status in ('APPROVED','RELEASED') and (patientsontreatment > 0 or patientstoinitiatetreatment > 0 or patientsstoppedtreatment > 0)  ";
        predicate = predicate + " and periodId = " + filter.getPeriodId() + " ";
        if (filter != null) {
            if (filter.getZoneId() != 0) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " zoneId = #{filterCriteria.zoneId} " +
                        " or districtid=#{filterCriteria.zoneId} " +
                        "or regionid = #{filterCriteria.zoneId} " +
                        "or parent = #{filterCriteria.zoneId}";
            }

            if (filter.getScheduleId() != 0) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " scheduleId= #{filterCriteria.scheduleId}";
            }

            if (filter.getProgramId() != 0) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " programId = #{filterCriteria.programId}";
            }
            if (filter.getPeriodId() != 0) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " periodId= #{filterCriteria.periodId}";
            }
            if (filter.getRegimenId() != 0) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " regimenId = #{filterCriteria.regimenId}";
            }
            if (filter.getRegimenCategoryId() != 0) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " categoryId = #{filterCriteria.regimenCategoryId}";
            }
            if (filter.getFacilityId() != 0) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " facilityId = #{filterCriteria.facilityId}";
            }
            if (filter.getFacilityTypeId() != 0) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " facilityTypeId = #{filterCriteria.facilityTypeId}";
            }
            predicate = predicate + " and facilityId in (select facility_id from vw_user_facilities where user_id = #{userId} and program_id = " + filter.getProgramId() + ")";

        }

        return predicate;
    }


    public static String getAggregateRegimenDistribution(Map params) {
        RegimenSummaryReportParam filter = (RegimenSummaryReportParam) params.get("filterCriteria");
        Long userId = (Long) params.get("userId");
        String predicates = "";

        if (filter.getRegimenId() > 0) {
            predicates = predicates + " and regimens.Id = " + filter.getRegimenId();
        }

        if (filter.getRegimenCategoryId() > 0) {
            predicates = predicates + " and regimens.categoryId = " + filter.getRegimenCategoryId();
        }

        if (filter.getZoneId() != 0) {
            predicates = predicates + " and (d.zone_id = " + filter.getZoneId() + " or d.parent = " + filter.getZoneId() + " or d.region_id = " + filter.getZoneId() + " or d.district_id = " + filter.getZoneId() + ") ";
        }
        String query = "";

        query = "SELECT DISTINCT \n" +
                "                li.name regimen,sum(li.patientsontreatment) patientsontreatment, SUM(li.patientstoinitiatetreatment) \n" +
                "                patientstoinitiatetreatment,  \n" +
                "                SUM(li.patientsstoppedtreatment) patientsstoppedtreatment \n" +
                "                FROM regimen_line_items li  \n" +
                "                JOIN requisitions r ON r.id = li.rnrid  \n" +
                "                JOIN facilities f ON r.facilityid = f.id  \n" +
                "                JOIN vw_districts d on d.district_id = f.geographicZoneId \n" +
                "                JOIN requisition_group_members rgm ON rgm.facilityid = r.facilityid \n" +
                "                JOIN programs_supported ps ON ps.programid = r.programid AND r.facilityid = ps.facilityid\n" +
                "                JOIN REGIMENS ON REGIMENS.code = li.code \n" +
                "                JOIN processing_periods pp ON r.periodid = pp.id  \n" +
                "                JOIN requisition_group_program_schedules rgps ON rgps.requisitiongroupid = rgm.requisitiongroupid AND pp.scheduleId = rgps.scheduleId \n" +
                "                WHERE (li.patientsontreatment != 0 or li.patientstoinitiatetreatment != 0 or li.patientsstoppedtreatment != 0) and r.status in ('APPROVED','RELEASED') and " +
                "   f.id in (select facility_id from vw_user_facilities where user_id = " + userId + " and program_id = " + filter.getProgramId() + ") " +
                "   and  pp.id = " + filter.getPeriodId() + " and r.programId= " + filter.getProgramId() + " and r.status in ('APPROVED','RELEASED') " + predicates +
                "                GROUP BY li.name\n" +
                "                ORDER BY li.name ";


        return query;

    }

}
