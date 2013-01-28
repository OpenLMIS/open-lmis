package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Role;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.mapper.RoleAssignmentMapper;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoleAssignmentRepositoryTest {


  RoleAssignmentRepository repository;

  @Mock
  RoleAssignmentMapper mapper;

  @Before
  public void setUp() throws Exception {
    repository = new RoleAssignmentRepository(mapper);
  }

  @Test
  public void shouldInsertUserProgramRoleMapping() throws Exception {
    User user = new User();
    Program program = new Program();
    Role role = new Role();

    repository.createUserProgramRoleAssignment(user, role, program, null);

    verify(mapper).createRoleAssignment(user, role, program, null);
  }

  @Test
  public void shouldDeleteAllRoleAssignmentsForTheUser() throws Exception {
    repository.deleteAllRoleAssignmentsForUser(1);

    verify(mapper).deleteAllRoleAssignmentsForUser(1);
  }

  @Test
  public void shouldReturnRolesForAUserIdAndProgramID() throws Exception {
    repository.getRoleAssignmentsForAUserAndProgram(1, 1);

    verify(mapper).getRoleAssignmentForAUserIdAndProgramId(1, 1);
  }

  @Test
  public void shouldGetListOfProgramIdsForTheUserWhichHasRoleMapping() throws Exception {
    List<Integer> programIdList = Arrays.asList(10, 20);
    when(mapper.getProgramsForWhichHasRoleAssignments(1)).thenReturn(programIdList);

    assertThat(repository.getProgramsForWhichHasRoleAssignments(1), is(programIdList));
  }
}
