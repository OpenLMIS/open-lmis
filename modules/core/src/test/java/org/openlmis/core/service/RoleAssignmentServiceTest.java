package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramToRoleMapping;
import org.openlmis.core.domain.Role;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.RoleAssignmentRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RoleAssignmentServiceTest {

  RoleAssignmentService roleAssignmentService;

  @Mock
  RoleAssignmentRepository roleAssignmentRepository;

  @Before
  public void setUp() throws Exception {
    roleAssignmentService = new RoleAssignmentService(roleAssignmentRepository, null, null);
  }

  @Test
  public void shouldInsertUserProgramRole() throws Exception {
    User user = new User();
    ProgramToRoleMapping programToRoleMapping = new ProgramToRoleMapping();
    Program program1 = new Program();
    programToRoleMapping.setProgram(program1);
    Role role1 = new Role();
    Role[] roles = {role1};
    programToRoleMapping.setRoles(Arrays.asList(roles));

    List<ProgramToRoleMapping> listOfProgramToToRoleMapping = new ArrayList<>();
    listOfProgramToToRoleMapping.add(programToRoleMapping);

    roleAssignmentService.insertUserProgramRoleMapping(user, listOfProgramToToRoleMapping);

    verify(roleAssignmentRepository).createUserProgramRoleAssignment(user, role1, program1, null);
  }

}
