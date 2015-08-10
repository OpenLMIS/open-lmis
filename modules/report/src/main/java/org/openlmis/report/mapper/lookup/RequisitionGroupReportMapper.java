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

import org.apache.ibatis.annotations.Select;
import org.openlmis.report.model.dto.RequisitionGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionGroupReportMapper {

    @Select("SELECT id, name, code " +
            "   FROM " +
            "       requisition_groups order by name")
    List<RequisitionGroup> getAll();

    @Select("SELECT id, name, code " +
            "   FROM " +
            "       requisition_groups where id = #{param1} order by name")
    List<RequisitionGroup> getById(int id);

    @Select("SELECT distinct g.id, g.name, g.code " +
            "   FROM " +
            "       requisition_groups g" +
            "       join requisition_group_program_schedules ps on ps.requisitiongroupid = g.id " +
            " where " +
            " ps.programid = cast( #{param1} as int4) and ps.scheduleid = cast( #{param2} as int4) " +
            " and  g.id in (select rgm.requisitiongroupid from requisition_group_members rgm join programs_supported ps on rgm.facilityid = ps.facilityid where ps.programid = cast(#{param1} as int4) ) " +
            " order by g.name")
    List<RequisitionGroup> getByProgramAndSchedule(int program, int schedule);

    @Select("SELECT distinct g.id, g.name, g.code " +
            "   FROM " +
            "       requisition_groups g" +
            "       join requisition_group_program_schedules ps on ps.requisitiongroupid = g.id " +
            " where " +
            " ps.programid = cast( #{param1} as int4) " +
            " and  g.id in (select rgm.requisitiongroupid from requisition_group_members rgm join programs_supported ps on rgm.facilityid = ps.facilityid where ps.programid = cast(#{param1} as int4) ) " +
            " order by g.name")
    List<RequisitionGroup> getByProgram(int program);

}
