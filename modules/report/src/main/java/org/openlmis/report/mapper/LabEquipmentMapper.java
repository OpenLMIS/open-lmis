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
