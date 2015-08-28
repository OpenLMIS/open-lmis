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
import org.openlmis.core.domain.SupervisoryNode;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: Issa
 * Date: 3/24/14
 * Time: 3:06 PM
 */
@Repository
public interface SupervisoryNodeReportMapper {
    @Select({"WITH  recursive  supervisoryNodesRec AS ",
            "   (",
            "   SELECT *",
            "   FROM supervisory_nodes ",
            "   WHERE id in  (SELECT DISTINCT s.id FROM  ",
            "       supervisory_nodes s ",
            "       INNER JOIN role_assignments ra ON s.id = ra.supervisoryNodeId  ",
            "       INNER JOIN role_rights rr ON ra.roleId = rr.roleId  ",
            "       WHERE  ra.userId = #{userId} ) ",
            "   UNION ",
            "   SELECT sn.* ",
            "   FROM supervisory_nodes sn ",
            "   JOIN supervisoryNodesRec ",
            "   ON sn.parentId = supervisoryNodesRec.id ",
            "   )",
            "SELECT * FROM supervisoryNodesRec order by name"})
    List<SupervisoryNode> getAllSupervisoryNodesInHierarchyByUser(@Param("userId") Long userId);

    @Select("SELECT DISTINCT s.* FROM  \n" +
            "                   supervisory_nodes s \n" +
            "                   INNER JOIN role_assignments ra ON s.id = ra.supervisoryNodeId  \n" +
            "                   INNER JOIN role_rights rr ON ra.roleId = rr.roleId\n" +
            "                   INNER JOIN programs p ON p.id = ra.programId  \n" +
            "                   WHERE  ra.userId = #{userId} \n" +
            "                   AND p.active = TRUE\n" +
            "                   AND p.push = FALSE \n" +
            "                   ORDER BY s.name")
    List<SupervisoryNode> getAllSupervisoryNodesByUserHavingActiveProgram(@Param("userId") Long userId);

    @Select("WITH  recursive  supervisoryNodesRec AS (\n" +
            "         SELECT *\n" +
            "         FROM supervisory_nodes          \n" +
            "         WHERE id = #{parentNodeId} \n" +
            "         UNION \n" +
            "         SELECT sn.* \n" +
            "         FROM supervisory_nodes sn \n" +
            "         JOIN supervisoryNodesRec \n" +
            "         ON sn.parentId = supervisoryNodesRec.id \n" +
            "         )\n" +
            "      SELECT distinct * FROM supervisoryNodesRec")
    List<SupervisoryNode> getAllSupervisoryNodesByParentNodeId(@Param("parentNodeId") Long parentNodeId);

}
