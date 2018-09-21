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
import org.openlmis.report.model.dto.Program;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramReportMapper {
    //TODO need to be refactor about program id
    @Select("SELECT id, name, description, code" +
            " FROM programs" +
            " WHERE id in (1, 3, 5, 6, 10);")
    List<Program> getRequisitionPrograms();

    @Select("SELECT id, name, description, code " +
            "   FROM " +
            "       programs order by name")
    List<Program> getAll();


    @Select("SELECT id, name, description, code " +
            "   FROM " +
            "       programs where id in (select program_id from vw_user_facilities where user_id = #{userId}) " +
            " order by name")
    List<Program> getAllForUser(@Param("userId") Long userId);

    @Select("SELECT * FROM Programs where code = #{code}")
    Program getProgramByCode(@Param("code") String code);

    @Select("SELECT p.id id, p.name as name, p.description description,p.code code \n" +
            "             FROM programs p\n" +
            "   INNER JOIN regimens rg on rg.programid = p.id \n" +
            "   GROUP BY p.name,p.id,p.description,p.code\n" +
            "        ORDER BY name\n")
    List<Program> getAllRegimenPrograms();

    @Select("SELECT DISTINCT p.* \n" +
            "FROM programs p \n" +
            "INNER JOIN role_assignments ra ON p.id = ra.programId \n" +
            "INNER JOIN role_rights rr ON ra.roleId = rr.roleId \n" +
            "WHERE ra.userId = #{userId}\n" +
            "AND ra.supervisoryNodeId IS NOT NULL \n" +
            "AND p.active = TRUE \n" +
            "AND p.push = FALSE \n" +
            " UNION\n" +
            "SELECT DISTINCT p.* \n" +
            "FROM programs p\n" +
            "INNER JOIN programs_supported ps ON p.id = ps.programId\n" +
            "INNER JOIN role_assignments ra ON ra.programId = p.id\n" +
            "INNER JOIN role_rights rr ON rr.roleId = ra.roleId\n" +
            "WHERE ra.supervisoryNodeId IS NULL\n" +
            "AND p.active = TRUE\n" +
            "AND p.push = FALSE\n" +
            "AND ps.active= TRUE\n" +
            "AND ra.userId = #{userId}\n" +
            "ORDER BY name")
    List<Program> getUserSupervisedActivePrograms(@Param("userId") Long userId);

    @Select("   SELECT DISTINCT p.* \n" +
            "            FROM programs p\n" +
            "            INNER JOIN role_assignments ra ON p.id = ra.programId \n" +
            "            INNER JOIN role_rights rr ON ra.roleId = rr.roleId \n" +
            "            WHERE ra.userId = #{userId}\n" +
            "            AND (ra.supervisoryNodeId = #{nodeId} or #{nodeId}=0 ) \n" +
            "            AND p.active = TRUE \n" +
            "            AND p.push = FALSE")
    List<Program> getUserSupervisedActiveProgramsBySupervisoryNode(@Param("userId") Long userId, @Param("nodeId") Long supervisoryNodeId);

    @Select("select * from programs where budgetingapplies = true")
    List<Program> getAllProgramsWithBudgeting();

}
