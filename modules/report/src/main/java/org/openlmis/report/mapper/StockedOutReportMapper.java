package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.StockedOutReportQueryBuilder;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.report.StockedOutReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * User: mahmed
 * Date: 7/27/13
 * Time: 4:47 PM
 */
@Repository
public interface StockedOutReportMapper {

    @SelectProvider(type=StockedOutReportQueryBuilder.class, method="getQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<StockedOutReport> getReport( @Param("filterCriteria") ReportData filterCriteria,
                                                 @Param("SortCriteria") Map<String, String[]> SortCriteria ,
                                                 @Param("RowBounds")RowBounds rowBounds);

    @SelectProvider(type=StockedOutReportQueryBuilder.class, method="getTotalCount")
    public Integer getTotal(Map params);
}
