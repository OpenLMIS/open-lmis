package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.NonReportingFacilityQueryBuilder;
import org.openlmis.report.builder.StockedOutReportQueryBuilder;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.dto.NameCount;
import org.openlmis.report.model.dto.RequisitionGroup;
import org.openlmis.report.model.report.NonReportingFacilityDetail;
import org.openlmis.report.model.report.StockedOutFacility;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * User: Elias
 * Date: 4/11/13
 * Time: 11:25 AM
 */

@Repository
public interface StockedOutReportMapper {


    @SelectProvider(type=StockedOutReportQueryBuilder.class, method="getQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<StockedOutFacility> getReport(@Param("filterCriteria")ReportData filterCriteria, @Param("RowBounds") RowBounds rowBounds);

    @SelectProvider(type=StockedOutReportQueryBuilder.class, method="getSummaryQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<NameCount> getReportSummary(Map params);

    // Gets the count of the total facility count under the selection criteria
    @SelectProvider(type=StockedOutReportQueryBuilder.class, method="getTotalFacilities")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<Integer> getTotalFacilities(Map params);

    // Gets the count of the total facility count that did not report under the selection criteria
    @SelectProvider(type=StockedOutReportQueryBuilder.class, method="getStockedOutFacilitiesCount")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<Integer> getStockedOutTotalFacilities(Map params);

    //TODO: refactor this out to an appropriate class
    @Select("SELECT id, name " +
            "   FROM " +
            "       facility_types where id = #{param1}")
    List<RequisitionGroup> getFacilityType(int id);

    //TODO: refactor this out to an appropriate class
    @Select("SELECT id, name " +
            "   FROM " +
            "       processing_periods where id = #{param1}")
    List<RequisitionGroup> getPeriodId(int id);

    //TODO: refactor this out to an appropriate class
    @Select("SELECT id, name " +
            "   FROM " +
            "       programs where id = #{param1}")
    List<RequisitionGroup> getProgram(int id);

}
