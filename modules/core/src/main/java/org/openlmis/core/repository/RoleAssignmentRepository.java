package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
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

  public void createUserProgramRoleAssignment(User user, Role role, Program program, SupervisoryNode supervisor) {
    mapper.createRoleAssignment(user, role, program, supervisor);
  }

  public void deleteAllRoleAssignmentsForUser(Integer id) {
    mapper.deleteAllRoleAssignmentsForUser(id);
  }

  public List<RoleAssignment> getRoleAssignmentsForAUser(Integer id) {
    return mapper.getRoleAssignmentForAUser(id);
  }

  public List<Role> getRoleAssignmentsForAUserAndProgram(Integer id, Integer programId) {
    return mapper.getRoleAssignmentForAUserIdAndProgramId(id, programId);
  }

  public List<Integer> getProgramsForWhichHasRoleAssignments(Integer userId) {
    return mapper.getProgramsForWhichHasRoleAssignments(userId);
  }

  public void insertUserProgramRoleMapping(User user, List<ProgramToRoleMapping> listOfProgramToToRoleMapping) {
    for (ProgramToRoleMapping programRoleMapping : listOfProgramToToRoleMapping) {
      for (Role role : programRoleMapping.getRoles()) {
        createUserProgramRoleAssignment(user, role, programRoleMapping.getProgram(), null);//To-Do : This will be modified once supervisory node is added to create user screen
      }
    }
  }
}
