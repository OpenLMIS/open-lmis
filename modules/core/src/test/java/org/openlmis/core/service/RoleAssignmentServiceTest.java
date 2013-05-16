/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.RoleAssignmentRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.domain.Right.AUTHORIZE_REQUISITION;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

@RunWith(MockitoJUnitRunner.class)
@Category(UnitTests.class)
 public class RoleAssignmentServiceTest {

  RoleAssignmentService service;

  @Mock
  RoleAssignmentRepository roleAssignmentRepository;

  @Before
  public void setUp() throws Exception {
    service = new RoleAssignmentService(roleAssignmentRepository);
  }

  @Test
  public void shouldSaveRoleAssignments() throws Exception {
    List<RoleAssignment> roleAssignments = Arrays.asList(new RoleAssignment(1L, 1L, 1L, new SupervisoryNode(1L)));
    User user = new User();
    user.setId(1L);
    user.setSupervisorRoles(roleAssignments);
    service.saveSupervisoryRoles(user);

    verify(roleAssignmentRepository).insertRoleAssignment(1L, 1L, 1L, 1L);
  }

  @Test
  public void shouldSaveAdminRoleAssignment() throws Exception {
    RoleAssignment adminRoleAssignment = new RoleAssignment();
    adminRoleAssignment.setRoleId(1L);
    User user = new User();
    user.setId(1L);
    user.setAdminRole(adminRoleAssignment);
    service.saveAdminRole(user);

    verify(roleAssignmentRepository).insertRoleAssignment(1L, null, null, 1L);
  }

  @Test
  public void shouldDeleteRoleAssignmentsOfAUser() throws Exception {
    service.deleteAllRoleAssignmentsForUser(1L);
    verify(roleAssignmentRepository).deleteAllRoleAssignmentsForUser(1L);
  }

  @Test
  public void shouldGetSupervisorRoleAssignments() throws Exception {

    List<RoleAssignment> expected = new ArrayList<>();
    when(roleAssignmentRepository.getSupervisorRoles(1L)).thenReturn(expected);
    List<RoleAssignment> actual = service.getSupervisorRoles(1L);

    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetHomeFacilityRoleAssignments() throws Exception {

    List<RoleAssignment> expected = new ArrayList<>();
    when(roleAssignmentRepository.getHomeFacilityRoles(1L)).thenReturn(expected);
    List<RoleAssignment> actual = service.getHomeFacilityRoles(1L);

    assertThat(actual, is(expected));
  }
  @Test
  public void shouldGetAdminRoleAssignments() throws Exception {

    RoleAssignment expected = new RoleAssignment();
    when(roleAssignmentRepository.getAdminRole(1L)).thenReturn(expected);
    RoleAssignment actual = service.getAdminRole(1L);

    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetRoleAssignmentsForAGivenUserOnAGivenProgramWithRights() throws Exception {
    Long userId =1L;
    Long programId =2L;
    List<RoleAssignment> expected = new ArrayList<>();
    when(roleAssignmentRepository.getHomeFacilityRolesForUserOnGivenProgramWithRights(userId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION)).thenReturn(expected);
    List<RoleAssignment> actual = service.getHomeFacilityRolesForUserOnGivenProgramWithRights(userId, programId, CREATE_REQUISITION, AUTHORIZE_REQUISITION);

    assertThat(actual, is(expected));
  }
}
