package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.core.domain.*;
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
                                                                @Param(value = "userId") int userId);

  @Insert("INSERT INTO role_assignments" +
    "(userId, roleId, programId, supervisoryNodeId) VALUES " +
    "(#{user.id}, #{role.id}, #{program.id}, #{supervisoryNode.id})")
  int createRoleAssignment(@Param(value = "user") User user,
                           @Param(value = "role") Role role,
                           @Param(value = "program") Program program,
                           @Param(value = "supervisoryNode") SupervisoryNode supervisoryNode);

  @Delete("DELETE FROM role_assignments WHERE userid=#{id}")
  void deleteAllRoleAssignmentsForUser(int id);

  @Select("SELECT * from role_assignments where userid=#{id}")
  List<RoleAssignment> getRoleAssignmentForAUser(int id);

  @Select("SELECT roleid from role_assignments where userid=#{id} AND programid=#{programId}")
  @Results(value = {
    @Result(property = "id", column = "roleid")
  })
  List<Role> getRoleAssignmentForAUserIdAndProgramId(@Param(value = "id")int id,@Param(value = "programId") int programId);

  @Select("SELECT distinct(programId) FROM role_assignments WHERE userId=#{userId}")
  List<Integer> getProgramsForWhichHasRoleAssignments(int userId);

}
