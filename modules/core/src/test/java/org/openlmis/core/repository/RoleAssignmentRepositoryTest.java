package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.repository.mapper.RoleAssignmentMapper;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoleAssignmentRepositoryTest {
  private RoleAssignmentRepository repository;

  @Mock
  private RoleAssignmentMapper mapper;

  @Before
  public void setUp() throws Exception {
    repository = new RoleAssignmentRepository(mapper);
  }

  @Test
  public void shouldInsertUserProgramRoleMapping() throws Exception {
    repository.createUserProgramRoleAssignment(1, 2, 3);

    verify(mapper).createRoleAssignment(1, 3, 2, null);
  }

  @Test
  public void shouldDeleteAllRoleAssignmentsForTheUser() throws Exception {
    repository.deleteAllRoleAssignmentsForUser(1);

    verify(mapper).deleteAllRoleAssignmentsForUser(1);
  }

  @Test
  public void shouldReturnRolesForUserAndProgram() throws Exception {
    List<Integer> roleIds = Arrays.asList(10, 20);
    when(mapper.getRoleAssignmentsForUserAndProgram(1, 2)).thenReturn(roleIds);

    assertThat(repository.getRoleAssignmentsForUserAndProgram(1, 2), is(roleIds));
  }

  @Test
  public void shouldGetListOfProgramIdsForWhichUserHasRoleAssignments() throws Exception {
    List<Integer> programIdList = Arrays.asList(10, 20);
    when(mapper.getProgramsForWhichUserHasRoleAssignments(1)).thenReturn(programIdList);

    assertThat(repository.getProgramsForWhichUserHasRoleAssignments(1), is(programIdList));
  }
}
