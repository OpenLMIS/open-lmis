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

import org.openlmis.report.model.params.DistrictSummaryReportParam;


import java.util.Map;

public class DistrictFinancialSummaryQueryBuilder {

    public static String getQuery(Map params){

        DistrictSummaryReportParam filter  = (DistrictSummaryReportParam)params.get("filterCriteria");

        String sql="";

        sql = "\n" +
                "\n" +
                "  WITH temp as (\n" +
                "                     select facilitycode,facility,facilitytype,rgroup,region,\n" +
                "                     sum(fullsupplyitemssubmittedcost) fullsupplyitemssubmittedcost,sum(nonfullsupplyitemssubmittedcost) nonfullsupplyitemssubmittedcost\n" +
                "                     from vw_district_financial_summary\n" +
                writePredicates(filter)+
                "        \n" +
                "                  Group by rgroup,facilitycode,facility,facilitytype,region\n" +
                "                             order by rgroup)\n" +
                "                            select t.facilitycode,t.facility,t.facilitytype,t.rgroup ,(t.fullsupplyitemssubmittedcost+t.nonfullsupplyitemssubmittedcost) totalcost,t.region\n" +
                "                              from temp t\n" +
                "                               INNER JOIN (select rgroup from temp GROUP BY rgroup order by rgroup) temp2 ON t.rgroup= temp2.rgroup ";
        return sql;
    }

    private static String writePredicates(DistrictSummaryReportParam filter){
        String predicate="";
        predicate = " WHERE status in ('IN_APPROVAL','APPROVED','RELEASED') ";

        if(filter != null){


            if (filter.getRgroupId() != 0 && filter.getRgroupId() != -1) {
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " rgroupid = #{filterCriteria.rgroupId}";
            }

            if(filter.getPeriodId() != 0 && filter.getPeriodId()!=-1){
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " periodid= #{filterCriteria.periodId}";
            }
            if(filter.getScheduleId() != 0 && filter.getScheduleId() !=-1){
                predicate = predicate.isEmpty() ?" where " : predicate + " and ";
                predicate = predicate + " scheduleid= #{filterCriteria.scheduleId}";
            }

            if(filter.getProgramId() != 0 && filter.getProgramId() != -1){
                predicate = predicate.isEmpty() ?" where " : predicate +  " and ";
                predicate = predicate + " programid = #{filterCriteria.programId}";
            }
        }

        return predicate;
    }
}
