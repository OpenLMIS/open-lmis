package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.UserRoleAssignment;
import org.openlmis.core.repository.RoleAssignmentRepository;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RoleAssignmentServiceTest {

  RoleAssignmentService service;

  @Mock
  RoleAssignmentRepository roleAssignmentRepository;

  @Before
  public void setUp() throws Exception {
    service = new RoleAssignmentService(roleAssignmentRepository, null, null);
  }

  @Test
  public void shouldGetUserRoleAssignment() throws Exception {
    when(roleAssignmentRepository.getProgramsForWhichUserHasRoleAssignments(1)).thenReturn(Arrays.asList(10));
    when(roleAssignmentRepository.getRoleAssignmentsForUserAndProgram(1, 10)).thenReturn(Arrays.asList(1, 2));

    List<UserRoleAssignment> roleAssignmentList = service.getRoleAssignments(1);

    assertThat(roleAssignmentList.size(), is(1));
    assertThat(roleAssignmentList.get(0).getProgramId(), is(10));
    assertThat(roleAssignmentList.get(0).getRoleIds(), is(Arrays.asList(1,2)));
  }

  @Test
  public void shouldGetUserRoleAssignments() throws Exception {
    when(roleAssignmentRepository.getProgramsForWhichUserHasRoleAssignments(1)).thenReturn(Arrays.asList(10,20));
    when(roleAssignmentRepository.getRoleAssignmentsForUserAndProgram(1, 10)).thenReturn(Arrays.asList(1,2));
    when(roleAssignmentRepository.getRoleAssignmentsForUserAndProgram(1, 10)).thenReturn(Arrays.asList(3,4));

    List<UserRoleAssignment> roleAssignmentList = service.getRoleAssignments(1);

    assertThat(roleAssignmentList.size(), is(2));
  }
}
