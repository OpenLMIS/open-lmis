/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.mapper.lookup;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.ProcessingPeriod;
import org.openlmis.report.model.dto.YearSchedulePeriodTree;
import org.springframework.stereotype.Repository;

import java.util.Date;
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

}

