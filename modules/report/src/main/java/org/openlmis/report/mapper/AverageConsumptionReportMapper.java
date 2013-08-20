/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.AverageConsumptionQueryBuilder;
import org.openlmis.report.builder.NonReportingFacilityQueryBuilder;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.dto.NameCount;
import org.openlmis.report.model.report.AverageConsumptionReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface AverageConsumptionReportMapper {


    @SelectProvider(type=AverageConsumptionQueryBuilder.class, method="getQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<AverageConsumptionReport> getReportData(Map params);

    @SelectProvider(type=AverageConsumptionQueryBuilder.class, method="SelectFilteredSortedPagedAverageConsumptionSql")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<AverageConsumptionReport> getFilteredSortedPagedAverageConsumptionReport(
            @Param("filterCriteria") ReportData filterCriteria,
            @Param("SortCriteria") Map<String, String[]> SortCriteria ,
            @Param("RowBounds")RowBounds rowBounds
    );

    @SelectProvider(type=AverageConsumptionQueryBuilder.class, method="SelectFilteredSortedPagedAverageConsumptionCountSql")
    public Integer getFilteredSortedPagedAverageConsumptionReportCount(@Param("filterCriteria") ReportData filterCriteria);
}
