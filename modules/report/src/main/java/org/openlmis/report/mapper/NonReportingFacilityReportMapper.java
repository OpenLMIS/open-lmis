/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.NonReportingFacilityQueryBuilder;
import org.openlmis.report.model.dto.NameCount;
import org.openlmis.report.model.dto.RequisitionGroup;
import org.openlmis.report.model.report.NonReportingFacilityDetail;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface NonReportingFacilityReportMapper {


    @SelectProvider(type=NonReportingFacilityQueryBuilder.class, method="getQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<NonReportingFacilityDetail> getReport(Map params, @Param("RowBounds") RowBounds rowBounds);

    @SelectProvider(type=NonReportingFacilityQueryBuilder.class, method="getSummaryQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<NameCount> getReportSummary(Map params);

    // Gets the count of the total facility count under the selection criteria
    @SelectProvider(type=NonReportingFacilityQueryBuilder.class, method="getTotalFacilities")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<Integer> getTotalFacilities(Map params);

    // Gets the count of the total facility count that did not report under the selection criteria
    @SelectProvider(type=NonReportingFacilityQueryBuilder.class, method="getTotalNonReportingFacilities")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<Integer> getNonReportingTotalFacilities(Map params);

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
