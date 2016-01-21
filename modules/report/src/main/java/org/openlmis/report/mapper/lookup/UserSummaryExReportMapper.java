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
package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.openlmis.report.builder.UserSummaryExQueryBuilder;
import org.openlmis.report.model.ReportParameter;
import org.openlmis.report.model.dto.UserRoleAssignmentsReport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSummaryExReportMapper {

  @Select("select supervisorynodename as supervisoryNodeName,rolename as roleName from vw_user_role_assignments " +
      "where roleid=#{roleId} and programid=#{programId} and supervisorynodeid=#{supervisoryNodeId}")
  List<UserRoleAssignmentsReport> getUserRoleAssignments(@Param("roleId") Long roleId, @Param("programId") Long programId, @Param("supervisoryNodeId") Long supervisoryNodeId);

  @SelectProvider(type = UserSummaryExQueryBuilder.class, method = "getQuery")
  @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize = 10, timeout = 0, useCache = true, flushCache = true)
  List<UserRoleAssignmentsReport> getUserRoleAssignment(@Param("filterCriteria") ReportParameter filterCriteria);
}
