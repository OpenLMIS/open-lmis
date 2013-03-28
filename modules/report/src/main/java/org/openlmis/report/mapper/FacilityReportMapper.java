package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;
import org.openlmis.report.model.FacilityReport;
import org.openlmis.report.model.ReportData;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 */

@Repository
public interface FacilityReportMapper {

    static final String STOR_PROC_FACILITY_REPORT = "select * from facilities_report()";

    @Select(STOR_PROC_FACILITY_REPORT)
   // @Options(statementType = StatementType.CALLABLE)
    @Results(value = {
            @Result(column="code", property="code"),
            @Result(column="facilityName", property="facilityName"),
            @Result(column="facilityType", property="facilityType")
    })
    public List<FacilityReport> getAllFacilitiesReportData();

}
