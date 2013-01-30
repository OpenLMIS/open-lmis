package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.repository.mapper.RoleAssignmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@NoArgsConstructor
public class RoleAssignmentRepository {

  RoleAssignmentMapper mapper;

  @Autowired
  public RoleAssignmentRepository(RoleAssignmentMapper roleAssignmentMapper) {
    this.mapper = roleAssignmentMapper;
  }

  public List<RoleAssignment> getRoleAssignmentsForUserWithRight(Right right, int userId) {
    return mapper.getRoleAssignmentsWithGivenRightForAUser(right, userId);
  }

  public void createUserProgramRoleAssignment(Integer userId, Integer roleId, Integer programId) {
    mapper.createRoleAssignment(userId, roleId, programId, null);
  }

  public void deleteAllRoleAssignmentsForUser(Integer id) {
    mapper.deleteAllRoleAssignmentsForUser(id);
  }

  public List<Integer> getRoleAssignmentsForAUserAndProgram(Integer id, Integer programId) {
    return mapper.getRoleAssignmentForAUserIdAndProgramId(id, programId);
  }

  public List<Integer> getProgramsForWhichHasRoleAssignments(Integer userId) {
    return mapper.getProgramsForWhichHasRoleAssignments(userId);
  }
}
