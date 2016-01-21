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

package org.openlmis.help.Repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Role;
import org.openlmis.help.domain.HelpTopic;
import org.openlmis.help.domain.HelpTopicRole;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by seifu on 10/23/2014.
 */
@Repository
public interface HelpTopicRoleMapper {
    @Insert({"INSERT INTO elmis_help_topic_roles" ,
            "( help_topic_id, role_id,is_asigned,was_previosly_assigned, created_by, modifiedby, modifiedDate) " ,
            "VALUES",
            "( #{helpTopic.id}, #{userRole.id},true, true, #{createdBy}, #{modifiedBy}, #{modifiedDate}) "})
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    void insert(HelpTopicRole helpTopicRole);

    @Select({"SELECT DISTINCT " ,
            "  htr.* ," ,
            "   r.name as name, " ,
            "  r.description as description" ,
            "FROM " ,
            "  public.elmis_help_topic_roles htr" ,
            "  INNER JOIN " ,
            "  public.roles r" ,
            " ON " ,
            "  htr.role_id = r.id " ,
            " WHERE htr.help_topic_id= #{helpTopicId}"})
    @Results({
            @Result(column = "is_asigned", property = "currentlyAssigned"),
            @Result(column = "was_previosly_assigned", property = "previoslyAssigned"),
            @Result(column = "role_id", property = "userRole.id"),
            @Result(column = "name", property = "userRole.name"),
            @Result(column = "description", property = "userRole.description")

    })
//            is_asigned , was_previosly_assigned
    List<HelpTopicRole> loadHelpTopicRolesAssignment(@Param(value = "helpTopicId") Long id);

    @Select("SELECT DISTINCT" +
            " r.* " +
            "FROM public.roles r " +
            "where r.id " +
            " NOT IN " +
            " ( SELECT htr.role_id " +
            " FROM elmis_help_topic_roles htr " +
            " WHERE " +
            " htr.help_topic_id=#{roleId})"
                )
    List<Role> loadRolesNotAssignedForHelpTopic(@Param(value = "roleId") Long id);
@Delete("delete from elmis_help_topic_roles htr " +
        "where htr.id= #{id}")
    void delete(@Param(value = "id")Long id);
}
