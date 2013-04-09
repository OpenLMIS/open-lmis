package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.model.FacilityReport;
import org.openlmis.report.model.FacilityReportFilter;
import org.openlmis.report.model.FacilityReportSorter;
import org.openlmis.report.model.ReportData;
import org.springframework.stereotype.Repository;
import static org.apache.ibatis.jdbc.SelectBuilder.*;
import org.apache.ibatis.mapping.SqlSource.*;

import java.util.List;

/**
 */

@Repository
public interface FacilityReportMapper {

    static final String STOR_PROC_FACILITY_REPORT = "select * from facilities_report()";
    static String SORTED_PAGEGED_FILTERED_REPORT = "";

    @Select(STOR_PROC_FACILITY_REPORT)
   // @Options(statementType = StatementType.CALLABLE)
    @Results(value = {
            @Result(column="code", property="code"),
            @Result(column="facilityName", property="facilityName"),
            @Result(column="facilityType", property="facilityType")
    })
    public List<FacilityReport> getAllFacilitiesReportData();

    @SelectProvider(type=FacilityReportQueryBuilder.class, method="SelectFilteredSortedPagedFacilitiesSql")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    //@Select("SELECT id, code, name FROM facilities")
    //@RowBounds(getLimit = 10,getOffset = 10)
    @Results(value = {
            @Result(column="code", property="code"),
            @Result(column="name", property="facilityName"),
            @Result(column="active", property="active"),
            @Result(column="facilityType", property="facilityType"),
            @Result(column="region", property="region"),
            @Result(column="owner", property="owner"),
            @Result(column="phoneNumber", property="phoneNumber"),
            @Result(column="fax", property="fax")
    })
    public  List<FacilityReport> SelectFilteredSortedPagedFacilities(
            @Param("filterCriteria") ReportData filterCriteria,
            @Param("SortCriteria") ReportData SortCriteria ,
            @Param("RowBounds")RowBounds rowBounds
    //        @Param("page") int page,
    //        @Param("pageSize") int pageSize
    );

    @SelectProvider(type=FacilityReportQueryBuilder.class, method="SelectFilteredFacilitiesCountSql")
    //@Selcet("select count(*) from facilities")
    public Integer SelectFilteredFacilitiesCount(@Param("filterCriteria") ReportData filterCriteria);


}
