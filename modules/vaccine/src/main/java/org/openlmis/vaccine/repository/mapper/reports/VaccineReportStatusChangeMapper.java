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

package org.openlmis.vaccine.repository.mapper.reports;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.vaccine.domain.reports.ReportStatus;
import org.openlmis.vaccine.domain.reports.ReportStatusChange;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineReportStatusChangeMapper {

  @Insert("INSERT into vaccine_report_status_changes (reportId, status, createdBy, createdDate, modifiedBy, modifiedDate) " +
    " values " +
    " (#{reportId}, #{status}, #{createdBy}, NOW(), #{modifiedBy}, NOW())")
  @Options(useGeneratedKeys = true)
  Integer insert(ReportStatusChange change);

  @Select("SELECT sc.status, sc.reportId, sc.createdDate as date,  u.username, u.firstName, u.lastName  from vaccine_report_status_changes sc join users u on u.id = sc.createdBy where reportId = #{reportId}")
  List<ReportStatusChange> getChangeLogByReportId(@Param("reportId") Long reportId);

  @Select("SELECT sc.status, sc.reportId, sc.createdDate as date, u.username, u.firstName, u.lastName " +
    "from vaccine_report_status_changes sc join users u on u.id = sc.createdBy " +
    "where reportId = #{reportId} and status = #{operation} " +
    "order by sc.createdDate desc limit 1")
  ReportStatusChange getOperationLog(@Param("reportId")Long reportId, @Param("operation") ReportStatus status);

}
