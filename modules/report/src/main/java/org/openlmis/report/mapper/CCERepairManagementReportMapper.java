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

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.builder.CCERepairManagementReportQueryBuilder;
import org.openlmis.report.model.params.CCERepairManagementEquipmentListParam;
import org.openlmis.report.model.params.CCERepairManagementReportParam;
import org.openlmis.report.model.report.CCERepairManagementEquipmentList;
import org.openlmis.report.model.report.CCERepairManagementReport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CCERepairManagementReportMapper {


    @SelectProvider(type=CCERepairManagementReportQueryBuilder.class, method="SelectEquipmentCountByStatusEnergySql")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=-1,timeout=0,useCache=true,flushCache=true)
    List<CCERepairManagementReport> SelectEquipmentCountByStatusEnergy(
            @Param("filterCriteria") CCERepairManagementReportParam filterCriteria,
            @Param("rowBounds") RowBounds rowBounds,
            @Param("userId") Long userId
    );

    @SelectProvider(type=CCERepairManagementReportQueryBuilder.class, method="EquipmentListSql")
    @Options(resultSetType = ResultSetType.SCROLL_SENSITIVE, fetchSize=-1,timeout=0,useCache=true,flushCache=true)
    List<CCERepairManagementEquipmentList> getEquipmentList(
            @Param("filterCriteria") CCERepairManagementEquipmentListParam filterCriteria,
            @Param("userId") Long userId
    );

}
