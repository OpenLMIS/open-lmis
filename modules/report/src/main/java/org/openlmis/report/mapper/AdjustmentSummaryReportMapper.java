package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.AdjustmentSummaryQueryBuilder;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.report.AdjustmentSummaryReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * User: Wolde
 * Date: 5/10/13
 * Time: 3:18 PM
 */
@Repository
public interface AdjustmentSummaryReportMapper {
    @SelectProvider(type=AdjustmentSummaryQueryBuilder.class, method="SelectFilteredSortedPagedRecords")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<AdjustmentSummaryReport> getFilteredSortedPagedAdjustmentSummaryReport(
            @Param("filterCriteria") ReportData filterCriteria,
            @Param("SortCriteria") Map<String, String[]> SortCriteria ,
            @Param("RowBounds")RowBounds rowBounds
    );

    @SelectProvider(type=AdjustmentSummaryQueryBuilder.class, method="SelectFilteredSortedPagedRecordsCount")
    public Integer getFilteredSortedPagedAdjustmentSummaryReportCount(@Param("filterCriteria") ReportData filterCriteria);

}
