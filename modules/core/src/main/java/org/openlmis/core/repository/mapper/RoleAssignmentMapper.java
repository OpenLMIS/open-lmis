/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.domain.SupervisoryNode;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleAssignmentMapper {

  @Select({"SELECT RA.*",
    "FROM role_assignments RA, role_rights RR, supervisory_nodes SN WHERE",
    "RA.supervisoryNodeId = SN.id",
    "AND RA.roleId = RR.roleId",
    "AND RA.userId = #{userId}",
    "AND RR.rightName = #{right}"})
  @Results(value = {@Result(property = "supervisoryNode", column = "supervisoryNodeId", javaType = SupervisoryNode.class,
    one = @One(select = "org.openlmis.core.repository.mapper.SupervisoryNodeMapper.getSupervisoryNode"))})
  List<RoleAssignment> getRoleAssignmentsWithGivenRightForAUser(@Param(value = "right") Right right,
                                                                @Param(value = "userId") Long userId);

  @Insert("INSERT INTO role_assignments" +
    "(userId, roleId, programId, supervisoryNodeId) VALUES " +
    "(#{userId}, #{roleId}, #{programId}, #{supervisoryNodeId})")
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


  @Select("SELECT userId, programId, array_agg(roleId) as roleIdsAsString " +
    "FROM role_assignments " +
    "WHERE userId=#{userId} AND programId IS NOT NULL AND supervisoryNodeId IS NULL " +
    "GROUP BY userId, programId")
  List<RoleAssignment> getHomeFacilityRoles(Long userId);

  @Select("SELECT RA.userId, RA.programId, array_agg(RA.roleId) as roleIdsAsString " +
    "FROM role_assignments RA INNER JOIN role_rights RR ON RA.roleId = RR.roleId " +
    "WHERE RA.userId=#{userId} AND RA.programId=#{programId} AND RR.rightName = ANY (#{commaSeparatedRights}::VARCHAR[])  AND  supervisoryNodeId IS NULL " +
    "GROUP BY userId, programId")
  List<RoleAssignment> getHomeFacilityRolesForUserOnGivenProgramWithRights(@Param("userId") Long userId, @Param("programId") Long programId, @Param("commaSeparatedRights") String commaSeparatedRights);


  @Select({"SELECT RA.userId, array_agg(RA.roleId) as roleIdsAsString FROM role_assignments RA INNER JOIN roles R ON RA.roleId = R.id",
    "WHERE userId = #{userId} AND R.type = 'ADMIN' GROUP BY userId, supervisoryNodeId, programId"})
  RoleAssignment getAdminRole(Long userId);

  @Insert("INSERT INTO role_assignments" +
    "(userId, roleId, programId, supervisoryNodeId, deliveryZoneId) VALUES " +
    "(#{userId}, #{roleId}, #{programId}, #{supervisoryNode.id}, #{deliveryZone.id})")
  void insert(@Param("userId") Long userId, @Param("programId") Long programId, @Param("supervisoryNode") SupervisoryNode supervisoryNode, @Param("deliveryZone") DeliveryZone deliveryZone, @Param("roleId") long roleId);

  @Select({"SELECT RA.userId, RA.programId, RA.deliveryZoneId, array_agg(RA.roleId) as roleIdsAsString ",
    "FROM role_assignments RA INNER JOIN roles R ON RA.roleId = R.id ",
    "WHERE RA.userId=#{userId} AND RA.programId IS NOT NULL AND R.type = 'ALLOCATION' GROUP BY RA.userId, RA.programId, RA.deliveryZoneId"})
  @Results(value = {@Result(property = "deliveryZone.id", column = "deliveryZoneId")})
  List<RoleAssignment> getAllocationRoles(Long userId);
}
