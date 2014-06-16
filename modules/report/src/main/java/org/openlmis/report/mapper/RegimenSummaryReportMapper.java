/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.RegimenSummaryQueryBuilder;
import org.openlmis.report.model.ReportParameter;
import org.openlmis.report.model.report.RegimenSummaryReport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RegimenSummaryReportMapper {

    @SelectProvider(type=RegimenSummaryQueryBuilder.class, method="getData")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<RegimenSummaryReport> getReport(@Param("filterCriteria") ReportParameter filterCriteria,
                                                 @Param("SortCriteria") Map<String, String[]> SortCriteria,
                                                 @Param("RowBounds") RowBounds rowBounds);

    @SelectProvider(type=RegimenSummaryQueryBuilder.class, method="getAggregateRegimenDistribution")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<RegimenSummaryReport> getAggregateReport(@Param("filterCriteria") ReportParameter filterCriteria,
                                                @Param("SortCriteria") Map<String, String[]> SortCriteria,
                                                @Param("RowBounds") RowBounds rowBounds);

    @SelectProvider(type=RegimenSummaryQueryBuilder.class, method="getRegimenDistributionData")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<RegimenSummaryReport> getRegimenDistributionReport(@Param("filterCriteria") ReportParameter filterCriteria,
                                                         @Param("SortCriteria") Map<String, String[]> SortCriteria,
                                                         @Param("RowBounds") RowBounds rowBounds);


}
