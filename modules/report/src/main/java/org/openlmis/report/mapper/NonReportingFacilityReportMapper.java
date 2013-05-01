package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.openlmis.report.builder.NonReportingFacilityQueryBuilder;
import org.openlmis.report.builder.SummaryQueryBuilder;
import org.openlmis.report.model.dto.NameCount;
import org.openlmis.report.model.report.NonReportingFacility;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * User: Elias
 * Date: 4/11/13
 * Time: 11:25 AM
 */

@Repository
public interface NonReportingFacilityReportMapper {


    @SelectProvider(type=NonReportingFacilityQueryBuilder.class, method="getQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<NonReportingFacility> getReport(Map params);

    @SelectProvider(type=NonReportingFacilityQueryBuilder.class, method="getSummaryQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<NameCount> getReportSummary(Map params);
}
