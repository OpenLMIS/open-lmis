
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

import org.openlmis.report.model.params.ReplacementPlanReportParam;

import java.util.Map;

public class ReplacementPlanSummaryQueryBuilder {

    public static String getQuery(Map params) {
        ReplacementPlanReportParam filter = (ReplacementPlanReportParam) params.get("filterCriteria");

        return (" SELECT FACILITIES.id facilityId, FACILITIES.name facilityName,facility_Types.name facilityTypeName,vw_districts.region_name Region, vw_districts.district_name district, " +
                "COALESCE(x.total,0) TOTAL_YEAR1,replacementYearOne,  " +
                "COALESCE(y.total,0) TOTAL_YEAR2,replacementYearTwo ,COALESCE(z.total,0) TOTAL_YEAR3, replacementYearThree,  " +
                "COALESCE(L.total,0) TOTAL_YEAR4,replacementYearFour,COALESCE(M.total,0) TOTAL_YEAR5,replacementYearFive,  coalesce(this_year_cost,0) this_year_cost " +
                "FROM FACILITIES    " +
                "LEFT JOIN facility_Types ON FACILITIES.typeId = facility_Types.ID " +
                "LEFT JOIN vw_districts ON FACILITIES.geographicZoneId = vw_districts.district_Id " +

                "LEFT JOIN  " +
                "  (select facilityId,replacementYearOne,sum(purchaseprice) this_year_cost,  " +
                "  count(*) total " +
                "  FROM vw_replacement_plan_summary " +
                "  WHERE coalesce(yearofinstallation,0) <= coalesce(this_year,0)    ") + getPredicates(filter) + "  GROUP BY facilityId,replacementYearOne " +
                "  )X ON FACILITIES.ID = X.FACILITYID  " +
                "LEFT JOIN   " +
                "  (select facilityId, replacementYearTwo, " +
                "  count(*) total  " +
                "  FROM vw_replacement_plan_summary  " +
                "  WHERE coalesce(yearofinstallation,0) = coalesce(second_year,0)  " + getPredicates(filter) + "  GROUP BY facilityId,replacementYearTwo)Y ON FACILITIES.ID =Y.facilityId " +
                "LEFT JOIN  " +
                "  (select facilityId, replacementYearThree,  " +
                "  count(*) total  " +
                "  FROM vw_replacement_plan_summary " +
                "  WHERE coalesce(yearofinstallation,0) = coalesce(third_year,0) " + getPredicates(filter) + "  GROUP BY facilityId,replacementYearThree)Z ON FACILITIES.ID =z.facilityId " +
                "LEFT JOIN  " +
                "  (select facilityId, replacementYearFour, " +
                "  count(*) total  " +
                "  FROM vw_replacement_plan_summary  " +
                "  WHERE coalesce(yearofinstallation,0) = coalesce(fourth_year,0) " + getPredicates(filter) + "  GROUP BY facilityId,replacementYearFour)L ON FACILITIES.ID = L.facilityId " +
                "LEFT JOIN  " +
                "  (select facilityId, replacementYearFive,  " +
                "  count(*) total " +
                "  FROM vw_replacement_plan_summary " +
                "  WHERE coalesce(yearofinstallation,0) = coalesce(fifth_year,0)  " + getPredicates(filter) + "  GROUP BY facilityId,replacementYearFive)M ON FACILITIES.ID = M.facilityId  " +
                "  where (x.total>0 or y.total>0 or z.total>0 or  l.total>0 or  m.total>0) " +
                "  ORDER BY facility_Types.levelId";
    }


    private static String getPredicates(ReplacementPlanReportParam params) {

        String predicate = " ";

        predicate += "  and programId = " + params.getProgramId();

        String facilityLevel = params.getFacilityLevel();
        if (facilityLevel.isEmpty()
                || facilityLevel.equalsIgnoreCase("cvs")
                || facilityLevel.equalsIgnoreCase("rvs")
                || facilityLevel.equalsIgnoreCase("dvs")) {
            predicate += " and facilityTypeCode =  #{filterCriteria.facilityLevel}";
        } else {
            predicate += "  and facilityTypeCode NOT IN ('cvs','rvs','dvs') ";
        }

        if (!params.getFacilityIds().isEmpty()) {

            predicate += " and facilityId = ANY (#{filterCriteria.facilityIds}::INT[]) ";

        }

        if (params.getDisaggregated() && params.getStatus() != null && !params.getStatus().equals("undefined") && !params.getStatus().isEmpty() && !params.getStatus().equals("0") && !params.getStatus().equals("-1")) {
            predicate += " and status =  #{filterCriteria.status}";
        }


        return predicate;

    }



    public static String getEquipmentListData(Map params) {

        ReplacementPlanReportParam filter = (ReplacementPlanReportParam) params.get("filterCriteria");

        return "   SELECT region,District,facilityName,facilityTypeName,model,brand,capacity,sourceofEnergy sourceOfEnergy,serialNumber,age total,sum(break_down) breakDown,working_status workingStatus,status,sum(purchaseprice) purchasePrice   " +
                "  FROM vw_replacement_plan_summary  " +

                "  WHERE facilityId =  " + filter.getFacility() +
                "  AND ( (COALESCE(yearofinstallation,0) <= COALESCE(this_year,0) AND replacementYearOne =  " + filter.getPlannedYear() + ")   " +
                "  OR  (COALESCE(yearofinstallation,0) = COALESCE(second_year,0) AND replacementYearTwo = " + filter.getPlannedYear() + ")  " +

                "  OR (COALESCE(yearofinstallation,0) = COALESCE(third_year,0) AND replacementYearThree = " + filter.getPlannedYear() + ")   " +
                "  OR (COALESCE(yearofinstallation,0) = COALESCE(fourth_year,0) AND replacementYearFour= " + filter.getPlannedYear() + ")   " +

                "  OR (COALESCE(yearofinstallation,0) = COALESCE(fifth_year,0) AND replacementYearFive= " + filter.getPlannedYear() + "))  " +

                getPredicates(filter) +


                "  GROUP BY   " +
                "  region,District,capacity,facilityName,model,brand,sourceofEnergy ,serialNumber,age,facilityTypeName,working_status,status    " +
                "  ORDER BY facilityName  ";


    }


}
