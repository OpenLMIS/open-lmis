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



    @Select("SELECT\n" +
            "  * \n" +
            "FROM (\n" +
            "  SELECT\n" +
            "    ROW_NUMBER() OVER (PARTITION BY t.scheduleid ORDER BY t.startdate desc) AS r,\n" +
            "    t.*\n" +
            "  FROM\n" +
            "    (\n" +
            "--------\n" +
            "select processing_periods.id, scheduleId, startdate, to_char(startdate, 'Month') || '-'|| extract(year from processing_periods.startdate) || '(' || processing_schedules.name || ')'  as Name \n" +
            "from processing_periods\n" +
            "join processing_schedules on scheduleid = processing_schedules.id\n" +
            " order by startdate desc\n" +
            "--------\n" +
            ") t) x\n" +
            "\n" +
            "WHERE\n" +
            "  x.r <= 2\n" +
            "order by r, startdate desc;")
    List<ProcessingPeriod> getLastPeriods();

}

