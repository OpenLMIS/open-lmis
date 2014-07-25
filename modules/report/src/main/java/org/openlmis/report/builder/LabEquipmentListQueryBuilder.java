package org.openlmis.report.builder;


import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.model.report.StockedOutReport;
import org.openlmis.report.util.StringHelper;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class LabEquipmentListQueryBuilder {

    public static String SelectFilteredSortedPagedRecords(Map params){

        Map filterCriteria = (Map) params.get("filterCriteria");
        Long userId = (Long) params.get("userId");

        StringBuilder query = new StringBuilder();
        query.append("select * from vw_equipment_operational_status")
        .append(writePredicates(filterCriteria, userId));

        return query.toString();
    }

    private static String writePredicates(Map params, Long userId){

        StringBuilder predicate = new StringBuilder(" WHERE ");
        String facilityTypeId =  StringHelper.isBlank(params,("facilityType"))  ? null :((String[])params.get("facilityType"))[0];
        String facilityId = StringHelper.isBlank(params,("facility"))  ? null :((String[])params.get("facility"))[0]; //params.get("facility") == null ? null : ((String[])params.get("facility"))[0];
        String period = StringHelper.isBlank(params,("period"))  ? null :((String[])params.get("period"))[0]; //params.get("period") == null ? null : ((String[])params.get("period"))[0];
        String program = StringHelper.isBlank(params,("program"))  ? null :((String[])params.get("program"))[0]; //params.get("program") == null ? null : ((String[])params.get("program"))[0];
        String schedule = StringHelper.isBlank(params,("schedule"))  ? null :((String[])params.get("schedule"))[0]; //params.get("schedule") == null ? null : ((String[])params.get("schedule"))[0];
        String equipmentTypeId = StringHelper.isBlank(params,("equipmentType"))  ? null :((String[])params.get("equipmentType"))[0]; //params.get("equipmentType") == null ? null : ((String[])params.get("equipmentType"))[0];
        String zoneId = StringHelper.isBlank(params,("zone"))  ? null :((String[])params.get("zone"))[0]; //params.get("equipmentType") == null ? null : ((String[])params.get("equipmentType"))[0];

        predicate.append(" pg_id = "+ program);
        predicate.append(" and pp_id = "+ (period.isEmpty()? null : period));
        predicate.append(" and ps_id = "+ schedule);
        predicate.append(" and f_id in (select facility_id from vw_user_facilities where user_id = ").append(userId).append(" and program_id = ").append(program).append(")");

        if (facilityId != null &&  !facilityId.equals("undefined") && !facilityId.isEmpty() && !facilityId.equals("0") &&  !facilityId.equals("-1")) {

           predicate.append(" and f_id = "+ facilityId);
        }

        if (zoneId != null &&  !zoneId.equals("undefined") && !zoneId.isEmpty() && !zoneId.equals("0") &&  !zoneId.equals("-1")) {

            //predicate.append(" and zone_id = "+ zoneId);
            predicate.append(" and ( zone_id = ").append(zoneId).append(" or parent = ").append(zoneId).append(" or region_id = ").append(zoneId).append(" or district_id = ").append(zoneId).append(") ") ;
        }

        if (equipmentTypeId != null &&  !equipmentTypeId.equals("undefined") && !equipmentTypeId.isEmpty() && !equipmentTypeId.equals("0") &&  !equipmentTypeId.equals("-1")) {

            predicate.append(" and eqpt_ty_id = ").append(equipmentTypeId);
        }

        if (facilityTypeId != null &&  !facilityTypeId.equals("undefined") && !facilityTypeId.isEmpty() && !facilityTypeId.equals("0") &&  !facilityTypeId.equals("-1")) {

            predicate.append(" and ft_id = ").append(facilityTypeId);
        }

        return predicate.toString();
    }


}
