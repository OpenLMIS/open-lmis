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

import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
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
    role = new Role("role name", "role description");
    roleRightsService = new RoleRightsService(roleRightsRepository, supervisoryNodeService, facilityService);
  }


  @Test
  public void shouldGetAllRightsByDisplayOrder() throws Exception {
    List<Right> allRights = new ArrayList<>(new RoleRightsService().getAllRights());
    List<Right> alphabeticalRights = asList(
            CONFIGURE_RNR,
            MANAGE_FACILITY,
            MANAGE_PROGRAM_PRODUCT,
            MANAGE_REGIMEN_TEMPLATE,
            MANAGE_ROLE, MANAGE_SCHEDULE,
            MANAGE_USER, UPLOADS,
            MANAGE_DISTRIBUTION,
            MANAGE_REPORT,
            VIEW_REPORT,
            APPROVE_REQUISITION,
            AUTHORIZE_REQUISITION,
            CONVERT_TO_ORDER,
            CREATE_REQUISITION,
            VIEW_REQUISITION,
            VIEW_ORDER,
            CONFIGURE_EDI,
            FACILITY_FILL_SHIPMENT,
            MANAGE_POD,
            ACCESS_ILS_GATEWAY,
            MANAGE_PRODUCT_ALLOWED_FOR_FACILITY,
            MANAGE_SETTING,
            MANAGE_REQ_GRP_PROG_SCHEDULE,
            MANAGE_SUPERVISORY_NODE,
            MANAGE_REQUISITION_GROUP,
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
            VIEW_SUPPLY_STATUS_REPORT,
            VIEW_STOCKED_OUT_REPORT,
            VIEW_DISTRICT_CONSUMPTION_REPORT,
            VIEW_ORDER_REPORT,
            VIEW_STOCK_IMBALANCE_REPORT,
            VIEW_RNR_FEEDBACK_REPORT);

    assertThat(allRights, is(alphabeticalRights));
  }

  @Test
  public void shouldCheckAdminRightHasIsAdminTrue() throws Exception {
    assertEquals(RightType.ADMIN, UPLOADS.getType());
    assertEquals(RightType.ADMIN, MANAGE_FACILITY.getType());
    assertEquals(RightType.ADMIN, MANAGE_PROGRAM_PRODUCT.getType());
    assertEquals(RightType.ADMIN, MANAGE_REPORT.getType());
    assertEquals(RightType.ADMIN, MANAGE_ROLE.getType());
    assertEquals(RightType.ADMIN, MANAGE_SCHEDULE.getType());
    assertEquals(RightType.ADMIN, MANAGE_USER.getType());
    assertEquals(RightType.ADMIN, CONFIGURE_RNR.getType());
  }

  @Test
  public void shouldCheckTransactionalRightIsNotAdminRight() throws Exception {
    assertEquals(RightType.REQUISITION, CREATE_REQUISITION.getType());
    assertEquals(RightType.REQUISITION, AUTHORIZE_REQUISITION.getType());
    assertEquals(RightType.REQUISITION, APPROVE_REQUISITION.getType());
    assertEquals(RightType.ALLOCATION, MANAGE_DISTRIBUTION.getType());
    assertEquals(RightType.REQUISITION, VIEW_REQUISITION.getType());
  }

  @Test
  public void shouldSaveRole() throws Exception {
    role.setRights(new HashSet<>(asList(CREATE_REQUISITION, VIEW_REQUISITION)));
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

  @Test
  public void shouldGetAllRolesMapByRightType() throws Exception {
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
    when(roleRightsRepository.getRightTypeForRoleId(2L)).thenReturn(RightType.ADMIN);
    when(roleRightsRepository.getRightTypeForRoleId(3L)).thenReturn(RightType.ALLOCATION);
    when(roleRightsRepository.getRightTypeForRoleId(4L)).thenReturn(RightType.ALLOCATION);

    Map<String, List<Role>> allRolesMap = roleRightsService.getAllRolesMap();

    assertThat(allRolesMap.size(), is(3));
    assertThat(allRolesMap.get(RightType.ADMIN.name()).size(), is(1));
    assertThat(allRolesMap.get(RightType.REQUISITION.name()).size(), is(1));
    assertThat(allRolesMap.get(RightType.ALLOCATION.name()).size(), is(2));
  }


  @Test
  public void shouldGetRightsForUserAndWarehouse() {
    Set<Right> expectedRights = new HashSet<>();
    Long userId = 1l;
    Long warehouseId = 2l;
    when(roleRightsRepository.getRightsForUserAndWarehouse(userId, warehouseId)).thenReturn(expectedRights);

    Set<Right> rights = roleRightsService.getRightsForUserAndWarehouse(userId, warehouseId);

    assertThat(rights, is(expectedRights));
    verify(roleRightsRepository).getRightsForUserAndWarehouse(userId, warehouseId);

  }
}
