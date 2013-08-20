package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.SummaryQueryBuilder;
import org.openlmis.report.builder.SupplyStatusQueryBuilder;
import org.openlmis.report.model.report.SummaryReport;
import org.openlmis.report.model.report.SupplyStatusReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SupplyStatusReportMapper {


    @SelectProvider(type=SupplyStatusQueryBuilder.class, method="getQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<SupplyStatusReport> getReport(@Param("filterCriteria")Map params, @Param("RowBounds") RowBounds rowBounds);


    @SelectProvider(type=SupplyStatusQueryBuilder.class, method="getTotalCount")
    public Integer getTotal(Map params);
}
