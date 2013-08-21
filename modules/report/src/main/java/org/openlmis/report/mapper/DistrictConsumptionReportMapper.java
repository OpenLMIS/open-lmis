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
import org.openlmis.report.builder.DistrictConsumptionQueryBuilder;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.report.DistrictConsumptionReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * User: Wolde
 * Date: 5/24/13
 */
@Repository
public interface DistrictConsumptionReportMapper {
    @SelectProvider(type=DistrictConsumptionQueryBuilder.class, method="SelectFilteredSortedPagedRecords")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<DistrictConsumptionReport> getFilteredSortedPagedAdjustmentSummaryReport(
            @Param("filterCriteria") ReportData filterCriteria,
            @Param("SortCriteria") Map<String, String[]> SortCriteria ,
            @Param("RowBounds")RowBounds rowBounds
    );

    @SelectProvider(type=DistrictConsumptionQueryBuilder.class, method="SelectFilteredSortedPagedRecordsCount")
    public Integer getFilteredSortedPagedAdjustmentSummaryReportCount(@Param("filterCriteria") ReportData filterCriteria);

}
