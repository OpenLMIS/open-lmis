/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.RoleAssignmentRepository;
import org.openlmis.core.repository.RoleRightsRepository;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Boolean.FALSE;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.openlmis.core.domain.Right.*;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RoleRightsServiceTest {

  Role role;
  RoleRightsService roleRightsService;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  RoleRightsRepository roleRightsRepository;

  @Mock
  RoleAssignmentRepository roleAssignmentRepository;
  @Mock
  private SupervisoryNodeService supervisoryNodeService;
  @Mock
  private FacilityService facilityService;

  @Before
  public void setUp() throws Exception {
    role = new Role("role name", FALSE, "role description");
    roleRightsService = new RoleRightsService(roleRightsRepository, supervisoryNodeService, facilityService);
  }


  @Test
  public void shouldGetAllRightsInAlphabeticalOrder() throws Exception {
    List<Right> allRights = new ArrayList<>(new RoleRightsService().getAllRights());
    List<Right> alphabeticalRights = asList(CONFIGURE_RNR,
      MANAGE_FACILITY,
      MANAGE_PROGRAM_PRODUCT,
      MANAGE_ROLE,
      MANAGE_SCHEDULE,
      MANAGE_USERS,
      UPLOADS,
      MANAGE_DISTRIBUTION,
      MANAGE_REPORTS,
      VIEW_REPORTS,
      APPROVE_REQUISITION,
      AUTHORIZE_REQUISITION,
      CONVERT_TO_ORDER,
      CREATE_REQUISITION,
      VIEW_REQUISITION,
      VIEW_ORDER);

    assertThat(allRights, is(alphabeticalRights));
  }

  @Test
  public void shouldCheckAdminRightHasIsAdminTrue() throws Exception {
    assertTrue(UPLOADS.getAdminRight());
    assertTrue(MANAGE_FACILITY.getAdminRight());
    assertTrue(MANAGE_PROGRAM_PRODUCT.getAdminRight());
    assertTrue(MANAGE_REPORTS.getAdminRight());
    assertTrue(MANAGE_ROLE.getAdminRight());
    assertTrue(MANAGE_SCHEDULE.getAdminRight());
    assertTrue(MANAGE_USERS.getAdminRight());
    assertTrue(CONVERT_TO_ORDER.getAdminRight());
    assertTrue(VIEW_ORDER.getAdminRight());
    assertTrue(CONFIGURE_RNR.getAdminRight());
  }

  @Test
  public void shouldCheckTransactionalRightIsNotAdminRight() throws Exception {
    assertFalse(CREATE_REQUISITION.getAdminRight());
    assertFalse(AUTHORIZE_REQUISITION.getAdminRight());
    assertFalse(APPROVE_REQUISITION.getAdminRight());
    assertFalse(MANAGE_DISTRIBUTION.getAdminRight());
    assertFalse(VIEW_REQUISITION.getAdminRight());
  }

  @Test
  public void shouldSaveRole() throws Exception {
    role.setRights(new HashSet<>(asList(CREATE_REQUISITION)));
    roleRightsService.saveRole(role);
    verify(roleRightsRepository).createRole(role);
  }

  @Test
  public void shouldNotSaveRoleWithoutAnyRights() throws Exception {
    Role role = mock(Role.class);
    doThrow(new DataException("error-message")).when(role).validate();
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error-message");
    roleRightsService.saveRole(role);
    verify(roleRightsRepository, never()).createRole(role);
  }

  @Test
  public void shouldReturnAllRoles() throws Exception {
    List<Role> allRoles = new ArrayList<>();
    when(roleRightsRepository.getAllRoles()).thenReturn(allRoles);

    assertThat(roleRightsService.getAllRoles(), is(allRoles));

    verify(roleRightsRepository).getAllRoles();
  }

  @Test
  public void shouldGetRoleById() throws Exception {
    Role role = new Role();
    Long roleId = 1L;
    when(roleRightsRepository.getRole(roleId)).thenReturn(role);

    assertThat(roleRightsService.getRole(roleId), is(role));

    verify(roleRightsRepository).getRole(roleId);
  }

  @Test
  public void shouldUpdateRole() {
    role.setRights(new HashSet<>(asList(CREATE_REQUISITION)));
    roleRightsService.updateRole(role);
    verify(roleRightsRepository).updateRole(role);
  }

  @Test
  public void shouldGetRightsForAUserOnSupervisedFacilityAndProgram() throws Exception {
    Long userId = 1L;
    Facility facility = new Facility(2L);
    Program program = new Program(3L);
    List<Right> expected = asList(CREATE_REQUISITION);
    SupervisoryNode supervisoryNode = new SupervisoryNode(4L);
    List<SupervisoryNode> supervisoryNodes = asList(supervisoryNode);

    when(supervisoryNodeService.getFor(facility, program)).thenReturn(supervisoryNode);
    when(supervisoryNodeService.getAllParentSupervisoryNodesInHierarchy(supervisoryNode)).thenReturn(supervisoryNodes);
    when(
      roleRightsRepository.getRightsForUserOnSupervisoryNodeAndProgram(userId, supervisoryNodes, program)).thenReturn(
      expected);

    Set<Right> result = roleRightsService.getRightsForUserAndFacilityProgram(userId, facility, program);

    verify(roleRightsRepository).getRightsForUserOnSupervisoryNodeAndProgram(userId, supervisoryNodes, program);
    assertThat(result.containsAll(expected), is(true));
  }

  @Test
  public void shouldGetRightsForAUserOnHomeFacilityAndProgram() throws Exception {
    Long userId = 1L;
    Facility facility = new Facility(2L);
    Program program = new Program(3L);
    List<Right> expected = asList(CREATE_REQUISITION);

    when(facilityService.getHomeFacility(userId)).thenReturn(facility);
    when(roleRightsRepository.getRightsForUserOnHomeFacilityAndProgram(userId, program)).thenReturn(expected);

    Set<Right> result = roleRightsService.getRightsForUserAndFacilityProgram(userId, facility, program);

    assertThat(result.containsAll(expected), is(true));
    verify(roleRightsRepository).getRightsForUserOnHomeFacilityAndProgram(userId, program);
  }
}
