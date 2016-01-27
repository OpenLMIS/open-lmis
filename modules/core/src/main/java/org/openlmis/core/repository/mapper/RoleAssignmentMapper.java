/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.domain.SupervisoryNode;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * RoleAssignmentMapper maps the RoleAssignment entity to corresponding representation in database. Also provides methods
 * to get Roles for a user on basis of varied criteria like roles on a program having given set of rights etc.
 */
@Repository
public interface RoleAssignmentMapper {

  @Select({"SELECT DISTINCT RA.programId, RA.supervisoryNodeId",
    "FROM role_assignments RA, role_rights RR, supervisory_nodes SN WHERE",
    "RA.supervisoryNodeId = SN.id",
    "AND RA.roleId = RR.roleId",
    "AND RA.userId = #{userId}",
    "AND RR.rightName = #{right}"})
  @Results(value = {@Result(property = "supervisoryNode", column = "supervisoryNodeId", javaType = SupervisoryNode.class,
    one = @One(select = "org.openlmis.core.repository.mapper.SupervisoryNodeMapper.getSupervisoryNode"))})
  List<RoleAssignment> getRoleAssignmentsWithGivenRightForAUser(@Param(value = "right") String rightName,
                                                                @Param(value = "userId") Long userId);

  @Insert("INSERT INTO role_assignments" +
    "(userId, roleId, programId, supervisoryNodeId) VALUES " +
    "(#{userId}, #{roleId}, #{programId}, #{supervisoryNodeId})")
  /**
   * @deprecated should use insert(userId, programId, supervisoryNode, deliveryZone, roleId)
   */
  int insertRoleAssignment(@Param(value = "userId") Long userId,
                           @Param(value = "programId") Long programId, @Param(value = "supervisoryNodeId") Long supervisoryNodeId, @Param(value = "roleId") Long roleId);

  @Delete("DELETE FROM role_assignments WHERE userId=#{id}")
  void deleteAllRoleAssignmentsForUser(Long userId);

  @Select("SELECT userId, programId, supervisoryNodeId, array_agg(roleId) as roleIdsAsString " +
    "FROM role_assignments " +
    "WHERE userId=#{userId} AND programId IS NOT NULL AND supervisoryNodeId IS NOT NULL " +
    "GROUP BY userId, programId, supervisoryNodeId ")
  @Results(value = {@Result(property = "supervisoryNode.id", column = "supervisoryNodeId")})
  List<RoleAssignment> getSupervisorRoles(Long userId);


  @Select({"SELECT userId, programId, array_agg(roleId) as roleIdsAsString ",
    "FROM role_assignments ",
    "WHERE userId=#{userId} AND programId IS NOT NULL AND supervisoryNodeId IS NULL AND deliveryZoneId IS NULL",
    "GROUP BY userId, programId"})
  List<RoleAssignment> getHomeFacilityRoles(Long userId);

  @Select("SELECT RA.userId, RA.programId, array_agg(RA.roleId) as roleIdsAsString " +
    "FROM role_assignments RA INNER JOIN role_rights RR ON RA.roleId = RR.roleId " +
    "WHERE RA.userId=#{userId} AND RA.programId=#{programId} AND RR.rightName = ANY (#{commaSeparatedRights}::VARCHAR[])  AND  supervisoryNodeId IS NULL " +
    "GROUP BY userId, programId")
  List<RoleAssignment> getHomeFacilityRolesForUserOnGivenProgramWithRights(@Param("userId") Long userId, @Param("programId") Long programId, @Param("commaSeparatedRights") String commaSeparatedRights);


  
  @Select({"SELECT DISTINCT RA.userId, array_agg(RA.roleId) as roleIdsAsString",
    "FROM role_assignments RA",
    "INNER JOIN role_rights RR ON RR.roleId = RA.roleId",
    "INNER JOIN rights RT ON RT.name = RR.rightName",
    "WHERE userId = #{userId} AND RT.rightType = 'ADMIN'",
    "GROUP BY userId"})
  RoleAssignment getAdminRole(Long userId);

  @Select({"SELECT DISTINCT RA.userId, array_agg(RA.roleId) as roleIdsAsString",
      "FROM role_assignments RA",
      "INNER JOIN role_rights RR ON RR.roleId = RA.roleId",
      "INNER JOIN rights RT ON RT.name = RR.rightName",
      "WHERE userId = #{userId} AND RT.rightType = 'REPORT'",
      "GROUP BY userId"})
  RoleAssignment getReportRole(Long userId);

  @Insert("INSERT INTO role_assignments" +
    "(userId, roleId, programId, supervisoryNodeId, deliveryZoneId) VALUES " +
    "(#{userId}, #{roleId}, #{programId}, #{supervisoryNode.id}, #{deliveryZone.id})")
  void insert(@Param("userId") Long userId, @Param("programId") Long programId, @Param("supervisoryNode") SupervisoryNode supervisoryNode, @Param("deliveryZone") DeliveryZone deliveryZone, @Param("roleId") long roleId);

  @Select({"SELECT RA.userId, RA.programId, RA.deliveryZoneId, array_agg(RA.roleId) as roleIdsAsString",
    "FROM role_assignments RA",
    "INNER JOIN role_rights RR ON RR.roleId = RA.roleId",
    "INNER JOIN rights RT ON RT.name = RR.rightName",
    "WHERE RA.userId=#{userId} AND RA.programId IS NOT NULL AND RT.rightType = 'ALLOCATION'",
    "GROUP BY RA.userId, RA.programId, RA.deliveryZoneId"})
  @Results(value = {@Result(property = "deliveryZone.id", column = "deliveryZoneId")})
  List<RoleAssignment> getAllocationRoles(Long userId);

  @Select({"SELECT DISTINCT RA.userId, array_agg(RA.roleId) as roleIdsAsString",
    "FROM role_assignments RA",
    "INNER JOIN role_rights RR ON RR.roleId = RA.roleId",
    "INNER JOIN rights RT ON RT.name = RR.rightName",
    "WHERE userId = #{userId} AND RT.rightType = 'REPORTING'",
    "GROUP BY userId, supervisoryNodeId, programId"})
  RoleAssignment getReportingRole(Long userId);
}
