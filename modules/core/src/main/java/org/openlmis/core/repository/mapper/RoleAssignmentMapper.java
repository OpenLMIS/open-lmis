package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.openlmis.core.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleAssignmentMapper {

    @Select("SELECT RA.userId, RA.roleId, RA.programId " +
            "FROM role_assignments RA, users U, role_rights RR WHERE " +
            "U.userName = #{userName} " +
            "AND U.id  = RA.userId " +
            "AND RA.roleId = RR.roleId " +
            "AND RR.rightId = #{right} ")
    List<RoleAssignment> getRoleAssignmentsWithGivenRightForAUser(@Param(value = "right") Right right,
                                                                  @Param(value = "userName") String userName);

    @Insert("INSERT INTO role_assignments" +
            "(userId, roleId, programId) VALUES " +
            "(#{user.id}, #{role.id}, #{program.id})")
    int createRoleAssignment(@Param(value = "user") User user,
                             @Param(value = "role") Role role,
                             @Param(value = "program") Program program);
}
