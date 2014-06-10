package org.openlmis.report.builder;


import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.model.report.StockedOutReport;

import java.util.Map;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

public class LabEquipmentListQueryBuilder {

    public static String SelectFilteredSortedPagedRecords(Map params){

        Map filterCriteria = (Map) params.get("filterCriteria");

        StringBuilder query = new StringBuilder();
        query.append("select * from vw_equipment_operational_status")
        .append(writePredicates(filterCriteria));

        return query.toString();
    }

    private static String writePredicates(Map params){
        StringBuilder predicate = new StringBuilder(" WHERE ");
        String facilityTypeId =  params.get("facilityType") == null ? null :((String[])params.get("facilityType"))[0];
        String facilityId = params.get("facility") == null ? null : ((String[])params.get("facility"))[0];
        String period = params.get("period") == null ? null : ((String[])params.get("period"))[0];
        String program = params.get("program") == null ? null : ((String[])params.get("program"))[0];
        String rgroup =  params.get("requisitionGroup") == null ? null : ((String[])params.get("requisitionGroup"))[0];
        String schedule = params.get("schedule") == null ? null : ((String[])params.get("schedule"))[0];
        String equipmentTypeId = params.get("equipmentType") == null ? null : ((String[])params.get("equipmentType"))[0];

        predicate.append(" pp_id = "+ (period.isEmpty()? null : period));

        predicate.append(" and pg_id = "+ program);

        predicate.append(" and ps_id = "+ schedule);

        predicate.append(" and rgm_id = "+ rgroup);

        if (facilityId != null &&  !facilityId.equals("undefined") && !facilityId.isEmpty() && !facilityId.equals("0") &&  !facilityId.equals("-1")) {

            predicate.append(" and f_id = "+ facilityId);
        }

        if (equipmentTypeId != null &&  !equipmentTypeId.equals("undefined") && !equipmentTypeId.isEmpty() && !equipmentTypeId.equals("0") &&  !equipmentTypeId.equals("-1")) {

            predicate.append(" and eqpt_ty_id = "+ equipmentTypeId);
        }

        if (facilityTypeId != null &&  !facilityTypeId.equals("undefined") && !facilityTypeId.isEmpty() && !facilityTypeId.equals("0") &&  !facilityTypeId.equals("-1")) {

            predicate.append(" and ft_id = "+ facilityTypeId);
        }

      /*  if (rgroup != null &&  !rgroup.equals("undefined") && !rgroup.isEmpty() && !rgroup.equals("0") &&  !rgroup.equals("-1")) {

            predicate.append(" and rgm_id = "+ rgroup);
        }*/

        return predicate.toString();
    }


}
