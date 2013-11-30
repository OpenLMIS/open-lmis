package org.openlmis.report.builder;

import org.openlmis.report.model.filter.RegimenSummaryReportFilter;
import org.openlmis.report.model.report.RegimenSummaryReport;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/**
 * Created with IntelliJ IDEA.
 * User: Hassan
 * Date: 10/28/13
 * Time: 6:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegimenSummaryQueryBuilder {

    public static String getQuery(Map params){

    RegimenSummaryReportFilter filter  = (RegimenSummaryReportFilter)params.get("filterCriteria");

       String sql ="WITH temp as ( select regimen,program,regimenid,district,status,code,programid,SUM(patientsontreatment) patientsontreatment,SUM(patientstoinitiatetreatment) patientstoinitiatetreatment,SUM(patientsstoppedtreatment) patientsstoppedtreatment,regimencategory,\n" +
               "                 period,periodid,rgroup,rgroupid, schedule,scheduleid,zoneid,regimencategorydisplayorder \n" +
               "                 from vw_regimen_summary \n" +
               writePredicates(filter)+
               "                \n" +
               "                group by regimen,regimencategory,program,district,code,patientsontreatment,period,periodid,zoneid,status\n" +
               "                ,rgroup,regimencategory,schedule,district,regimenid,programid,rgroupid,scheduleid,regimencategorydisplayorder\n" +
               "                 order by regimen,district)\n" +
               "                \n" +
               "                select distinct t.district district, t.regimen regimen,t.regimenid regimenid,program,\n" +
               "                t.regimencategory regimencategory,t.code code,t.patientsontreatment patientsontreatment,t.status,\n" +
               "                t.patientstoinitiatetreatment patientsToInitiateTreatment,\n" +
               "                t.programid,t.rgroupid rgroupid,t.zoneid zoneid,rgroup,t.schedule schedule,t.scheduleid scheduleid,t.periodid periodid,t.regimencategorydisplayorder regimencategorydisplayorder,\n" +
               "                t.period period, \n" +
               "                case when temp2.total > 0 THEN round(((t.patientsontreatment*100)/temp2.total),1) ELSE temp2.total END totalOnTreatmentPercentage,\n" +
               "                case when temp2.total2 > 0 THEN round(((t.patientstoinitiatetreatment*100)/temp2.total2),1) ELSE temp2.total2 END totalpatientsToInitiateTreatmentPercentage  \n" +
               "             \n" +
               "                from temp t\n" +
               "                \n" +
               "                INNER JOIN (select regimen,SUM(patientsontreatment) total,SUM(patientstoinitiatetreatment) total2 from temp GROUP BY regimen order by regimen) temp2 ON t.regimen= temp2.regimen ";
      return sql;
    }


    private static String writePredicates(RegimenSummaryReportFilter filter){
        String predicate = " WHERE status in ('APPROVED','RELEASED') ";
        if(filter != null){
            if (!filter.getRegimenId().equals("")) {
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " code = #{filterCriteria.regimenId}";
            }
            if (filter.getZoneId() != 0 && filter.getZoneId() != -1) {
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " zoneid = #{filterCriteria.zoneId}";
            }


            if(filter.getRegimenCategoryId() != 0 && filter.getRegimenCategoryId()!=-1){
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " regimencategorydisplayorder = #{filterCriteria.regimenCategoryId}";
            }
            if(filter.getRgroupId() != 0 && filter.getRgroupId() != -1){
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " rgroupid = #{filterCriteria.rgroupId}";
            }
            if(filter.getPeriodId() != 0&& filter.getPeriodId() != -1){
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " periodid= #{filterCriteria.periodId}";
            }
            if(filter.getScheduleId() != 0 && filter.getScheduleId() != -1){
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " scheduleid= #{filterCriteria.scheduleId}";
            }
            if(filter.getProgramId() != 0 && filter.getProgramId() !=-1){
                predicate = predicate.isEmpty() ?" where " : predicate +  " and ";
                predicate = predicate + " programid = #{filterCriteria.programId}";
            }

        }

        return predicate;
    }





}
