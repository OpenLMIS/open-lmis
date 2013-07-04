/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.builder.RoleAssignmentBuilder;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.User;
import org.openlmis.core.repository.mapper.RoleAssignmentMapper;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static com.natpryce.makeiteasy.MakeItEasy.with;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.core.builder.RoleAssignmentBuilder.defaultRoleAssignment;
import static org.openlmis.core.builder.RoleAssignmentBuilder.supervisoryNode;
import static org.openlmis.core.builder.SupervisoryNodeBuilder.*;
import static org.openlmis.core.domain.Right.AUTHORIZE_REQUISITION;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RoleAssignmentRepositoryTest {
  private RoleAssignmentRepository repository;

  @Mock
  private RoleAssignmentMapper mapper;

  @Before
  public void setUp() throws Exception {
    repository = new RoleAssignmentRepository(mapper);
  }

  @Test
  public void shouldDeleteAllRoleAssignmentsForTheUser() throws Exception {
    repository.deleteAllRoleAssignmentsForUser(1L);

    verify(mapper).deleteAllRoleAssignmentsForUser(1L);
  }

  @Test
  public void shouldGetSupervisorRoles() throws Exception {
    List<RoleAssignment> expected = new ArrayList<>();
    when(mapper.getSupervisorRoles(1L)).thenReturn(expected);
    List<RoleAssignment> actual = repository.getSupervisorRoles(1L);
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetHomeFacilityRoles() throws Exception {
    List<RoleAssignment> expected = new ArrayList<>();
    when(mapper.getHomeFacilityRoles(1L)).thenReturn(expected);
    List<RoleAssignment> actual = repository.getHomeFacilityRoles(1L);
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetAdminRoles() throws Exception {
    RoleAssignment expected = new RoleAssignment();
    when(mapper.getAdminRole(1L)).thenReturn(expected);
    RoleAssignment actual = repository.getAdminRole(1L);
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldGetAllocationRoles() throws Exception {
    List<RoleAssignment> expected = new ArrayList<>();
    when(mapper.getAllocationRoles(1L)).thenReturn(expected);
    List<RoleAssignment> allocationRoles = repository.getAllocationRoles(1L);
    assertThat(allocationRoles, is(expected));
    verify(mapper).getAllocationRoles(1L);
  }

  @Test
  public void shouldGetRoleAssignmentsForAGivenUserOnAGivenProgramWithRights() throws Exception {
    Long userId = 1L;
    Long programId = 2L;
    List<RoleAssignment> expected = new ArrayList<>();
    when(mapper.getHomeFacilityRolesForUserOnGivenProgramWithRights(userId, programId,
      "['CREATE_REQUISITION', 'AUTHORIZE_REQUISITION']")).thenReturn(expected);
    List<RoleAssignment> actual = repository.getHomeFacilityRolesForUserOnGivenProgramWithRights(userId, programId,
      CREATE_REQUISITION, AUTHORIZE_REQUISITION);
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldSaveRoleAssignment() throws Exception {
    final List<Long> roles = new ArrayList<Long>() {{
      add(1l);
      add(2l);
      add(3l);
    }};

    final List<Long> roles2 = new ArrayList<Long>() {{
      add(5l);
      add(6l);
      add(7l);
    }};

    SupervisoryNode node = make(a(defaultSupervisoryNode, with(id, 1l)));
    RoleAssignment roleAssignment = make(a(defaultRoleAssignment, with(RoleAssignmentBuilder.roleIds, roles), with(supervisoryNode, node)));
    RoleAssignment roleAssignment2 = make(a(defaultRoleAssignment, with(RoleAssignmentBuilder.roleIds, roles2), with(supervisoryNode, node)));
    List<RoleAssignment> roleAssignments = new ArrayList<>();
    roleAssignments.add(roleAssignment);
    roleAssignments.add(roleAssignment2);

    repository.insert(roleAssignments, 1L);

    verify(mapper).insert(1L, roleAssignment.getProgramId(),
      roleAssignment.getSupervisoryNode(), roleAssignment.getDeliveryZone(), 1l);
    verify(mapper).insert(1L, roleAssignment.getProgramId(),
      roleAssignment.getSupervisoryNode(), roleAssignment.getDeliveryZone(), 2l);
    verify(mapper).insert(1L, roleAssignment.getProgramId(),
      roleAssignment.getSupervisoryNode(), roleAssignment.getDeliveryZone(), 3l);

    verify(mapper).insert(1L, roleAssignment.getProgramId(),
      roleAssignment.getSupervisoryNode(), roleAssignment.getDeliveryZone(), 5l);
    verify(mapper).insert(1L, roleAssignment.getProgramId(),
      roleAssignment.getSupervisoryNode(), roleAssignment.getDeliveryZone(), 6l);
    verify(mapper).insert(1L, roleAssignment.getProgramId(),
      roleAssignment.getSupervisoryNode(), roleAssignment.getDeliveryZone(), 7l);
  }

  @Test
  public void shouldNotInsertIfRoleAssignmentListIsNull() throws Exception {
    User user = new User();

    repository.insert(user.getHomeFacilityRoles(), user.getId());

    verify(mapper, never()).insert(anyLong(),anyLong(),any(SupervisoryNode.class),any(DeliveryZone.class),anyLong());
  }
}
