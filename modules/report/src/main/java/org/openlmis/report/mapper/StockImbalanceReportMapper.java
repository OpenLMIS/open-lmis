package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.StockImbalanceQueryBuilder;
import org.openlmis.report.model.report.StockImbalanceReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * User: Wolde
 * Date: 7/27/13
 * Time: 4:47 PM
 */
@Repository
public interface StockImbalanceReportMapper {

    @SelectProvider(type=StockImbalanceQueryBuilder.class, method="getQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<StockImbalanceReport> getReport(Map params, @Param("RowBounds") RowBounds rowBounds);

}
