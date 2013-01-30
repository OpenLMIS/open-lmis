package org.openlmis.core.repository.mapper;

import org.apache.ibatis.annotations.*;
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
                                                                @Param(value = "userId") int userId);

  @Insert("INSERT INTO role_assignments" +
      "(userId, roleId, programId, supervisoryNodeId) VALUES " +
      "(#{userId}, #{roleId}, #{programId}, #{supervisoryNodeId})")
  int createRoleAssignment(@Param(value = "userId") Integer userId,
                           @Param(value = "programId") Integer programId, @Param(value = "roleId") Integer roleId,
                           @Param(value = "supervisoryNodeId") Integer supervisoryNodeId);

  @Delete("DELETE FROM role_assignments WHERE userId=#{id}")
  void deleteAllRoleAssignmentsForUser(int userId);

  @Select("SELECT roleId from role_assignments where userId=#{id} AND programId=#{programId}")
  List<Integer> getRoleAssignmentsForUserAndProgram(@Param(value = "id") int userId, @Param(value = "programId") int programId);

  @Select("SELECT distinct(programId) FROM role_assignments WHERE userId=#{userId} AND programId IS NOT NULL")
  List<Integer> getProgramsForWhichUserHasRoleAssignments(int userId);

}
