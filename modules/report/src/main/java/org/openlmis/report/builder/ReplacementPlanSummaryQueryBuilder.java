
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

import org.openlmis.report.util.StringHelper;

import java.util.Map;

public class ReplacementPlanSummaryQueryBuilder {

    public static String getQuery(Map params) {

        Long userId = (Long) params.get("userId");

        if (params.containsKey("param1")) {
            params = (Map) params.get("param1");
        }

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
                "  WHERE coalesce(yearofinstallation,0) <= coalesce(this_year,0)    ") + getPredicates(params, userId) + "  GROUP BY facilityId,replacementYearOne " +
                "  )X ON FACILITIES.ID = X.FACILITYID  " +
                "LEFT JOIN   " +
                "  (select facilityId, replacementYearTwo, " +
                "  count(*) total  " +
                "  FROM vw_replacement_plan_summary  " +
                "  WHERE coalesce(yearofinstallation,0) = coalesce(second_year,0)  " + getPredicates(params, userId) + "  GROUP BY facilityId,replacementYearTwo)Y ON FACILITIES.ID =Y.facilityId " +
                "LEFT JOIN  " +
                "  (select facilityId, replacementYearThree,  " +
                "  count(*) total  " +
                "  FROM vw_replacement_plan_summary " +
                "  WHERE coalesce(yearofinstallation,0) = coalesce(third_year,0) " + getPredicates(params, userId) + "  GROUP BY facilityId,replacementYearThree)Z ON FACILITIES.ID =z.facilityId " +
                "LEFT JOIN  " +
                "  (select facilityId, replacementYearFour, " +
                "  count(*) total  " +
                "  FROM vw_replacement_plan_summary  " +
                "  WHERE coalesce(yearofinstallation,0) = coalesce(fourth_year,0) " + getPredicates(params, userId) + "  GROUP BY facilityId,replacementYearFour)L ON FACILITIES.ID = L.facilityId " +
                "LEFT JOIN  " +
                "  (select facilityId, replacementYearFive,  " +
                "  count(*) total " +
                "  FROM vw_replacement_plan_summary " +
                "  WHERE coalesce(yearofinstallation,0) = coalesce(fifth_year,0)  " + getPredicates(params, userId) + "  GROUP BY facilityId,replacementYearFive)M ON FACILITIES.ID = M.facilityId  " +
                "  where (x.total>0 or y.total>0 or z.total>0 or  l.total>0 or  m.total>0) " +
                "  ORDER BY facility_Types.levelId";
    }


    private static String getPredicates(Map params, Long userId) {

                String predicate = " ";
                String program = StringHelper.isBlank(params, "program") ? null : ((String[]) params.get("program"))[0];
                String facilityType = StringHelper.isBlank(params, "facilityType") ? null : ((String[]) params.get("facilityType"))[0];
                String status = StringHelper.isBlank(params, "status") ? null : ((String[]) params.get("status"))[0];
                Boolean disaggregated = StringHelper.isBlank(params, "disaggregated") ? false : Boolean.parseBoolean(StringHelper.getValue(params, "disaggregated"));


                predicate += "  and programId = " + program;

                predicate += "  and facilityId  IN(select facility_id from vw_user_facilities where user_id =  " + userId + "  and program_Id = " + program + ")";


                if (facilityType != null && !facilityType.equals("undefined") && !facilityType.isEmpty() && !facilityType.equals("0") && !facilityType.equals("-1")) {
                    predicate += "  and facilityTypeId =  " + facilityType;
                }

                if (disaggregated && status != null && !status.equals("undefined") && !status.isEmpty() && !status.equals("0") && !status.equals("-1")) {
                    predicate += " and status =  '" + status + "'";
                }


                return predicate;

    }


    public static String getEquipmentListData(Map params) {

                Long userId = (Long) params.get("userId");

                if (params.containsKey("param1")) {
                    params = (Map) params.get("param1");
                }

                String facility = StringHelper.isBlank(params, "facility") ? null : ((String[]) params.get("facility"))[0];
                String plannedYear = StringHelper.isBlank(params, "plannedYear") ? null : ((String[]) params.get("plannedYear"))[0];


                return "   SELECT region,District,facilityName,facilityTypeName,model,brand,capacity,sourceofEnergy sourceOfEnergy,serialNumber,age total,sum(break_down) breakDown,working_status workingStatus,status,sum(purchaseprice) purchasePrice   " +
                        "  FROM vw_replacement_plan_summary  " +

                        "  WHERE facilityId =  " + facility +
                        "  AND ( (COALESCE(yearofinstallation,0) <= COALESCE(this_year,0) AND replacementYearOne =  " + plannedYear + ")   " +
                        "  OR  (COALESCE(yearofinstallation,0) = COALESCE(second_year,0) AND replacementYearTwo = " + plannedYear + ")  " +

                        "  OR (COALESCE(yearofinstallation,0) = COALESCE(third_year,0) AND replacementYearThree = " + plannedYear + ")   " +
                        "  OR (COALESCE(yearofinstallation,0) = COALESCE(fourth_year,0) AND replacementYearFour= " + plannedYear + ")   " +

                        "  OR (COALESCE(yearofinstallation,0) = COALESCE(fifth_year,0) AND replacementYearFive= " + plannedYear + "))  " +

                            getPredicates(params, userId) +


                        "  GROUP BY   " +
                        "  region,District,capacity,facilityName,model,brand,sourceofEnergy ,serialNumber,age,facilityTypeName,working_status,status    " +
                        "  ORDER BY facilityName  ";


            }


}
