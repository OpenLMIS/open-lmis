package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.RoleAssignmentRepository;
import org.openlmis.core.repository.RoleRightsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

  public void saveHomeFacilityRoles(User user) {
    List<RoleAssignment> homeFacilityRoles = user.getHomeFacilityRoles();
    saveRoles(user, homeFacilityRoles);
  }

  public void saveSupervisoryRoles(User user) {
    List<RoleAssignment> supervisorRoles = user.getSupervisorRoles();
    saveRoles(user, supervisorRoles);
  }

  public void deleteAllRoleAssignmentsForUser(Integer id) {
    roleAssignmentRepository.deleteAllRoleAssignmentsForUser(id);
  }

  public List<RoleAssignment> getHomeFacilityRoles(Integer userId) {
    return roleAssignmentRepository.getHomeFacilityRoles(userId);
  }

  public List<RoleAssignment> getSupervisorRoles(Integer userId) {
    return roleAssignmentRepository.getSupervisorRoles(userId);
  }

  private void saveRoles(User user, List<RoleAssignment> roleAssignments) {
    if (roleAssignments == null) return;
    for (RoleAssignment roleAssignment : roleAssignments) {
      for (Integer role : roleAssignment.getRoleIds()) {
        SupervisoryNode node = roleAssignment.getSupervisoryNode();
        Integer supervisoryNodeId = null;
        if(node != null) supervisoryNodeId = node.getId();
        roleAssignmentRepository.insertRoleAssignment(user.getId(), roleAssignment.getProgramId(), supervisoryNodeId, role);
      }
    }
  }
}
