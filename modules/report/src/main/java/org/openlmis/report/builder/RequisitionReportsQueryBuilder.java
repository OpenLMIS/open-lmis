package org.openlmis.report.builder;

import org.openlmis.report.model.params.RequisitionReportsParam;
import static org.apache.ibatis.jdbc.SqlBuilder.*;

import java.util.Map;

public class RequisitionReportsQueryBuilder {
    public static String getSubmittedResult(Map params) {
        RequisitionReportsParam filter = (RequisitionReportsParam) params.get("filterCriteria");
        BEGIN();
        SELECT("req.id id");
        SELECT("fac.name facilityName");
        SELECT("zone.name districtName");
        SELECT("parent_zone.name provinceName");
        SELECT("req.emergency emergency");
        SELECT("pro.name programName");
        SELECT("us.username submittedUser");
        SELECT("req.clientsubmittedtime clientSubmittedTime, req.status requisitionStatus, req.modifieddate webSubmittedTime");
        SELECT("rp.periodenddate actualPeriodEnd");
        SELECT("pp.startdate schedulePeriodStart");
        SELECT("pp.enddate schedulePeriodEnd");
        FROM("requisitions req");
        LEFT_OUTER_JOIN("facilities as fac on req.facilityid = fac.id");
        LEFT_OUTER_JOIN("geographic_zones as zone on fac.geographiczoneid = zone.id");
        LEFT_OUTER_JOIN("geographic_zones as parent_zone on zone.parentid = parent_zone.id");
        LEFT_OUTER_JOIN("programs as pro on req.programid = pro.id");
        LEFT_OUTER_JOIN("users as us on req.modifiedby=us.id");
        LEFT_OUTER_JOIN("requisition_periods as rp on req.id = rp.rnrid");
        LEFT_OUTER_JOIN("processing_periods as pp on req.periodid = pp.id");
        writePredicates(filter);

        return SQL();
    }

    private static void writePredicates(RequisitionReportsParam filter) {
        WHERE("pp.startDate >= #{filterCriteria.startTime}");
        WHERE("pp.endDate <= #{filterCriteria.endTime}");

        if(filter != null) {
            if(filter.getProvinceId() != null && !filter.getProvinceId().equals("")) {
                WHERE("parent_zone.id = #{filterCriteria.provinceId}");
            }
            if(filter.getDistrictId() != null && !filter.getDistrictId().equals("")) {
                WHERE("zone.id = #{filterCriteria.districtId}");
            }
            if(filter.getFacilityId() != null && !filter.getFacilityId().equals("")) {
                WHERE("fac.id = #{filterCriteria.facilityId}");
            }
            if(filter.getProgramIds() != null) {
                WHERE(getProgramSqlConditions(filter));
            }
        }
    }

    private static String getProgramSqlConditions(RequisitionReportsParam filter) {
        StringBuilder conditions = new StringBuilder("pro.id in (");
        for(Integer programId : filter.getProgramIds()) {
            conditions.append(programId + ",");
        }
        conditions.deleteCharAt(conditions.length() - 1);
        conditions.append(")");

        return conditions.toString();
    }
}