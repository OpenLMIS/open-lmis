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
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.core.domain.Role;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.report.builder.UserSummaryQueryBuilder;
import org.openlmis.report.model.ReportParameter;
import org.openlmis.report.model.dto.Program;
import org.openlmis.report.model.report.UserSummaryReport;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Repository
public interface UserSummaryReportMapper {

    @SelectProvider(type=UserSummaryQueryBuilder.class, method="getQuery")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=10,timeout=0,useCache=true,flushCache=true)
    public List<UserSummaryReport> getReport( @Param("filterCriteria") ReportParameter filterCriteria,
                                             @Param("SortCriteria") Map<String, String[]> sortCriteria ,
                                             @Param("RowBounds")RowBounds rowBounds);

    @Select("select rolename, count(*) count from vw_user_role_assignments group by rolename")
    public List<HashMap> getUserRoleAssignments();

    @Select("select * from programs where id = #{programId}" )
    Program getProgram(@Param(value = "programId") Long programId);

    @Select("select * from roles where id = #{roleId}" )
    Role getRole(@Param(value = "roleId") Long roleId);

    @Select("select * from supervisory_nodes where id = #{superviosryNodeId}" )
    SupervisoryNode getSuperVisoryNode(@Param(value = "superviosryNodeId") Long superviosryNodeId);
}
