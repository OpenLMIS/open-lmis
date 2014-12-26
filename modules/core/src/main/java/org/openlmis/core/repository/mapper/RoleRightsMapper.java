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
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RightType;
import org.openlmis.core.domain.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RoleRightsMapper maps the roles to rights entity to corresponding representation in database.
 */
@Repository
public interface RoleRightsMapper {

  @Insert("INSERT INTO role_rights(roleId, rightName, createdBy) VALUES " +
    "(#{role.id}, #{rightName}, #{role.modifiedBy})")
  int createRoleRight(@Param(value = "role") Role role, @Param(value = "rightName") String rightName);

  //used below
  @SuppressWarnings("unused")
  @Select({"SELECT rightName, displayNameKey,rightType FROM role_rights RR",
    "INNER JOIN rights R on R.name = RR.rightName",
    "WHERE roleId = #{roleId}"})
  @Results(value = {
    @Result(property = "name", column = "rightName"),
    @Result(property = "type", column = "rightType"),
  })
  List<Right> getAllRightsForRole(Long roleId);

  @Insert({"INSERT INTO roles",
    "(name, description, createdBy,modifiedBy,createdDate,modifiedDate) VALUES",
    "(#{name}, #{description}, #{createdBy},#{modifiedBy},COALESCE(#{createdDate}, NOW()) ," +
      "COALESCE(#{modifiedDate}, NOW()) )"})
  @Options(useGeneratedKeys = true)
  int insertRole(Role role);

  @Select("SELECT * FROM roles WHERE id = #{id}")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "rights", javaType = List.class, column = "id",
      many = @Many(select = "getAllRightsForRole"))
  })
  Role getRole(Long id);

  @Select("SELECT * FROM roles ORDER BY name")
  @Results(value = {
    @Result(property = "id", column = "id"),
    @Result(property = "rights", javaType = List.class, column = "id",
      many = @Many(select = "getAllRightsForRole"))
  })
  List<Role> getAllRoles();

  @Update("UPDATE roles SET name=#{name}, description=#{description}, modifiedBy=#{modifiedBy}, modifiedDate= DEFAULT WHERE id=#{id}")
  void updateRole(Role role);

  @Delete("DELETE FROM role_rights WHERE roleId=#{roleId}")
  int deleteAllRightsForRole(Long roleId);

  @Select({"SELECT DISTINCT(R.name), R.rightType",
    "FROM (SELECT userId, roleId FROM role_assignments UNION ALL SELECT userId, roleId FROM fulfillment_role_assignments) A",
    "INNER JOIN users U ON A.userId = U.id",
    "INNER JOIN role_rights RR ON A.roleId = RR.roleId",
    "INNER JOIN rights R on R.name = RR.rightName",
    "WHERE A.userId = #{userId}"})
  @Results(value = {
    @Result(property = "type", column = "rightType")
  })
  List<Right> getAllRightsForUserById(@Param("userId") Long userId);

  @Select({"SELECT DISTINCT RR.rightName " +
    "FROM role_rights RR INNER JOIN role_assignments RA ON RR.roleId = RA.roleId " +
    "WHERE RA.userId = #{userId} AND RA.supervisoryNodeId = ANY(#{commaSeparatedSupervisoryNodeIds}::INTEGER[]) AND RA.programId = #{program.id}"})
  @Results(value = {
    @Result(property = "name", column = "rightName")
  })
  List<Right> getRightsForUserOnSupervisoryNodeAndProgram(@Param("userId") Long userId, @Param("commaSeparatedSupervisoryNodeIds") String commaSeparatedSupervisoryNodeIds, @Param("program") Program program);

  @Select({"SELECT DISTINCT RR.rightName " +
    "FROM role_rights RR INNER JOIN role_assignments RA ON RR.roleId = RA.roleId " +
    "WHERE RA.userId = #{userId} AND RA.supervisoryNodeId IS NULL AND RA.programId = #{program.id}"})
  @Results(value = {
    @Result(property = "name", column = "rightName")
  })
  List<Right> getRightsForUserOnHomeFacilityAndProgram(@Param("userId") Long userId, @Param("program") Program program);


  @Select({"SELECT R.rightType from rights R INNER JOIN role_rights RR ON RR.rightName = R.name AND RR.roleId = #{roleId} LIMIT 1"})
  RightType getRightTypeForRoleId(Long roleId);

  @Select({"SELECT DISTINCT RR.rightName FROM role_rights RR INNER JOIN fulfillment_role_assignments FRA ON RR.roleId = FRA.roleId ",
    "WHERE FRA.userId = #{userId} AND FRA.facilityId = #{warehouseId}"})
  @Results(value = {
    @Result(property = "name", column = "rightName")
  })
  List<Right> getRightsForUserAndWarehouse(@Param("userId") Long userId, @Param("warehouseId") Long warehouseId);

}
