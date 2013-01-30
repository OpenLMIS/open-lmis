package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.repository.RoleAssignmentRepository;

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
  public void shouldInsertUserRoleAssignment() throws Exception {
   //To-Do come back to write tests
  }

}
