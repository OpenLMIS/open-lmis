/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.RoleAssignmentRepository;
import org.openlmis.core.repository.RoleRightsRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Boolean.FALSE;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.domain.Right.CONFIGURE_RNR;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

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
    assertThat(allRights.get(0), is(CONFIGURE_RNR));
    assertThat(allRights.get(1), is(Right.MANAGE_FACILITY));
    assertThat(allRights.get(2), is(Right.MANAGE_ROLE));
    assertThat(allRights.get(3), is(Right.MANAGE_SCHEDULE));
    assertThat(allRights.get(4), is(Right.MANAGE_USERS));
    assertThat(allRights.get(5), is(Right.UPLOAD_REPORT));
    assertThat(allRights.get(6), is(Right.UPLOADS));
    assertThat(allRights.get(7), is(Right.APPROVE_REQUISITION));
    assertThat(allRights.get(8), is(Right.AUTHORIZE_REQUISITION));
    assertThat(allRights.get(9), is(Right.CONVERT_TO_ORDER));
    assertThat(allRights.get(10), is(Right.CREATE_REQUISITION));
    assertThat(allRights.get(11), is(Right.VIEW_REQUISITION));
    assertThat(allRights.get(12), is(Right.VIEW_ORDER));
    assertThat(allRights.get(0).getAdminRight(), is(true));
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
    int roleId = 1;
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
    Integer userId = 1;
    Facility facility = new Facility(2);
    Program program = new Program(3);
    List<Right> expected = asList(CREATE_REQUISITION);
    SupervisoryNode supervisoryNode = new SupervisoryNode(4);
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
    Integer userId = 1;
    Facility facility = new Facility(2);
    Program program = new Program(3);
    List<Right> expected = asList(CREATE_REQUISITION);

    when(facilityService.getHomeFacility(userId)).thenReturn(facility);
    when(roleRightsRepository.getRightsForUserOnHomeFacilityAndProgram(userId, program)).thenReturn(expected);

    Set<Right> result = roleRightsService.getRightsForUserAndFacilityProgram(userId, facility, program);

    assertThat(result.containsAll(expected), is(true));
    verify(roleRightsRepository).getRightsForUserOnHomeFacilityAndProgram(userId, program);
  }
}
