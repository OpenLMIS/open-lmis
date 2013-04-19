/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.Role;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RoleRightsMapper {

  @Insert("INSERT INTO role_rights(roleId, rightName) VALUES " +
      "(#{roleId}, #{right})")
  int createRoleRight(@Param(value = "roleId") Integer roleId, @Param(value = "right") Right right);

  @Select({"SELECT RR.rightName",
      "FROM users U, role_assignments RA, role_rights RR WHERE",
      "lower(U.userName) = lower(#{userName}) ",
      "AND U.id = RA.userId",
      "AND RA.roleId = RR.roleId"})
  Set<Right> getAllRightsForUserByUserName(String username);

  //used below
  @SuppressWarnings("unused")
  @Select("SELECT rightName FROM role_rights RR WHERE roleId = #{roleId}")
  Set<Right> getAllRightsForRole(Integer roleId);

  @Insert({"INSERT INTO roles",
      "(name, adminRole, description, modifiedBy) VALUES",
      "(#{name}, #{adminRole}, #{description}, #{modifiedBy})"})
  @Options(useGeneratedKeys = true)
  int insertRole(Role role);

  @Select("SELECT * FROM roles WHERE id = #{id}")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "rights", javaType = Set.class, column = "id",
          many = @Many(select = "getAllRightsForRole"))
  })
  Role getRole(Integer id);

  @Select("SELECT * FROM roles ORDER BY id")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "rights", javaType = Set.class, column = "id",
          many = @Many(select = "getAllRightsForRole"))
  })
  List<Role> getAllRoles();

  @Update("UPDATE roles SET name=#{name}, adminRole=#{adminRole}, description=#{description}, modifiedBy=#{modifiedBy}, modifiedDate= DEFAULT WHERE id=#{id}")
  void updateRole(Role role);

  @Delete("DELETE FROM role_rights WHERE roleId=#{roleId}")
  int deleteAllRightsForRole(int roleId);

  @Select({"SELECT RR.rightName",
      "FROM users U, role_assignments RA, role_rights RR WHERE",
      "U.id = #{userId}",
      "AND U.id = RA.userId",
      "AND RA.roleId = RR.roleId"})
  Set<Right> getAllRightsForUserById(@Param("userId") Integer userId);

  @Select({"SELECT DISTINCT RR.rightName " +
      "FROM role_rights RR INNER JOIN role_assignments RA ON RR.roleId = RA.roleId " +
      "WHERE RA.userId = #{userId} AND RA.supervisoryNodeId = ANY(#{commaSeparatedSupervisoryNodeIds}::INTEGER[]) AND RA.programId = #{program.id}"})
  List<Right> getRightsForUserOnSupervisoryNodeAndProgram(@Param("userId") Integer userId, @Param("commaSeparatedSupervisoryNodeIds") String commaSeparatedSupervisoryNodeIds, @Param("program") Program program);

  @Select({"SELECT DISTINCT RR.rightName " +
      "FROM role_rights RR INNER JOIN role_assignments RA ON RR.roleId = RA.roleId " +
      "WHERE RA.userId = #{userId} AND RA.supervisoryNodeId IS NULL AND RA.programId = #{program.id}"})
  List<Right> getRightsForUserOnHomeFacilityAndProgram(@Param("userId") Integer userId, @Param("program") Program program);
}
