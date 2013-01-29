package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.ProgramRepository;
import org.openlmis.core.repository.RoleAssignmentRepository;
import org.openlmis.core.repository.RoleRightsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

  private void createUserProgramRoleAssignment(User user, Role role, Program program, SupervisoryNode supervisoryNode) {
    roleAssignmentRepository.createUserProgramRoleAssignment(user, role, program, supervisoryNode);
  }

  public void insertUserProgramRoleMapping(User user, List<ProgramToRoleMapping> listOfProgramToToRoleMapping) {
    for (ProgramToRoleMapping programRoleMapping : listOfProgramToToRoleMapping) {
      for (Role role : programRoleMapping.getRoles()) {
        createUserProgramRoleAssignment(user, role, programRoleMapping.getProgram(), null);//To-Do : This will be modified once supervisory node is added to create user screen
      }
    }
  }

  public void deleteAllRoleAssignmentsForUser(Integer id) {
    roleAssignmentRepository.deleteAllRoleAssignmentsForUser(id);
  }

  public List<ProgramToRoleMapping> getListOfProgramToRoleMappingForAUser(Integer userId) {
    List<Integer> listOfProgramIds = roleAssignmentRepository.getProgramsForWhichHasRoleAssignments(userId);

    List<ProgramToRoleMapping> listOfProgramToRoleMapping = new ArrayList<>();


    for(Integer programId:listOfProgramIds) {
      ProgramToRoleMapping programToRoleMapping = new ProgramToRoleMapping();
      programToRoleMapping.setProgram(programRepository.getById(programId));
      programToRoleMapping.setRoles(roleAssignmentRepository.getRoleAssignmentsForAUserAndProgram(userId, programId));
      listOfProgramToRoleMapping.add(programToRoleMapping);
    }

    return listOfProgramToRoleMapping;
  }
}
