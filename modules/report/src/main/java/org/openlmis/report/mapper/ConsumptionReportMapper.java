package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.ConsumptionQueryBuilder;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.filter.ConsumptionReportFilter;
import org.openlmis.report.model.report.ConsumptionReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * User: Elias
 * Date: 4/11/13
 * Time: 11:25 AM
 */

@Repository
public interface ConsumptionReportMapper {


    @SelectProvider(type=ConsumptionQueryBuilder.class, method="getQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<ConsumptionReport> getReport(Map params);


    @SelectProvider(type=ConsumptionQueryBuilder.class, method="SelectFilteredSortedPagedConsumptionSql")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<ConsumptionReport> getFilteredSortedPagedConsumptionReport(
            @Param("filterCriteria") ReportData filterCriteria,
            @Param("SortCriteria") ReportData SortCriteria ,
            @Param("RowBounds")RowBounds rowBounds
    );

    @SelectProvider(type=ConsumptionQueryBuilder.class, method="SelectFilteredSortedPagedConsumptionCountSql")
    public Integer getFilteredSortedPagedConsumptionReportCount(@Param("filterCriteria") ReportData filterCriteria);
}
