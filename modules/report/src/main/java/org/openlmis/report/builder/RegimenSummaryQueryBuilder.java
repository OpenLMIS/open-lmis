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

import org.openlmis.report.model.params.RegimenSummaryReportParam;


import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.WHERE;

public class RegimenSummaryQueryBuilder {

    public static String getData(Map params) {

        RegimenSummaryReportParam filter = (RegimenSummaryReportParam) params.get("filterCriteria");
        String sql = "";
        sql = "WITH temp as ( select regimen,district,facilityname,\n" +
                "SUM(patientsontreatment) patientsontreatment,\n" +
                "SUM(patientstoinitiatetreatment) patientstoinitiatetreatment,\n" +
                "SUM(patientsstoppedtreatment) patientsstoppedtreatment\n" +
                "from vw_regimen_district_distribution\n" +
                writePredicates(filter) +
                "group by regimen,district,facilityname\n" +
                "order by regimen,district ) \n" +
                "  \n" +
                "select  t.district,t.regimen,t.facilityname,\n" +
                "t.patientsontreatment patientsontreatment,\n" +
                "t.patientstoinitiatetreatment patientsToInitiateTreatment,\n" +
                "t.patientsstoppedtreatment patientsstoppedtreatment,\n" +
                "COALESCE( case when temp2.total > 0 THEN round(((t.patientsontreatment*100)/temp2.total),0) ELSE temp2.total END ) \n" +
                " totalOnTreatmentPercentage, \n" +
                " COALESCE( case when temp2.total2 > 0 THEN round(((t.patientstoinitiatetreatment*100)/temp2.total2),0) ELSE temp2.total2 END ) \n" +
                " totalpatientsToInitiateTreatmentPercentage,\n" +
                " COALESCE( case when temp2.total3 > 0 THEN round(((t.patientsstoppedtreatment*100)/temp2.total3),0) \n" +
                " ELSE temp2.total3 END ) stoppedTreatmentPercentage from temp t\n" +
                "INNER JOIN (select district,SUM(patientsontreatment) total,SUM(patientstoinitiatetreatment) total2,\n" +
                " SUM(patientsstoppedtreatment) total3 from  temp GROUP BY district ) temp2 ON t.district= temp2.district  ";

        return sql;
    }


    private static String writePredicates(RegimenSummaryReportParam filter) {
        String predicate = "";
        predicate = " WHERE status in ('APPROVED','RELEASED') ";
        if (filter != null) {
            if (filter.getZoneId() != 0) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " zoneId = #{filterCriteria.zoneId}";
            }

            if (filter.getScheduleId() != 0 && filter.getScheduleId() != -1) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " scheduleid= #{filterCriteria.scheduleId}";
            }

            if (filter.getProgramId() != 0 && filter.getProgramId() != -1) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " programid = #{filterCriteria.programId}";
            }
            if (filter.getPeriodId() != 0 && filter.getPeriodId() != -1) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " periodid= #{filterCriteria.periodId}";
            }
            if (filter.getRegimenId() != 0 && filter.getRegimenId() != -1) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " regimenid = #{filterCriteria.regimenId}";
            }
            if (filter.getRegimenCategoryId() != 0) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " categoryid = #{filterCriteria.regimenCategoryId}";
            }
            if (filter.getFacilityId() != 0) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " facilityid = #{filterCriteria.facilityId}";
            }
            if (filter.getFacilityTypeId() != 0) {
                predicate = predicate.isEmpty() ? " where " : predicate + " and ";
                predicate = predicate + " facilitytypeid = #{filterCriteria.facilityTypeId}";
            }

        }

        return predicate;
    }


    public static String getAggregateRegimenDistribution(Map params) {
        RegimenSummaryReportParam filter = (RegimenSummaryReportParam) params.get("filterCriteria");
        String predicates = "";

        if (filter.getRegimenId() > 0) {
            predicates = predicates + " and regimens.id = " + filter.getRegimenId();
        }

        if (filter.getRegimenCategoryId() > 0) {
            predicates = predicates + " and regimens.categoryId = " + filter.getRegimenCategoryId();
        }

        if (filter.getZoneId() != 0) {
            predicates = predicates + " and ( f.geographicZoneId = " + filter.getZoneId() + " or gz.parentId = " + filter.getZoneId() + " or zone.parentId = " + filter.getZoneId() + " or c.parentId = " + filter.getZoneId() + ") ";
        }
        String query = "";
        query = "SELECT DISTINCT\n" +
                " regimens.name regimen,sum(li.patientsontreatment) patientsontreatment, SUM(li.patientstoinitiatetreatment)\n" +
                "patientstoinitiatetreatment, \n" +
                "SUM(li.patientsstoppedtreatment) patientsstoppedtreatment" +
                "   FROM regimen_line_items li\n" +
                "   JOIN requisitions r ON r.id = li.rnrid\n" +
                "   JOIN facilities f ON r.facilityid = f.id\n" +
                "   JOIN geographic_zones gz ON gz.id = f.geographiczoneid\n" +
                "   JOIN geographic_zones zone ON gz.parentid = zone.id\n" +
                "   JOIN geographic_zones c ON zone.parentid = c.id\n" +
                "   JOIN requisition_group_members rgm ON rgm.facilityid = r.facilityid\n" +
                "   JOIN programs_supported ps ON ps.programid = r.programid AND r.facilityid = ps.facilityid\n" +
                "   JOIN processing_periods pp ON r.periodid = pp.id\n" +
                "   JOIN requisition_group_program_schedules rgps ON rgps.requisitiongroupid = rgm.requisitiongroupid AND pp.scheduleid = rgps.scheduleid\n" +
                "   JOIN regimens ON r.programid = regimens.programid\n" +
                "   WHERE r.periodid = " + filter.getPeriodId() + " and r.programId =  " + filter.getProgramId() + "and r.status in ('APPROVED','RELEASED') " + predicates +
                "   GROUP BY regimens.name\n" +
                "   ORDER BY regimens.name ";

        return query;

    }


    public static String getRegimenDistributionData(Map params) {
        RegimenSummaryReportParam filter = (RegimenSummaryReportParam) params.get("filterCriteria");
        String sql = "";

        sql = "WITH temp as ( select regimen,district,\n" +
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

}
