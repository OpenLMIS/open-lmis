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

package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.ProcessingPeriod;
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
    public List<NonReportingFacilityDetail> getReport(@Param("filterCriteria") Map params,
                                                      @Param("RowBounds") RowBounds rowBounds,
                                                      @Param("userId") Long userId
    );

    @SelectProvider(type=NonReportingFacilityQueryBuilder.class, method="getSummaryQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<NameCount> getReportSummary(@Param("filterCriteria") Map params, @Param("userId") Long userId);

    // Gets the count of the total facility count under the selection criteria
    @SelectProvider(type=NonReportingFacilityQueryBuilder.class, method="getTotalFacilities")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<Integer> getTotalFacilities(@Param("filterCriteria") Map params, @Param("userId") Long userId);

    // Gets the count of the total facility count that did not report under the selection criteria
    @SelectProvider(type=NonReportingFacilityQueryBuilder.class, method="getTotalNonReportingFacilities")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<Integer> getNonReportingTotalFacilities(@Param("filterCriteria") Map params, @Param("userId") Long userId);

    //TODO: refactor this out to an appropriate class
    @Select("SELECT id, name " +
            "   FROM " +
            "       facility_types where id = #{id}")
    List<RequisitionGroup> getFacilityType(@Param("id")int id);

    //TODO: refactor this out to an appropriate class
    @Select("SELECT * " +
            "   FROM " +
            "       processing_periods where id = #{id}")
    ProcessingPeriod getPeriodId(@Param("id")int id);

    //TODO: refactor this out to an appropriate class
    @Select("SELECT id, name " +
            "   FROM " +
            "       programs where id = #{id}")
    List<RequisitionGroup> getProgram(@Param("id")int id);

}
