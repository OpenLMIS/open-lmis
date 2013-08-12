package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.OrderSummaryQueryBuilder;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.report.OrderSummaryReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * User: Wolde
 * Date: 7/11/13
 * Time: 11:42 PM
 */

@Repository
public interface OrderSummaryReportMapper {
    @SelectProvider(type=OrderSummaryQueryBuilder.class, method="SelectFilteredSortedPagedRecords")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<OrderSummaryReport> getFilteredSortedPagedOrderSummaryReport(
            @Param("filterCriteria") ReportData filterCriteria,
            @Param("RowBounds") RowBounds rowBounds
    );


}
