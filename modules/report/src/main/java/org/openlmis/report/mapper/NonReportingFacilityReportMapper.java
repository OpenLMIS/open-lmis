/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.mapper;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.NonReportingFacilityQueryBuilder;
import org.openlmis.report.model.dto.NameCount;
import org.openlmis.report.model.params.NonReportingFacilityParam;
import org.openlmis.report.model.report.NonReportingFacilityDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NonReportingFacilityReportMapper {


  @SelectProvider(type = NonReportingFacilityQueryBuilder.class, method = "getQuery")
  @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = 10, timeout = 0, useCache = true, flushCache = true)
  public List<NonReportingFacilityDetail> getReport(@Param("filterCriteria") NonReportingFacilityParam params,
                                                    @Param("RowBounds") RowBounds rowBounds,
                                                    @Param("userId") Long userId
  );

  @SelectProvider(type = NonReportingFacilityQueryBuilder.class, method = "getSummaryQuery")
  @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = 10, timeout = 0, useCache = true, flushCache = true)
  public List<NameCount> getReportSummary(@Param("filterCriteria") NonReportingFacilityParam params, @Param("userId") Long userId);

  // Gets the count of the total facility count under the selection criteria
  @SelectProvider(type = NonReportingFacilityQueryBuilder.class, method = "getTotalFacilities")
  @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = 10, timeout = 0, useCache = true, flushCache = true)
  public Double getTotalFacilities(@Param("filterCriteria") NonReportingFacilityParam params, @Param("userId") Long userId);

  // Gets the count of the total facility count that did not report under the selection criteria
  @SelectProvider(type = NonReportingFacilityQueryBuilder.class, method = "getTotalNonReportingFacilities")
  public Double getNonReportingTotalFacilities(@Param("filterCriteria") NonReportingFacilityParam params, @Param("userId") Long userId);

}
