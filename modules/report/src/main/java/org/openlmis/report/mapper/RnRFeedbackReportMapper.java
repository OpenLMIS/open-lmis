package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.RnRFeedbackReportQueryBuilder;
import org.openlmis.report.model.report.RnRFeedbackReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * User: Wolde
 * Date: 8/21/13
 * Time: 3:39 AM
 */
@Repository
public interface RnRFeedbackReportMapper {
    @SelectProvider(type = RnRFeedbackReportQueryBuilder.class, method = "getQuery")
    public List<RnRFeedbackReport> getFilteredRnRFeedbackReport(@Param("filterCriteria") Map params, @Param("RowBounds")RowBounds rowBounds);
}
