
/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.builder.vaccineReport;

import org.openlmis.report.util.StringHelper;

import java.util.Map;

public class ReplacementPlanSummaryQueryBuilder {

    public static String getQuery(Map params) {
        if (params.containsKey("param1")) {
            params = (Map) params.get("param1");
        }
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT FACILITIES.id facilityId, FACILITIES.name facilityName, COALESCE(x.total,0) TOTAL_YEAR1,replacementYearOne,  " +
                        "COALESCE(y.total,0) TOTAL_YEAR2,replacementYearTwo ,COALESCE(z.total,0) TOTAL_YEAR3, replacementYearThree,  " +
                        "COALESCE(L.total,0) TOTAL_YEAR4,replacementYearFour,COALESCE(M.total,0) TOTAL_YEAR5,replacementYearFive,  coalesce(this_year_cost,0) this_year_cost " +
                        "FROM FACILITIES    " +
                        "LEFT JOIN facility_Types ON FACILITIES.typeId = facility_Types.ID " +
                        "LEFT JOIN  " +
                        "(  select facilityid,replacementYearOne,sum(purchaseprice) this_year_cost,  " +
                        "  count(equipment_id) total " +
                        "  FROM vw_replacement_plan " +
                        "  WHERE coalesce(yearofinstallation,0) <= coalesce(this_year,0)    "
        ).append(getPredicates(params)).append(
                "  GROUP BY facilityid,replacementyearone " +
                        "  )X ON FACILITIES.ID = X.FACILITYID  " +
                        "LEFT JOIN   " +
                        "(select facilityid, replacementYearTwo, " +
                        "  count(equipment_id) total  " +
                        "  FROM vw_replacement_plan  " +
                        "  WHERE coalesce(yearofinstallation,0) = coalesce(second_year,0)  "
        ).append(getPredicates(params)).append(

                "  GROUP BY facilityid,replacementYearTwo)Y ON FACILITIES.ID =Y.facilityid " +
                        "LEFT JOIN  " +
                        "  (select facilityid, replacementYearThree,  " +
                        "  count(equipment_id) total  " +
                        "  FROM vw_replacement_plan " +
                        "  WHERE coalesce(yearofinstallation,0) = coalesce(third_year,0) "
        ).append(getPredicates(params)).append(

                "  GROUP BY facilityid,replacementYearThree)Z ON FACILITIES.ID =z.facilityid " +
                        "LEFT JOIN  " +
                        "   (select facilityid, replacementYearFour, " +
                        "  count(equipment_id) total  " +
                        "  FROM vw_replacement_plan  " +
                        "  WHERE coalesce(yearofinstallation,0) = coalesce(foUrth_year,0) "
        ).append(getPredicates(params)).append(

                "  GROUP BY facilityid,replacementYearFour)L ON FACILITIES.ID = L.facilityid " +
                        "  LEFT JOIN  " +
                        "   (select facilityid, replacementYearFive,  " +
                        "  count(equipment_id) total " +
                        "  FROM vw_replacement_plan " +
                        "  WHERE coalesce(yearofinstallation,0) = coalesce(fifth_year,0)  "
        ).append(getPredicates(params)).append(

                "  GROUP BY facilityid,replacementYearFive)M ON FACILITIES.ID =M.facilityid  " +
                        "  where (x.total>0 or y.total>0 or z.total>0 or  l.total>0 or  m.total>0) "
        );

        return sql.toString();
    }


    private static String getPredicates(Map params) {

        String predicate = " ";
        String program = StringHelper.isBlank(params, "program") ? null : ((String[]) params.get("program"))[0];
        String facilityType = StringHelper.isBlank(params, "facilityType") ? null : ((String[]) params.get("facilityType"))[0];

        predicate += "  and programId = " + program;

        if (facilityType != null && !facilityType.isEmpty() && !facilityType.endsWith("undefined")) {
            predicate += "  and facilityTypeId =  " + facilityType;
        }

        return predicate;

    }

}
