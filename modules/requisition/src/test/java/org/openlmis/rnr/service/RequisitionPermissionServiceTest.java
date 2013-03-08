package org.openlmis.rnr.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.RoleAssignmentService;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.domain.Rnr;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.openlmis.core.domain.Right.*;
import static org.openlmis.rnr.builder.RequisitionBuilder.status;
import static org.openlmis.rnr.domain.RnrStatus.*;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequisitionPermissionServiceTest {

  @Mock
  FacilityService facilityService;
  @Mock
  private RoleAssignmentService roleAssignmentService;

  private RequisitionPermissionService requisitionPermissionService;
  private Integer userId;
  private Integer programId;
  private Integer facilityId;

  @Before
  public void setUp() throws Exception {
    userId = 1;
    programId = 1;
    facilityId = 1;
    requisitionPermissionService = new RequisitionPermissionService(facilityService, roleAssignmentService);
  }

  @Test
  public void shouldReturnFalseIfUserDoesNotHaveRequiredPermissionOnProgramForHomeFacility() throws Exception {
    when(facilityService.getHomeFacility(userId)).thenReturn(new Facility(1));
    List<RoleAssignment> roleAssignments = new ArrayList<>();

    when(roleAssignmentService.getHomeFacilityRolesForUserOnGivenProgramWithRights(userId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION)).thenReturn(roleAssignments);

    assertThat(requisitionPermissionService.hasPermission(userId, facilityId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION), is(false));
  }

  @Test
  public void shouldReturnTrueIfUserHasRequiredPermissionOnProgramForHomeFacility() throws Exception {
    when(facilityService.getHomeFacility(userId)).thenReturn(new Facility(1));
    List<RoleAssignment> roleAssignments = asList(new RoleAssignment());

    when(roleAssignmentService.getHomeFacilityRolesForUserOnGivenProgramWithRights(userId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION)).thenReturn(roleAssignments);

    assertThat(requisitionPermissionService.hasPermission(userId, facilityId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION), is(true));
  }

  @Test
  public void shouldReturnTrueIfUserHasRequiredPermissionOnProgramForSupervisedFacility() throws Exception {
    List<Facility> facilities = asList(new Facility(1));
    when(facilityService.getUserSupervisedFacilities(userId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION)).thenReturn(facilities);

    assertThat(requisitionPermissionService.hasPermission(userId, facilityId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION), is(true));
  }

  @Test
  public void shouldReturnFalseIfUserDoesNotHaveRequiredPermissionOnProgramForSupervisedFacility() throws Exception {
    List<Facility> facilities = new ArrayList<>();
    when(facilityService.getUserSupervisedFacilities(userId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION)).thenReturn(facilities);

    assertThat(requisitionPermissionService.hasPermission(userId, facilityId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION), is(false));
  }

  @Test
  public void shouldReturnFalseIfUserDoesNotHaveRequiredPermissionOnRnr() throws Exception {
    Rnr rnr = new Rnr();
    rnr.setProgram(new Program(programId));
    rnr.setFacility(new Facility(facilityId));

    RequisitionPermissionService rnrPermissionEvaluationServiceSpy = spy(requisitionPermissionService);
    doReturn(false).when(rnrPermissionEvaluationServiceSpy).hasPermission(userId, rnr, CREATE_REQUISITION, AUTHORIZE_REQUISITION);
    assertThat(requisitionPermissionService.hasPermission(userId, rnr, CREATE_REQUISITION, AUTHORIZE_REQUISITION), is(false));
  }

  @Test
  public void shouldReturnTrueIfRnrStatusIsSubmittedAndUserHasAuthorizeRight() {
    RequisitionPermissionService requisitionPermissionServiceSpy = spy(requisitionPermissionService);
    Rnr rnr = make(a(RequisitionBuilder.defaultRnr, with(status, SUBMITTED)));
    doReturn(true).when(requisitionPermissionServiceSpy).hasPermission(userId, rnr, AUTHORIZE_REQUISITION);
    assertThat(requisitionPermissionServiceSpy.hasPermissionToSave(userId, rnr), is(true));
  }

  @Test
  public void shouldReturnTrueIfRnrStatusIsAuthorizedAndUserHasApproveRight() {
    RequisitionPermissionService requisitionPermissionServiceSpy = spy(requisitionPermissionService);
    Rnr rnr = make(a(RequisitionBuilder.defaultRnr, with(status, AUTHORIZED)));
    doReturn(true).when(requisitionPermissionServiceSpy).hasPermission(userId, rnr, APPROVE_REQUISITION);
    assertThat(requisitionPermissionServiceSpy.hasPermissionToSave(userId, rnr), is(true));
  }

  @Test
  public void shouldReturnTrueIfRnrStatusIsInitiatedAndUserHasCreateRight() {
    RequisitionPermissionService requisitionPermissionServiceSpy = spy(requisitionPermissionService);
    Rnr rnr = make(a(RequisitionBuilder.defaultRnr, with(status, INITIATED)));
    doReturn(true).when(requisitionPermissionServiceSpy).hasPermission(userId, rnr, CREATE_REQUISITION);
    assertThat(requisitionPermissionServiceSpy.hasPermissionToSave(userId, rnr), is(true));
  }

}
