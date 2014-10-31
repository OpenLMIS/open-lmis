package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.LabEquipmentByDonorQueryBuilder;
import org.openlmis.report.model.params.LabEquipmentListReportParam;
import org.openlmis.report.model.report.LabEquipmentsByDonorReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface LabEquipmentByDonorMapper {

    @SelectProvider(type = LabEquipmentByDonorQueryBuilder.class, method = "SelectFilteredSortedPagedRecords")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = -1, timeout = 0, useCache = false, flushCache = false)
    public List<LabEquipmentsByDonorReport> getFilteredLabEquipmentByDonorReport(
            @Param("filterCriteria") LabEquipmentListReportParam labEquipmentReportParam,
            @Param("rowBounds") RowBounds rowBounds,
            @Param("userId") Long userId
    );

}
