package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.DistrictConsumptionQueryBuilder;
import org.openlmis.report.builder.LabEquipmentListQueryBuilder;
import org.openlmis.report.model.ReportParameter;
import org.openlmis.report.model.report.DistrictConsumptionReport;
import org.openlmis.report.model.report.LabEquipmentStatusReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface LabEquipmentMapper {

    @SelectProvider(type = LabEquipmentListQueryBuilder.class, method = "SelectFilteredSortedPagedRecords")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = -1, timeout = 0, useCache = false, flushCache = false)
    public List<LabEquipmentStatusReport> getFilteredSortedLabEquipmentStatusReport(
            @Param("filterCriteria") Map<String, String[]> params,
            @Param("rowBounds") RowBounds rowBounds
    );

}
