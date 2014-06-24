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
import org.openlmis.report.builder.OrderFillRateQueryBuilder;
import org.openlmis.report.model.ReportData;
import org.openlmis.report.model.ReportParameter;
import org.openlmis.report.model.report.OrderFillRateReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OrderFillRateReportMapper {

    @SelectProvider(type=OrderFillRateQueryBuilder.class, method="getQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<OrderFillRateReport> getReport(@Param("filterCriteria") ReportParameter filterCriteria,
                                               @Param("SortCriteria") Map<String, String[]> SortCriteria,
                                               @Param("RowBounds") RowBounds rowBounds);

    @SelectProvider(type=OrderFillRateQueryBuilder.class, method="getOrderFillRateQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<OrderFillRateReport> getReportData(@Param("filterCriteria") ReportParameter filterCriteria,
                                               @Param("SortCriteria") Map<String, String[]> SortCriteria,
                                               @Param("RowBounds") RowBounds rowBounds);

    @SelectProvider(type=OrderFillRateQueryBuilder.class, method="getTotalProductsOrdered")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<Integer> getReportTotalQuantityOrdered(Map params);

    @SelectProvider(type=OrderFillRateQueryBuilder.class, method="getTotalProductsReceived")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<Integer> getReportTotalQuantityReceived(Map params);

}
