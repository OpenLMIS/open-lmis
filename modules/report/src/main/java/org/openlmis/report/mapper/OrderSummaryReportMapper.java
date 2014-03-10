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

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.User;
import org.openlmis.report.builder.OrderSummaryQueryBuilder;
import org.openlmis.report.model.ReportParameter;
import org.openlmis.report.model.report.OrderSummaryReport;
import org.openlmis.rnr.domain.RequisitionStatusChange;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OrderSummaryReportMapper {

  @SelectProvider(type = OrderSummaryQueryBuilder.class, method = "SelectFilteredSortedPagedRecords")
  @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = 10, timeout = 0, useCache = true, flushCache = true)
  public List<OrderSummaryReport> getOrderSummaryReport(
    @Param("filterCriteria") ReportParameter filterCriteria,
    @Param("sortCriteria") Map params,
    @Param("RowBounds") RowBounds rowBounds
  );

  @Select("select * from requisition_status_changes where rnrid = #{rnrId} and status = #{status} order by id desc")
  @Results(value = {@Result(property = "createdBy", column = "createdBy", javaType = User.class,
      one = @One(select = "org.openlmis.core.repository.mapper.UserMapper.getById"))})
  public List<RequisitionStatusChange> getLastUsersWhoActedOnRnr(@Param("rnrId")Long rnrid,
                                                                  @Param("status")String status);

  @Select("select max(id) from requisitions where facilityId = #{facilityId} and programId = #{programId} and periodId = #{periodId}")
  public Long getRequisitionId(@Param("facilityId") Long facilityId,
                               @Param("programId") Long programId,
                               @Param("periodId") Long periodId);
}
