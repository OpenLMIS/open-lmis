package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRightsMapper {

  @Insert("INSERT INTO role_rights(roleId, rightName) VALUES " +
      "(#{roleId}, #{right})")
  int createRoleRight(@Param(value = "roleId") Integer roleId, @Param(value = "right") Right right);

  @Select({"SELECT RR.rightName",
      "FROM users U, role_assignments RA, role_rights RR WHERE",
      "U.userName = #{userName}",
      "AND U.id = RA.userId",
      "AND RA.roleId = RR.roleId"})
  List<Right> getAllRightsForUser(String username);

  @Select("SELECT rightName FROM role_rights RR WHERE roleId = #{roleId}")
  List<Right> getAllRightsForRole(Integer roleId);

  @Insert({"INSERT INTO roles",
      "(name, description) VALUES",
      "(#{name}, #{description})"})
  @Options(useGeneratedKeys = true)
  int insertRole(Role role);

  @Select("SELECT id, name, description from roles WHERE id = #{id}")
  Role getRole(Integer id);

  @Select("SELECT * FROM roles ORDER BY id")
  @Results(value = {
      @Result(property = "id", column = "id"),
      @Result(property = "name", column = "name"),
      @Result(property = "description", column = "description"),
      @Result(property = "rights", javaType = List.class, column = "id",
          many = @Many(select = "getAllRightsForRole"))
  })
  List<Role> getAllRoles();


}
