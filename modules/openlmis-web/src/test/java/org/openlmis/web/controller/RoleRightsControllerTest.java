/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.core.domain.*;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.core.service.RightService;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.core.web.OpenLmisResponse;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.openlmis.core.domain.RightName.CONFIGURE_RNR;
import static org.openlmis.core.domain.RightName.CREATE_REQUISITION;
import static org.openlmis.core.domain.RightType.ADMIN;
import static org.openlmis.core.domain.RightType.REQUISITION;
import static org.openlmis.core.matchers.Matchers.facilityMatcher;
import static org.openlmis.core.matchers.Matchers.programMatcher;
import static org.openlmis.web.controller.RoleRightsController.*;
import static org.openlmis.core.web.OpenLmisResponse.SUCCESS;

@Category(UnitTests.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(BlockJUnit4ClassRunner.class)
public class RoleRightsControllerTest {

  Role role;
  private static final Long LOGGED_IN_USERID = 11L;
  private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

  @Mock
  RoleRightsService roleRightsService;

  @Mock
  RightService rightService;

  @Mock
  MessageService messageService;

  @InjectMocks
  private RoleRightsController controller;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    MockHttpSession mockHttpSession = new MockHttpSession();
    httpServletRequest.setSession(mockHttpSession);
    mockHttpSession.setAttribute(USER_ID, LOGGED_IN_USERID);
    role = new Role("test role", "test role description");
  }

  @Test
  public void shouldFetchAllRightsInSystem() throws Exception {
    List<Right> rights = new ArrayList<>();
    when(rightService.getAll()).thenReturn(rights);
    ResponseEntity<OpenLmisResponse> result = controller.getAllRights();
    assertThat((List<Right>) result.getBody().getData().get(RIGHTS), is(rights));
    verify(rightService).getAll();
  }

  @Test
  public void shouldSaveRole() throws Exception {
    when(messageService.message("message.role.created.success", "test role")).thenReturn("'test role' created successfully");
    ResponseEntity<OpenLmisResponse> responseEntity = controller.createRole(role, httpServletRequest);
    verify(roleRightsService).saveRole(role);
    String successMsg = (String) responseEntity.getBody().getData().get(SUCCESS);
    assertThat(successMsg, is("'test role' created successfully"));
  }

  @Test
  public void shouldGiveErrorIfRoleNotSaved() throws Exception {
    doThrow(new DataException("Error message")).when(roleRightsService).saveRole(role);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.createRole(role, httpServletRequest);

    verify(roleRightsService).saveRole(role);
    assertThat(responseEntity.getBody().getErrorMsg(), is("Error message"));
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.CONFLICT));
  }

  @Test
  public void shouldGetAllRolesWithRights() throws Exception {
    Map<String, List<Role>> roles_map = new HashMap<>();
    when(roleRightsService.getAllRolesMap()).thenReturn(roles_map);

    OpenLmisResponse response = controller.getAll().getBody();

    assertThat((Map<String, List<Role>>) response.getData().get(ROLES_MAP), is(roles_map));
    verify(roleRightsService).getAllRolesMap();
  }

  @Test
  public void shouldGetRoleById() throws Exception {
    Role role = new Role();
    Long roleId = 1L;
    when(roleRightsService.getRole(roleId)).thenReturn(role);

    OpenLmisResponse response = controller.get(roleId).getBody();

    assertThat((Role) response.getData().get(ROLE), is(role));
    verify(roleRightsService).getRole(roleId);
  }

  @Test
  public void shouldGetRightTypeForRoleId() throws Exception {
    Role role = new Role();
    Long roleId = 1L;
    when(roleRightsService.getRole(roleId)).thenReturn(role);
    when(roleRightsService.getRightTypeForRoleId(roleId)).thenReturn(ADMIN);

    OpenLmisResponse response = controller.get(roleId).getBody();

    assertThat((Role) response.getData().get(ROLE), is(role));
    assertThat((RightType) response.getData().get(RIGHT_TYPE), is(ADMIN));

    verify(roleRightsService).getRole(roleId);
    verify(roleRightsService).getRightTypeForRoleId(roleId);
  }

  @Test
  public void shouldUpdateRoleAndRights() throws Exception {
    Role role = new Role("Role Name", "Desc", asList(new Right(CONFIGURE_RNR, ADMIN)));
    when(messageService.message("message.role.updated.success", "Role Name")).thenReturn("Role Name updated successfully");

    OpenLmisResponse response = controller.updateRole(role.getId(), role, httpServletRequest).getBody();

    assertThat(response.getSuccessMsg(), is("Role Name updated successfully"));
    verify(roleRightsService).updateRole(role);
  }

  @Test
  public void shouldReturnErrorMsgIfUpdateFails() throws Exception {

    Role role = new Role("Role Name", "Desc", null);

    doThrow(new DataException("Duplicate Role found")).when(roleRightsService).updateRole(role);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.updateRole(role.getId(), role, httpServletRequest);

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.CONFLICT));
    assertThat(responseEntity.getBody().getErrorMsg(), is("Duplicate Role found"));
  }

  @Test
  public void shouldGetRightsForUserAndFacilityProgram() throws Exception {
    List<Right> rights = asList(new Right(CREATE_REQUISITION, REQUISITION));

    Long facilityId = 1L;
    Long programId = 1L;
    when(roleRightsService.getRightsForUserAndFacilityProgram(eq(LOGGED_IN_USERID), any(Facility.class), any(Program.class))).thenReturn(rights);

    ResponseEntity<OpenLmisResponse> response = controller.getRightsForUserAndFacilityProgram(facilityId, programId, httpServletRequest);

    assertThat((List<Right>)response.getBody().getData().get("rights"), is(rights));
    verify(roleRightsService).getRightsForUserAndFacilityProgram(eq(LOGGED_IN_USERID), argThat(facilityMatcher(facilityId)), argThat(programMatcher(programId)));
  }
}
