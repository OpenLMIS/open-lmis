package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.LabEquipmentListQueryBuilder;
import org.openlmis.report.model.params.LabEquipmentListReportParam;
import org.openlmis.report.model.report.LabEquipmentStatusReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface LabEquipmentMapper {

    @SelectProvider(type = LabEquipmentListQueryBuilder.class, method = "getData")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = -1, timeout = 0, useCache = false, flushCache = false)
    public List<LabEquipmentStatusReport> getFilteredSortedLabEquipmentStatusReport(
            @Param("filterCriteria") LabEquipmentListReportParam labEquipmentReportParam,
            @Param("rowBounds") RowBounds rowBounds,
            @Param("userId") Long userId
    );

    @SelectProvider(type = LabEquipmentListQueryBuilder.class, method = "getNonFunctioningEquipmentWithContract")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = -1, timeout = 0, useCache = false, flushCache = false)
    public List<LabEquipmentStatusReport> getNonFunctioningEquipmentWithContractReport(
            @Param("filterCriteria") LabEquipmentListReportParam labEquipmentReportParam,
            @Param("rowBounds") RowBounds rowBounds,
            @Param("userId") Long userId
    );

    @SelectProvider(type = LabEquipmentListQueryBuilder.class, method = "getFunctioningEquipmentWithContract")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = -1, timeout = 0, useCache = false, flushCache = false)
    public List<LabEquipmentStatusReport> getFunctioningEquipmentWithContractReport(
            @Param("filterCriteria") LabEquipmentListReportParam labEquipmentReportParam,
            @Param("rowBounds") RowBounds rowBounds,
            @Param("userId") Long userId
    );

}
