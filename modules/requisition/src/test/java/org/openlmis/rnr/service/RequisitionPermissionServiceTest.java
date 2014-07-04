/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.rnr.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.*;
import org.openlmis.core.service.ProgramSupportedService;
import org.openlmis.core.service.RoleAssignmentService;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.domain.Rnr;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.openlmis.core.builder.ProgramSupportedBuilder.defaultProgramSupported;
import static org.openlmis.core.builder.ProgramSupportedBuilder.isActive;
import static org.openlmis.core.domain.RightName.*;
import static org.openlmis.core.domain.RightType.FULFILLMENT;
import static org.openlmis.core.domain.RightType.REQUISITION;
import static org.openlmis.rnr.builder.RequisitionBuilder.program;
import static org.openlmis.rnr.builder.RequisitionBuilder.status;
import static org.openlmis.rnr.domain.RnrStatus.*;
import static org.powermock.api.mockito.PowerMockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RequisitionPermissionServiceTest {

  @Mock
  private RoleRightsService roleRightsService;
  @Mock
  private RoleAssignmentService roleAssignmentService;

  @Mock
  private ProgramSupportedService programSupportedService;

  @InjectMocks
  private RequisitionPermissionService requisitionPermissionService;
  private Long userId;
  private Long programId;
  private Long facilityId;

  @Before
  public void setUp() throws Exception {
    userId = 1L;
    programId = 2L;
    facilityId = 3L;
  }

  @Test
  public void shouldReturnFalseIfUserDoesNotHaveRequiredPermissionOnProgramAndFacility() throws Exception {
    Facility facility = new Facility(facilityId);
    Program program = new Program(programId);
    List<Right> rights = asList(new Right(APPROVE_REQUISITION, REQUISITION));

    when(roleRightsService.getRightsForUserAndFacilityProgram(userId, facility, program)).thenReturn(rights);

    assertThat(requisitionPermissionService.hasPermission(userId, facility, program, CREATE_REQUISITION), is(false));
  }

  @Test
  public void shouldReturnFalseIfFacilityDoesNotSupportAProgram() throws Exception {
    Facility facility = new Facility(facilityId);
    Program program = new Program(programId);

    when(programSupportedService.getByFacilityIdAndProgramId(facility.getId(), program.getId())).thenReturn(null);

    assertThat(requisitionPermissionService.hasPermission(userId, facility, program, CREATE_REQUISITION), is(false));
    verify(programSupportedService).getByFacilityIdAndProgramId(facility.getId(), program.getId());
  }

  @Test
  public void shouldReturnFalseIfSupportedProgramNotActive() throws Exception {
    Facility facility = new Facility(facilityId);
    Program program = new Program(programId);

    when(programSupportedService.getByFacilityIdAndProgramId(facility.getId(), program.getId())).thenReturn(make(a(defaultProgramSupported, with(isActive, false))));

    assertThat(requisitionPermissionService.hasPermission(userId, facility, program, CREATE_REQUISITION), is(false));
    verify(programSupportedService).getByFacilityIdAndProgramId(facility.getId(), program.getId());
  }

  @Test
  public void shouldReturnFalseIfProgramNotActive() throws Exception {
    Facility facility = new Facility(facilityId);
    Program program = new Program(programId);
    program.setActive(false);

    ProgramSupported programSupported = make(a(defaultProgramSupported));
    programSupported.setProgram(program);
    when(programSupportedService.getByFacilityIdAndProgramId(facility.getId(), program.getId())).thenReturn(programSupported);

    assertThat(requisitionPermissionService.hasPermission(userId, facility, program, CREATE_REQUISITION), is(false));
    verify(programSupportedService).getByFacilityIdAndProgramId(facility.getId(), program.getId());
  }

  @Test
  public void shouldReturnTrueIfUserHasRequiredPermissionOnProgramAndFacility() throws Exception {
    List<Right> rights = asList(new Right(CREATE_REQUISITION, REQUISITION));

    when(programSupportedService.getByFacilityIdAndProgramId(facilityId, programId)).thenReturn(make(a(defaultProgramSupported)));
    when(roleRightsService.getRightsForUserAndFacilityProgram(eq(userId), any(Facility.class), any(Program.class))).thenReturn(rights);

    assertThat(requisitionPermissionService.hasPermission(userId, new Facility(facilityId), new Program(programId), CREATE_REQUISITION), is(true));
  }

  @Test
  public void shouldReturnFalseIfUserDoesNotHaveRequiredPermissionOnRnr() throws Exception {
    Rnr rnr = new Rnr();
    rnr.setProgram(new Program(programId));
    rnr.setFacility(new Facility(facilityId));

    RequisitionPermissionService rnrPermissionEvaluationServiceSpy = spy(requisitionPermissionService);
    doReturn(false).when(rnrPermissionEvaluationServiceSpy).hasPermission(userId, rnr, CREATE_REQUISITION);
    assertThat(requisitionPermissionService.hasPermission(userId, rnr, CREATE_REQUISITION), is(false));
  }

  @Test
  public void shouldReturnTrueIfRnrStatusIsSubmittedAndUserHasAuthorizeRight() {
    RequisitionPermissionService requisitionPermissionServiceSpy = spy(requisitionPermissionService);
    Rnr rnr = make(a(RequisitionBuilder.defaultRequisition, with(status, SUBMITTED)));
    doReturn(true).when(requisitionPermissionServiceSpy).hasPermission(userId, rnr, AUTHORIZE_REQUISITION);
    assertThat(requisitionPermissionServiceSpy.hasPermissionToSave(userId, rnr), is(true));
  }

  @Test
  public void shouldReturnTrueIfRnrStatusIsAuthorizedAndUserHasApproveRightForSupervisoryNode() {
    RequisitionPermissionService requisitionPermissionServiceSpy = spy(requisitionPermissionService);
    final RoleAssignment approverRoleAssignment = new RoleAssignment(userId, 2l, 3l, new SupervisoryNode(5l));
    when(roleAssignmentService.getRoleAssignments(APPROVE_REQUISITION, userId)).thenReturn(new ArrayList<RoleAssignment>() {{
      add(approverRoleAssignment);
    }});

    Rnr rnr = make(a(RequisitionBuilder.defaultRequisition, with(status, AUTHORIZED)));
    rnr.setSupervisoryNodeId(5l);

    assertThat(requisitionPermissionServiceSpy.hasPermissionToSave(userId, rnr), is(true));
  }

  @Test
  public void shouldReturnTrueIfRnrStatusIsAuthorizedAndUserHasApproveRightButNotForSupervisoryNode() {
    RequisitionPermissionService requisitionPermissionServiceSpy = spy(requisitionPermissionService);
    final RoleAssignment approverRoleAssignment = new RoleAssignment(userId, 2l, 3l, new SupervisoryNode(7l));
    when(roleAssignmentService.getRoleAssignments(APPROVE_REQUISITION, userId)).thenReturn(new ArrayList<RoleAssignment>() {{
      add(approverRoleAssignment);
    }});

    Rnr rnr = make(a(RequisitionBuilder.defaultRequisition, with(status, AUTHORIZED)));
    rnr.setSupervisoryNodeId(5l);

    assertFalse(requisitionPermissionServiceSpy.hasPermissionToSave(userId, rnr));
  }

  @Test
  public void shouldReturnTrueIfRnrStatusIsInitiatedAndUserHasCreateRight() {
    RequisitionPermissionService requisitionPermissionServiceSpy = spy(requisitionPermissionService);
    Rnr rnr = make(a(RequisitionBuilder.defaultRequisition, with(status, INITIATED)));
    doReturn(true).when(requisitionPermissionServiceSpy).hasPermission(userId, rnr, CREATE_REQUISITION);
    assertThat(requisitionPermissionServiceSpy.hasPermissionToSave(userId, rnr), is(true));
  }

  @Test
  public void shouldReturnTrueIfUserCanApproveARnr() throws Exception {
    Long supervisoryNodeId = 1L;
    Rnr rnr = make(a(RequisitionBuilder.defaultRequisition, with(status, AUTHORIZED)));
    rnr.setSupervisoryNodeId(supervisoryNodeId);
    final RoleAssignment assignment = roleAssignmentWithSupervisoryNodeId(supervisoryNodeId, 3l);
    List<RoleAssignment> roleAssignments = new ArrayList<RoleAssignment>() {{
      add(assignment);
    }};

    when(roleAssignmentService.getRoleAssignments(APPROVE_REQUISITION, userId)).thenReturn(roleAssignments);

    assertThat(requisitionPermissionService.hasPermission(userId, rnr, APPROVE_REQUISITION), is(true));
  }

  @Test
  public void shouldReturnFalseIfUserHasApproveRightOnNodeButNotForProgram() throws Exception {
    Long supervisoryNodeId = 1L;
    Long supportedProgramId = 3l;
    Long rnrProgramId = 2l;
    Rnr rnr = make(a(RequisitionBuilder.defaultRequisition, with(status, AUTHORIZED), with(program, new Program(rnrProgramId))));
    rnr.setSupervisoryNodeId(supervisoryNodeId);
    final RoleAssignment assignment = roleAssignmentWithSupervisoryNodeId(supervisoryNodeId, supportedProgramId);
    List<RoleAssignment> roleAssignments = new ArrayList<RoleAssignment>() {{
      add(assignment);
    }};

    when(roleAssignmentService.getRoleAssignments(APPROVE_REQUISITION, userId)).thenReturn(roleAssignments);

    assertThat(requisitionPermissionService.hasPermission(userId, rnr, APPROVE_REQUISITION), is(false));
  }

  @Test
  public void shouldCheckIfUserHasGivenPermission() throws Exception {
    List<Right> rights = asList(new Right(CONVERT_TO_ORDER, FULFILLMENT));

    when(roleRightsService.getRights(1L)).thenReturn(rights);
    assertThat(requisitionPermissionService.hasPermission(1L, CONVERT_TO_ORDER), is(true));
    assertThat(requisitionPermissionService.hasPermission(1L, CREATE_REQUISITION), is(false));
  }

  private RoleAssignment roleAssignmentWithSupervisoryNodeId(Long supervisoryNodeId, Long programId) {
    final RoleAssignment assignment = new RoleAssignment();
    final SupervisoryNode node = new SupervisoryNode();
    node.setId(supervisoryNodeId);
    assignment.setSupervisoryNode(node);
    assignment.setProgramId(programId);
    return assignment;
  }
}
