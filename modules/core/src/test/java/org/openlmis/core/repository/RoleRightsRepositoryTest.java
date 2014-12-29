/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
import org.openlmis.core.domain.*;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.repository.mapper.RoleRightsMapper;
import org.openlmis.db.categories.UnitTests;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.domain.RightName.CONFIGURE_RNR;
import static org.openlmis.core.domain.RightName.CREATE_REQUISITION;
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
    role = new Role("role name", "role description");
    roleRightsRepository = new RoleRightsRepository(roleRightsMapper, commaSeparator);
  }

  @Test
  public void shouldSaveRoleWithMappings() throws Exception {
    role.setRights(asList(new Right(CONFIGURE_RNR, RightType.ADMIN), new Right(CREATE_REQUISITION, RightType.REQUISITION)));
    role.setId(1L);
    roleRightsRepository.createRole(role);

    verify(roleRightsMapper).insertRole(role);
    verify(roleRightsMapper).createRoleRight(role, CONFIGURE_RNR);
    verify(roleRightsMapper).createRoleRight(role, CREATE_REQUISITION);
  }

  @Test
  public void shouldNotSaveDuplicateRole() throws Exception {
    doThrow(DuplicateKeyException.class).when(roleRightsMapper).insertRole(role);

    expectedEx.expect(dataExceptionMatcher("error.duplicate.role"));

    roleRightsRepository.createRole(role);
  }

  @Test
  public void shouldNotUpdateToDuplicateRoleName() {
    Role role = new Role("Name", "Desc");
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
    role.setRights(asList(new Right(CONFIGURE_RNR, RightType.ADMIN)));
    role.setId(100L);
    roleRightsRepository.updateRole(role);
    verify(roleRightsMapper).updateRole(role);
    verify(roleRightsMapper).deleteAllRightsForRole(100L);
    verify(roleRightsMapper).createRoleRight(role, CONFIGURE_RNR);
  }

  @Test
  public void shouldGetRightsForAUserOnSupervisoryNodeAndProgram() throws Exception {
    Long userId = 1L;
    List<SupervisoryNode> supervisoryNodes = asList(new SupervisoryNode(2L), new SupervisoryNode(3L));
    Program program = new Program(3L);
    when(commaSeparator.commaSeparateIds(supervisoryNodes)).thenReturn("{2, 3}");
    when(roleRightsMapper.getRightsForUserOnSupervisoryNodeAndProgram(userId, "{2, 3}", program)).thenReturn(null);
    List<Right> result = roleRightsRepository.getRightsForUserOnSupervisoryNodeAndProgram(userId, supervisoryNodes, program);
    verify(roleRightsMapper).getRightsForUserOnSupervisoryNodeAndProgram(userId, "{2, 3}", program);
    assertNull(result);
  }

  @Test
  public void shouldGetRightsForAUserOnHomeFacilityAndProgram() throws Exception {
    Long userId = 1L;
    Program program = new Program(3L);
    when(roleRightsMapper.getRightsForUserOnHomeFacilityAndProgram(userId, program)).thenReturn(null);
    List<Right> result = roleRightsRepository.getRightsForUserOnHomeFacilityAndProgram(userId, program);
    verify(roleRightsMapper).getRightsForUserOnHomeFacilityAndProgram(userId, program);
    assertNull(result);
  }

  @Test
  public void shouldGetRightsForUserAndWarehouse() {
    List<Right> expectedRights = new ArrayList<>();
    Long userId = 1l;
    Long warehouseId = 2l;
    when(roleRightsMapper.getRightsForUserAndWarehouse(userId, warehouseId)).thenReturn(expectedRights);

    List<Right> rights = roleRightsRepository.getRightsForUserAndWarehouse(userId, warehouseId);

    assertThat(rights, is(expectedRights));
    verify(roleRightsMapper).getRightsForUserAndWarehouse(userId, warehouseId);
  }
}
