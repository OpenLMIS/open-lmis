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

  public void createUserProgramRoleAssignment(Integer userId, Integer roleId, Integer programId, Integer supervisoryNodeId) {
    mapper.createRoleAssignment(userId, programId, roleId, supervisoryNodeId);
  }

  public void deleteAllRoleAssignmentsForUser(Integer id) {
    mapper.deleteAllRoleAssignmentsForUser(id);
  }

  public List<Integer> getRoleAssignmentsForUserAndProgram(Integer id, Integer programId) {
    return mapper.getRoleAssignmentsForUserAndProgram(id, programId);
  }

  public List<Integer> getProgramsForWhichUserHasRoleAssignments(Integer userId) {
    return mapper.getProgramsForWhichUserHasRoleAssignments(userId);
  }

  public List<RoleAssignment> getSupervisorRoles(Integer userId) {
    return mapper.getSupervisorRoles(userId);
  }
}
