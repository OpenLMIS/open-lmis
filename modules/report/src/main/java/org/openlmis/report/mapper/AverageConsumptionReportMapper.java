package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.openlmis.report.builder.AverageConsumptionQueryBuilder;
import org.openlmis.report.builder.NonReportingFacilityQueryBuilder;
import org.openlmis.report.model.dto.NameCount;
import org.openlmis.report.model.report.AverageConsumptionReport;
import org.openlmis.report.model.report.NonReportingFacility;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public interface AverageConsumptionReportMapper {


    @SelectProvider(type=AverageConsumptionQueryBuilder.class, method="getQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<AverageConsumptionReport> getReportData(Map params);

}
