/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
import org.openlmis.core.service.RoleAssignmentService;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.rnr.builder.RequisitionBuilder;
import org.openlmis.rnr.domain.Rnr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.openlmis.core.domain.Right.*;
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

    Set<Right> rights = new HashSet<Right>(){{add(APPROVE_REQUISITION);}};
    when(roleRightsService.getRightsForUserAndFacilityProgram(userId, facility, program)).thenReturn(rights);

    assertThat(requisitionPermissionService.hasPermission(userId, facility, program, CREATE_REQUISITION, AUTHORIZE_REQUISITION), is(false));
  }

  @Test
  public void shouldReturnTrueIfUserHasRequiredPermissionOnProgramAndFacility() throws Exception {
    Set<Right> rights = new HashSet<Right>(){{add(CREATE_REQUISITION);}};
    when(roleRightsService.getRightsForUserAndFacilityProgram(eq(userId), any(Facility.class), any(Program.class))).thenReturn(rights);

    assertThat(requisitionPermissionService.hasPermission(userId, new Facility(facilityId), new Program(programId), CREATE_REQUISITION, AUTHORIZE_REQUISITION), is(true));
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

  @Test
  public void shouldReturnTrueIfUserCanApproveARnr() throws Exception {
    Long supervisoryNodeId = 1L;
    Rnr rnr = make(a(RequisitionBuilder.defaultRnr, with(status, AUTHORIZED)));
    rnr.setSupervisoryNodeId(supervisoryNodeId);
    final RoleAssignment assignment = roleAssignmentWithSupervisoryNodeId(supervisoryNodeId);
    List<RoleAssignment> roleAssignments = new ArrayList<RoleAssignment>() {{
      add(assignment);
    }};

    when(roleAssignmentService.getRoleAssignments(APPROVE_REQUISITION, userId)).thenReturn(roleAssignments);

    assertThat(requisitionPermissionService.hasPermissionToApprove(userId, rnr), is(true));
  }

  @Test
  public void shouldCheckIfUserHasGivenPermission() throws Exception {
    Set<Right> rights = new HashSet<Right>(){{add(CONVERT_TO_ORDER);}};
    when(roleRightsService.getRights(1L)).thenReturn(rights);
   assertThat(requisitionPermissionService.hasPermission(1L, CONVERT_TO_ORDER), is(true));
   assertThat(requisitionPermissionService.hasPermission(1L, CREATE_REQUISITION), is(false));
  }

  private RoleAssignment roleAssignmentWithSupervisoryNodeId(Long supervisoryNodeId) {
    final RoleAssignment assignment = new RoleAssignment();
    final SupervisoryNode node = new SupervisoryNode();
    node.setId(supervisoryNodeId);
    assignment.setSupervisoryNode(node);
    return assignment;
  }
}
