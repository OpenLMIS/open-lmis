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
import org.openlmis.report.model.dto.Program;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramReportMapper {

    @Select("SELECT id, name, description, code " +
            "   FROM " +
            "       programs order by name")
    List<Program> getAll();

    @Select("SELECT * FROM Programs where code = #{code}")
    Program getProgramByCode(String code);

    @Select("SELECT p.id id, p.name as name, p.description description,p.code code \n" +
            "             FROM programs p\n" +
            "   INNER JOIN regimens rg on rg.programid = p.id \n" +
            "   GROUP BY p.name,p.id,p.description,p.code\n" +
            "        ORDER BY name\n" )
    List<Program>getAllRegimenPrograms();

    @Select("\n" +
            "SELECT DISTINCT p.* \n" +
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
            "            AND ra.supervisoryNodeId = #{nodeId}\n" +
            "            AND p.active = TRUE \n" +
            "            AND p.push = FALSE")
    List<Program> getUserSupervisedActiveProgramsBySupervisoryNode(@Param("userId") Long userId, @Param("nodeId") Long supervisoryNodeId);

    @Select("select * from programs where budgetingapplies = true")
    List<Program>getAllProgramsWithBudgeting();
}
