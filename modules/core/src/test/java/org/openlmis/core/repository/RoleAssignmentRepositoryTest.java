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
import org.openlmis.core.domain.RoleAssignment;
import org.openlmis.core.repository.mapper.RoleAssignmentMapper;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
  public void shouldInsertUserProgramRoleMapping() throws Exception {
    repository.insertRoleAssignment(1L, 3L, 1L, 2L);

    verify(mapper).insertRoleAssignment(1L, 3L, 1L, 2L);
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
}
