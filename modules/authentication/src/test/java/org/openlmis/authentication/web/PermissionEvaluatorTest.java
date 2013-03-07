package org.openlmis.authentication.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.RoleAssignmentService;
import org.openlmis.core.service.RoleRightsService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PermissionEvaluatorTest {

  @Mock
  RoleRightsService roleRightsService;
  @Mock
  FacilityService facilityService;
  @Mock
  private RoleAssignmentService roleAssignmentService;

  private PermissionEvaluator evaluator;

  @Before
  public void setUp() throws Exception {
    evaluator = new PermissionEvaluator(roleRightsService, facilityService, roleAssignmentService);
  }

  @Test
  public void shouldReturnTrueIfUserHasRequiredPermission() throws Exception {

    Set<Right> rights = new HashSet<Right>() {{
      add(Right.CONFIGURE_RNR);
      add(Right.AUTHORIZE_REQUISITION);
    }};
    Integer userId = 1;
    when(roleRightsService.getRights(userId)).thenReturn(rights);

    assertThat(evaluator.hasPermission(userId, "AUTHORIZE_REQUISITION, CONFIGURE_RNR"), is(true));
    assertThat(evaluator.hasPermission(userId, "AUTHORIZE_REQUISITION"), is(true));
    assertThat(evaluator.hasPermission(userId, "MANAGE_FACILITY"), is(false));
  }

  @Test
  public void shouldReturnFalseIfUserDoesNotHaveRequiredPermissionOnProgramForHomeFacility() throws Exception {
    int facilityId = 1;
    int programId = 1;
    Integer userId = 1;
    when(facilityService.getHomeFacility(userId)).thenReturn(new Facility(1));
    List<RoleAssignment> roleAssignments = new ArrayList<>();

    when(roleAssignmentService.getHomeFacilityRolesForUserOnGivenProgramWithRights(userId, programId, Right.CREATE_REQUISITION, Right.AUTHORIZE_REQUISITION)).thenReturn(roleAssignments);

    assertThat(evaluator.hasPermission(userId, facilityId, programId, "CREATE_REQUISITION, AUTHORIZE_REQUISITION"), is(false));
  }

  @Test
  public void shouldReturnTrueIfUserHasRequiredPermissionOnProgramForHomeFacility() throws Exception {
    int facilityId = 1;
    int programId = 1;
    Integer userId = 1;
    when(facilityService.getHomeFacility(userId)).thenReturn(new Facility(1));
    List<RoleAssignment> roleAssignments = asList(new RoleAssignment());

    when(roleAssignmentService.getHomeFacilityRolesForUserOnGivenProgramWithRights(userId, programId, Right.CREATE_REQUISITION, Right.AUTHORIZE_REQUISITION)).thenReturn(roleAssignments);

    assertThat(evaluator.hasPermission(userId, facilityId, programId, "CREATE_REQUISITION, AUTHORIZE_REQUISITION"), is(true));
  }

  @Test
  public void shouldReturnTrueIfUserHasRequiredPermissionOnProgramForSupervisedFacility() throws Exception {
    int facilityId = 1;
    int programId = 1;
    Integer userId = 1;
    List<Facility> facilities = asList(new Facility(1));
    when(facilityService.getUserSupervisedFacilities(userId, programId, Right.CREATE_REQUISITION, Right.AUTHORIZE_REQUISITION)).thenReturn(facilities);

    assertThat(evaluator.hasPermission(userId, facilityId, programId, "CREATE_REQUISITION, AUTHORIZE_REQUISITION"), is(true));
  }

  @Test
  public void shouldReturnFalseIfUserDoesNotHaveRequiredPermissionOnProgramForSupervisedFacility() throws Exception {
    int facilityId = 1;
    int programId = 1;
    Integer userId = 1;
    List<Facility> facilities = new ArrayList<>();
    when(facilityService.getUserSupervisedFacilities(userId, programId, Right.CREATE_REQUISITION, Right.AUTHORIZE_REQUISITION)).thenReturn(facilities);

    assertThat(evaluator.hasPermission(userId, facilityId, programId, "CREATE_REQUISITION, AUTHORIZE_REQUISITION"), is(false));
  }
}
