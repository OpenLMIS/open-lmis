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

public class RegimenSummaryQueryBuilder {

    public static String getQuery(Map params){

    RegimenSummaryReportParam filter  = (RegimenSummaryReportParam)params.get("filterCriteria");

       String sql ="\n" +
               "   WITH temp as ( select rgroup, district,regimen,\n" +
               "   SUM(patientsontreatment) patientsontreatment,\n" +
               "   SUM(patientstoinitiatetreatment) patientstoinitiatetreatment,\n" +
               "   SUM(patientsstoppedtreatment) patientsstoppedtreatment\n" +
               "   from vw_regimen_summary\n" +
              writePredicates(filter)+
               "   group by district, regimen,rgroup\n" +
               "   order by district,regimen,rgroup ) \n" +
               "   select  t.district district,t.rgroup rgroup, t.regimen,\n" +
               "   t.patientsontreatment patientsontreatment,\n" +
               "   t.patientstoinitiatetreatment patientsToInitiateTreatment,\n" +
               "   t.patientsstoppedtreatment patientsstoppedtreatment,\n" +
               "   COALESCE( case when temp2.total2 > 0 THEN round(((t.patientstoinitiatetreatment*100)/temp2.total2),1) ELSE temp2.total2 END ) totalpatientsToInitiateTreatmentPercentage,\n" +
               "   COALESCE( case when temp2.total3 > 0 THEN round(((t.patientsstoppedtreatment*100)/temp2.total3),1) ELSE temp2.total3 END ) stoppedTreatmentPercentage \n" +
               "   from temp t\n" +
               "   INNER JOIN (select  rgroup,SUM(patientsontreatment) total,SUM(patientstoinitiatetreatment) total2,SUM(patientsstoppedtreatment) total3 from \n" +
               "   temp GROUP BY rgroup) temp2 ON t.rgroup= temp2.rgroup\n ";
        return sql;
    }

   private static String writePredicates(RegimenSummaryReportParam filter){
        String predicate="";
       predicate = " WHERE status in ('APPROVED','RELEASED') ";
     if(filter != null){
         /*
            if (filter.getGeographicLevelId() != 0 ) {
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " geographiclevelid = #{filterCriteria.geographicLevelId}";
            }

            if (filter.getZoneId() != 0 ) {
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " zoneid = #{filterCriteria.zoneId}";
            }*/
            if(filter.getRegimenCategoryId() != 0  ){
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " categoryid = #{filterCriteria.regimenCategoryId}";
            }
            if(filter.getRgroupId() != 0){
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " rgroupid = #{filterCriteria.rgroupId}";
            }
            if(filter.getPeriodId() != 0 ){
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " periodid= #{filterCriteria.periodId}";
            }
            if(filter.getScheduleId() != 0 ){
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " scheduleid= #{filterCriteria.scheduleId}";
            }

            if(filter.getProgramId() != 0 ){
                predicate = predicate.isEmpty() ?" where " : predicate +  " and ";
                predicate = predicate + " programid = #{filterCriteria.programId}";
            }
             if (filter.getRegimenId()!=0) {
                 predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                 predicate = predicate + " regimenid = #{filterCriteria.regimenId}";
             }

     }

        return predicate;
    }
}
