/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.Role;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.repository.mapper.RoleRightsMapper;
import org.openlmis.db.categories.UnitTests;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.domain.Right.*;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class RoleRightsRepositoryTest {

  private Role role;
  @Mock
  RoleRightsMapper roleRightsMapper;
  @Mock
  private CommaSeparator commaSeparator;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();
  private RoleRightsRepository roleRightsRepository;


  @Before
  public void setUp() throws Exception {
    role = new Role("role name", FALSE, "role description");
    roleRightsRepository = new RoleRightsRepository(roleRightsMapper, commaSeparator);
  }

  @Test
  public void shouldSaveRoleWithMappings() throws Exception {
    role.setRights(new HashSet<>(asList(CONFIGURE_RNR, CREATE_REQUISITION)));
    role.setId(1L);
    roleRightsRepository.createRole(role);

    verify(roleRightsMapper).insertRole(role);
    verify(roleRightsMapper).createRoleRight(role, CONFIGURE_RNR);
    verify(roleRightsMapper).createRoleRight(role, CREATE_REQUISITION);
  }

  @Test
  public void shouldSaveRoleWithMappingsAndTheirDependentMappings() throws Exception {
    role.setRights(new HashSet<>(asList(CONFIGURE_RNR, CREATE_REQUISITION)));
    role.setId(1L);
    roleRightsRepository.createRole(role);

    verify(roleRightsMapper).insertRole(role);
    verify(roleRightsMapper).createRoleRight(role, CONFIGURE_RNR);
    verify(roleRightsMapper).createRoleRight(role, CREATE_REQUISITION);
    verify(roleRightsMapper, times(1)).createRoleRight(role, VIEW_REQUISITION);
  }

  @Test
  public void shouldNotSaveDuplicateRole() throws Exception {
    doThrow(DuplicateKeyException.class).when(roleRightsMapper).insertRole(role);

    expectedEx.expect(dataExceptionMatcher("error.duplicate.role"));

    roleRightsRepository.createRole(role);
  }

  @Test
  public void shouldNotUpdateToDuplicateRoleName() {
    Role role = new Role("Name", FALSE, "Desc");
    role.setId(123L);
    doThrow(DuplicateKeyException.class).when(roleRightsMapper).updateRole(role);

    expectedEx.expect(dataExceptionMatcher("error.duplicate.role"));

    roleRightsRepository.updateRole(role);
  }

  @Test
  public void shouldGetAllRolesInTheSystem() throws Exception {
    List<Role> roles = new ArrayList<>();
    when(roleRightsMapper.getAllRoles()).thenReturn(roles);
    List<Role> allRoles = roleRightsRepository.getAllRoles();
    assertThat(allRoles, is(roles));
    verify(roleRightsMapper).getAllRoles();
  }

  @Test
  public void shouldGetRoleById() throws Exception {
    Role role = new Role();
    Long roleId = 1L;
    when(roleRightsMapper.getRole(roleId)).thenReturn(role);

    Role fetchedRole = roleRightsRepository.getRole(roleId);

    assertThat(fetchedRole, is(role));
    verify(roleRightsMapper).getRole(roleId);
  }

  @Test
  public void shouldUpdateRole() {
    role.setRights(new HashSet<>(asList(CONFIGURE_RNR)));
    role.setId(100L);
    roleRightsRepository.updateRole(role);
    verify(roleRightsMapper).updateRole(role);
    verify(roleRightsMapper).deleteAllRightsForRole(100L);
    verify(roleRightsMapper).createRoleRight(role, CONFIGURE_RNR);
  }

  @Test
  public void shouldUpdateRoleAlongWithDependentRights() {
    role.setRights(new HashSet<>(asList(CREATE_REQUISITION)));
    role.setId(100L);

    roleRightsRepository.updateRole(role);

    verify(roleRightsMapper).updateRole(role);
    verify(roleRightsMapper).deleteAllRightsForRole(100L);
    verify(roleRightsMapper).createRoleRight(role, CREATE_REQUISITION);
    verify(roleRightsMapper).createRoleRight(role, VIEW_REQUISITION);
  }

  @Test
  public void shouldGetRightsForAUserOnSupervisoryNodeAndProgram() throws Exception {
    Long userId = 1L;
    List<SupervisoryNode> supervisoryNodes = asList(new SupervisoryNode(2L), new SupervisoryNode(3L));
    Program program = new Program(3L);
    List<Right> expected = null;
    when(commaSeparator.commaSeparateIds(supervisoryNodes)).thenReturn("{2, 3}");
    when(roleRightsMapper.getRightsForUserOnSupervisoryNodeAndProgram(userId, "{2, 3}", program)).thenReturn(expected);
    List<Right> result = roleRightsRepository.getRightsForUserOnSupervisoryNodeAndProgram(userId, supervisoryNodes, program);
    verify(roleRightsMapper).getRightsForUserOnSupervisoryNodeAndProgram(userId, "{2, 3}", program);
    assertThat(result, is(expected));
  }


  @Test
  public void shouldGetRightsForAUserOnHomeFacilityAndProgram() throws Exception {
    Long userId = 1L;
    Program program = new Program(3L);
    List<Right> expected = null;
    when(roleRightsMapper.getRightsForUserOnHomeFacilityAndProgram(userId, program)).thenReturn(expected);
    List<Right> result = roleRightsRepository.getRightsForUserOnHomeFacilityAndProgram(userId, program);
    verify(roleRightsMapper).getRightsForUserOnHomeFacilityAndProgram(userId, program);
    assertThat(result, is(expected));
  }
}
