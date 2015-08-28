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

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.ProcessingPeriod;
import org.openlmis.report.model.dto.YearSchedulePeriodTree;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Repository
public interface ProcessingPeriodReportMapper {

    @Select("SELECT id, name, startdate, enddate, description , scheduleid  " +
            "   FROM " +
            "       processing_periods")
    List<ProcessingPeriod> getAll();

    @Select("SELECT id, name, startdate, enddate, description  " +
            "   FROM processing_periods " +
            "WHERE startdate >= #{startDate, jdbcType=DATE, javaType=java.util.Date, mode=IN} and " +
            "enddate <= #{endDate, jdbcType=DATE, javaType=java.util.Date, mode=IN}")
    List<ProcessingPeriod> getFilteredPeriods(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Select("select EXTRACT(YEAR FROM pp.startdate) as year, ps.name as groupname, pp.name as periodname, pp.id AS periodid, ps.id as groupid\n" +
            " from processing_periods pp " +
            " join processing_schedules ps on pp.scheduleid = ps.id " +
            " order by year,groupname,pp.startdate  asc")
    List<YearSchedulePeriodTree> getYearSchedulePeriodTree();



    @Select("select distinct on (pp.startdate) pp.id, pp.scheduleId, \n" +
            "pp.startdate::date startdate, \n" +
            "to_char(pp.startdate, 'Month') || '-'|| extract(year from pp.startdate) || '(' || pp.name || ')'  as Name\n" +
            "from requisitions r\n" +
            "inner join processing_periods pp on r.periodid = pp.id\n" +
            "where pp.startdate < NOW()\n" +
            "and r.programid = #{programId}\n" +
            "order by pp.startdate desc\n" +
            "limit 4")
    List<ProcessingPeriod> getLastPeriods(@Param("programId")Long programId);

}

