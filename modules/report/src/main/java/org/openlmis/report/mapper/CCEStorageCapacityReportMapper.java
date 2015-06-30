package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.CCEStorageCapacityQueryBuilder;
import org.openlmis.report.model.params.CCEStorageCapacityReportParam;
import org.openlmis.report.model.report.CCEStorageCapacityReport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CCEStorageCapacityReportMapper {

    @SelectProvider(type = CCEStorageCapacityQueryBuilder.class, method = "getData")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = -1, timeout = 0, useCache = false, flushCache = false)
    List<CCEStorageCapacityReport> getFilteredSortedCCEStorageCapacityReport(
        @Param("filterCriteria") CCEStorageCapacityReportParam param,
        @Param("rowBounds") RowBounds rowBounds,
        @Param("userId") Long userId
    );
}
