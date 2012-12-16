package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRightsMapper {

    @Insert("INSERT INTO role_rights(roleId, rightId) VALUES " +
            "(#{roleId}, #{right})")
    int createRoleRight(@Param(value = "roleId") Integer roleId, @Param(value = "right") Right right);

    @Insert("INSERT INTO roles" +
            "(name, description) VALUES " +
            "(#{name}, #{description})")
    @Options(useGeneratedKeys = true)
    int insertRole(Role role);

    @Select("SELECT RA.userId, RA.roleId, RA.programId " +
            "FROM role_assignments RA, users U, role_rights RR WHERE " +
            "U.user_name = #{userName} " +
            "AND U.id  = RA.user_id " +
            "AND RA.role_id = RR.roleId " +
            "AND RR.rightId = #{right} ")
    List<RoleAssignment> getRoleAssignmentsWithGivenRightForAUser(@Param(value = "right") Right right,
                                                                  @Param(value = "userName") String userName);

    @Insert("INSERT INTO role_assignments" +
            "(userId, roleId, programId) VALUES " +
            "(#{user.id}, #{role.id}, #{program.id})")
    int createRoleAssignment(@Param(value = "user") User user,
                             @Param(value = "role") Role role,
                             @Param(value = "program") Program program);

    @Select("SELECT R.id, R.description " +
            "FROM role_assignments RA, users U, role_rights RR, rights R WHERE " +
            "U.userName = #{userName} " +
            "AND U.id = RA.userId " +
            "AND RA.roleId = RR.roleId " +
            "AND R.id = RR.rightId")
    List<Right> getAllRightsForUser(String username);

}
