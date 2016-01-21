/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.domain.RightName.CREATE_REQUISITION;
import static org.openlmis.core.domain.RightName.VIEW_REQUISITION;
import static org.openlmis.core.domain.RightType.ADMIN;

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

  @Mock
  private ProgramService programService;

  @Mock
  private ProgramProductService programProductService;

  @Before
  public void setUp() throws Exception {
    role = new Role("role name", "role description");
    roleRightsService = new RoleRightsService(roleRightsRepository, supervisoryNodeService, facilityService, programService, programProductService);
  }

  @Test
  public void shouldSaveRole() {
    role.setRights(asList(new Right(VIEW_REQUISITION, RightType.REQUISITION), new Right(CREATE_REQUISITION, RightType.REQUISITION)));
    roleRightsService.saveRole(role);
    verify(roleRightsRepository).createRole(role);
  }

  @Test
  public void shouldNotSaveRoleWithoutAnyRights() {
    Role role = mock(Role.class);
    doThrow(new DataException("error-message")).when(role).validate();
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("error-message");
    roleRightsService.saveRole(role);
    verify(roleRightsRepository, never()).createRole(role);
  }

  @Test
  public void shouldGetRoleById() {
    Role role = new Role();
    Long roleId = 1L;
    when(roleRightsRepository.getRole(roleId)).thenReturn(role);

    assertThat(roleRightsService.getRole(roleId), is(role));

    verify(roleRightsRepository).getRole(roleId);
  }

  @Test
  public void shouldUpdateRole() {
    role.setRights(asList(new Right(CREATE_REQUISITION, ADMIN)));
    roleRightsService.updateRole(role);
    verify(roleRightsRepository).updateRole(role);
  }

  @Test
  public void shouldGetRightsForAUserOnSupervisedFacilityAndProgram() {
    Long userId = 1L;
    Facility facility = new Facility(2L);
    Program program = new Program(3L);
    List<Right> expected = asList(new Right(CREATE_REQUISITION, ADMIN));
    SupervisoryNode supervisoryNode = new SupervisoryNode(4L);
    List<SupervisoryNode> supervisoryNodes = asList(supervisoryNode);

    when(supervisoryNodeService.getFor(facility, program)).thenReturn(supervisoryNode);
    when(supervisoryNodeService.getAllParentSupervisoryNodesInHierarchy(supervisoryNode)).thenReturn(supervisoryNodes);
    when(roleRightsRepository.getRightsForUserOnSupervisoryNodeAndProgram(userId, supervisoryNodes, program)).thenReturn(
            expected);

    List<Right> result = roleRightsService.getRightsForUserAndFacilityProgram(userId, facility, program);

    verify(roleRightsRepository).getRightsForUserOnSupervisoryNodeAndProgram(userId, supervisoryNodes, program);
    assertThat(result.containsAll(expected), is(true));
  }

  @Test
  public void shouldGetRightsForAUserOnHomeFacilityAndProgram() {
    Long userId = 1L;
    Facility facility = new Facility(2L);
    Program program = new Program(3L);
    List<Right> expected = asList(new Right(CREATE_REQUISITION, ADMIN));

    when(facilityService.getHomeFacility(userId)).thenReturn(facility);
    when(roleRightsRepository.getRightsForUserOnHomeFacilityAndProgram(userId, program)).thenReturn(expected);

    List<Right> result = roleRightsService.getRightsForUserAndFacilityProgram(userId, facility, program);

    assertThat(result.containsAll(expected), is(true));
    verify(roleRightsRepository).getRightsForUserOnHomeFacilityAndProgram(userId, program);
  }

  // Test case commented out because it gives a Mockito exception that is not understood
  @Test
  public void shouldGetRightsForAUserOnFacilityAndProductCode() {
    Long userId = 1L;
    Long facilityId = 2L;
    String productCode = "3";
    Facility facility = new Facility(facilityId);
    Program program = new Program(3L);
    program.setCode("4");
    ProgramProduct programProduct = new ProgramProduct(5L);
    programProduct.setProgram(program);
    List<Right> expected = Collections.singletonList(new Right(CREATE_REQUISITION, ADMIN));

    when(facilityService.getById(facilityId)).thenReturn(facility);
    when(programProductService.getByProductCode(productCode)).thenReturn(Collections.singletonList(programProduct));
    when(programService.getByCode(any(String.class))).thenReturn(program);
    when(supervisoryNodeService.getFor(facility, program)).thenReturn(null);
    when(facilityService.getHomeFacility(userId)).thenReturn(facility);
    when(roleRightsRepository.getRightsForUserOnHomeFacilityAndProgram(userId, program)).thenReturn(expected);

    List<Right> result = roleRightsService.getRightsForUserFacilityAndProductCode(userId, facilityId, productCode);

    assertThat(result.containsAll(expected), is(true));
  }

  @Test
  public void shouldGetAllRolesMapByRightType() {
    List<Role> allRoles = new ArrayList<>();
    Role role1 = new Role();
    role1.setId(1L);
    Role role2 = new Role();
    role2.setId(2L);
    Role role3 = new Role();
    role3.setId(3L);
    Role role4 = new Role();
    role4.setId(4L);

    allRoles.add(role1);
    allRoles.add(role2);
    allRoles.add(role3);
    allRoles.add(role4);

    when(roleRightsRepository.getAllRoles()).thenReturn(allRoles);
    when(roleRightsRepository.getRightTypeForRoleId(1L)).thenReturn(RightType.REQUISITION);
    when(roleRightsRepository.getRightTypeForRoleId(2L)).thenReturn(ADMIN);
    when(roleRightsRepository.getRightTypeForRoleId(3L)).thenReturn(RightType.ALLOCATION);
    when(roleRightsRepository.getRightTypeForRoleId(4L)).thenReturn(RightType.ALLOCATION);

    Map<String, List<Role>> allRolesMap = roleRightsService.getAllRolesMap();

    assertThat(allRolesMap.size(), is(3));
    assertThat(allRolesMap.get(ADMIN.name()).size(), is(1));
    assertThat(allRolesMap.get(RightType.REQUISITION.name()).size(), is(1));
    assertThat(allRolesMap.get(RightType.ALLOCATION.name()).size(), is(2));
  }

  @Test
  public void shouldGetRightsForUserAndWarehouse() {
    List<Right> expectedRights = new ArrayList<>();
    Long userId = 1l;
    Long warehouseId = 2l;
    when(roleRightsRepository.getRightsForUserAndWarehouse(userId, warehouseId)).thenReturn(expectedRights);

    List<Right> rights = roleRightsService.getRightsForUserAndWarehouse(userId, warehouseId);

    assertThat(rights, is(expectedRights));
    verify(roleRightsRepository).getRightsForUserAndWarehouse(userId, warehouseId);
  }
}
