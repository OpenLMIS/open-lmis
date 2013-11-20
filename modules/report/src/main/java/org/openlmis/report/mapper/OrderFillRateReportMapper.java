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
import org.openlmis.report.model.report.OrderFillRateReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * User: Hassan
 * Date: 7/27/13
 * Time: 4:47 PM
 */
@Repository
public interface OrderFillRateReportMapper {

    @SelectProvider(type=OrderFillRateQueryBuilder.class, method="getQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<OrderFillRateReport> getReport(@Param("filterCriteria") ReportData filterCriteria,
                                               @Param("SortCriteria") Map<String, String[]> SortCriteria,
                                               @Param("RowBounds") RowBounds rowBounds);

   /* // Gets the count of the total Quantity Received under the selection criteria
    @SelectProvider(type=OrderFillRateQueryBuilder.class, method="getTotalQuantityReceived")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<Integer> getTotalQuantityReceived(Map params);

    // Gets the count of the total Quantity Approved under the selection criteria
    @SelectProvider(type=OrderFillRateQueryBuilder.class, method="getTotalQuantityApproved")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<Integer> getTotalQuantityApproved(Map params); */

    @SelectProvider(type=OrderFillRateQueryBuilder.class, method="getOrderFillRateQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<OrderFillRateReport> getReport(Map params);
}
