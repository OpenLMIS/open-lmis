package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.RoleAssignmentRepository;
import org.openlmis.core.repository.RoleRightsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@NoArgsConstructor
public class RoleAssignmentService {

  RoleAssignmentRepository roleAssignmentRepository;

  ProgramRepository programRepository;

  RoleRightsRepository roleRightsRepository;


  @Autowired
  public RoleAssignmentService(RoleAssignmentRepository roleAssignmentRepository, ProgramRepository programRepository, RoleRightsRepository roleRightsRepository) {
    this.roleAssignmentRepository = roleAssignmentRepository;
    this.roleRightsRepository = roleRightsRepository;
    this.programRepository = programRepository;
  }

  private void createUserProgramRoleAssignment(Integer userId, Integer roleId, Integer programId, Integer supervisoryNodeId) {
    roleAssignmentRepository.createUserProgramRoleAssignment(userId, roleId, programId, supervisoryNodeId);
  }

  public void saveRoles(User user) {
    roleAssignmentRepository.deleteAllRoleAssignmentsForUser(user.getId());

    List<UserRoleAssignment> roleAssignments = user.getRoleAssignments();
    if (roleAssignments == null) return;
    for (UserRoleAssignment userRoleAssignment : roleAssignments) {
      for (Integer role : userRoleAssignment.getRoleIds()) {
        createUserProgramRoleAssignment(user.getId(), role, userRoleAssignment.getProgramId(), null);//To-Do : This will be modified once supervisory node is added to create user screen
      }
    }
  }

  public void saveSupervisoryRoles(User user) {
    if (user.getSupervisorRoles() == null) return;
    for (RoleAssignment userRoleAssignment : user.getSupervisorRoles()) {
      for (Integer role : userRoleAssignment.getRoleIds()) {
        createUserProgramRoleAssignment(user.getId(), role, userRoleAssignment.getProgramId(), userRoleAssignment.getSupervisoryNode().getId());
      }
    }
  }

  public void deleteAllRoleAssignmentsForUser(Integer id) {
    roleAssignmentRepository.deleteAllRoleAssignmentsForUser(id);
  }

  public List<UserRoleAssignment> getRoleAssignments(Integer userId) {
    List<Integer> listOfProgramIds = roleAssignmentRepository.getProgramsForWhichUserHasRoleAssignments(userId);

    List<UserRoleAssignment> roleAssignmentsList = new ArrayList<>();

    for (Integer programId : listOfProgramIds) {
      List<Integer> roleIds = roleAssignmentRepository.getRoleAssignmentsForUserAndProgram(userId, programId);
      UserRoleAssignment roleAssignments = new UserRoleAssignment(programId, roleIds);
      roleAssignmentsList.add(roleAssignments);
    }

    return roleAssignmentsList;
  }

  public List<RoleAssignment> getSupervisorRoles(Integer userId) {
    return roleAssignmentRepository.getSupervisorRoles(userId);
  }
}
