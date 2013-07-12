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

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.domain.Right.*;
import static org.openlmis.core.domain.RoleType.REQUISITION;

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
    role = new Role("role name", REQUISITION, "role description");
    roleRightsService = new RoleRightsService(roleRightsRepository, supervisoryNodeService, facilityService);
  }


  @Test
  public void shouldGetAllRightsInAlphabeticalOrder() throws Exception {
    List<Right> allRights = new ArrayList<>(new RoleRightsService().getAllRights());
    List<Right> alphabeticalRights = asList(CONFIGURE_RNR,
      MANAGE_FACILITY,
      MANAGE_PROGRAM_PRODUCT,
      MANAGE_REGIMEN_TEMPLATE,
      MANAGE_ROLE,
      MANAGE_SCHEDULE,
      MANAGE_USER,
      UPLOADS,
      MANAGE_DISTRIBUTION,
      MANAGE_REPORT,
      VIEW_REPORT,
      APPROVE_REQUISITION,
      AUTHORIZE_REQUISITION,
      CONVERT_TO_ORDER,
      CREATE_REQUISITION,
      VIEW_REQUISITION,
      VIEW_ORDER,
            MANAGE_PRODUCT,
            MANAGE_SUPPLYLINE,
            MANAGE_GEOGRAPHIC_ZONES,
            VIEW_FACILITY_REPORT,
            VIEW_MAILING_LABEL_REPORT,
            VIEW_SUMMARY_REPORT,
            VIEW_CONSUMPTION_REPORT,
            VIEW_AVERAGE_CONSUMPTION_REPORT,
            VIEW_REPORTING_RATE_REPORT,
            VIEW_NON_REPORTING_FACILITIES,
            VIEW_ADJUSTMENT_SUMMARY_REPORT,
            VIEW_SUPPLY_STATUS_REPORT, VIEW_STOCKED_OUT_REPORT,
            VIEW_DISTRICT_CONSUMPTION_REPORT
    );

    assertThat(allRights, is(alphabeticalRights));
  }

  @Test
  public void shouldCheckAdminRightHasIsAdminTrue() throws Exception {
    assertEquals(UPLOADS.getType(), RightType.ADMIN);
    assertEquals(MANAGE_FACILITY.getType(), RightType.ADMIN);
    assertEquals(MANAGE_PROGRAM_PRODUCT.getType(), RightType.ADMIN);
    assertEquals(MANAGE_REPORT.getType(), RightType.ADMIN);
    assertEquals(MANAGE_ROLE.getType(), RightType.ADMIN);
    assertEquals(MANAGE_SCHEDULE.getType(), RightType.ADMIN);
    assertEquals(MANAGE_USER.getType(), RightType.ADMIN);
    assertEquals(CONVERT_TO_ORDER.getType(), RightType.ADMIN);
    assertEquals(VIEW_ORDER.getType(), RightType.ADMIN);
    assertEquals(CONFIGURE_RNR.getType(), RightType.ADMIN);
  }

  @Test
  public void shouldCheckTransactionalRightIsNotAdminRight() throws Exception {
    assertEquals(CREATE_REQUISITION.getType(), RightType.REQUISITION);
    assertEquals(AUTHORIZE_REQUISITION.getType(), RightType.REQUISITION);
    assertEquals(APPROVE_REQUISITION.getType(), RightType.REQUISITION);
    assertEquals(MANAGE_DISTRIBUTION.getType(), RightType.ALLOCATION);
    assertEquals(VIEW_REQUISITION.getType(), RightType.REQUISITION);
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
