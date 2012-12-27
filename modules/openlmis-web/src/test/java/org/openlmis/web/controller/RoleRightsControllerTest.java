package org.openlmis.web.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.Role;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.openlmis.core.domain.Right.CONFIGURE_RNR;
import static org.openlmis.web.response.OpenLmisResponse.*;

@RunWith(MockitoJUnitRunner.class)
public class RoleRightsControllerTest {

  Role role;

  @Mock
  RoleRightsService roleRightsService;

  private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

  RoleRightsController controller;
  private static final int LOGGED_IN_USERID = 11;

  @Before
  public void setUp() throws Exception {
    controller = new RoleRightsController(roleRightsService);
    MockHttpSession mockHttpSession = new MockHttpSession();
    httpServletRequest.setSession(mockHttpSession);
    mockHttpSession.setAttribute(USER_ID, LOGGED_IN_USERID);
    role = new Role("test role", "test role description");
  }

  @Test
  public void shouldFetchAllRightsInSystem() throws Exception {
    List<Right> rights = new ArrayList<>();
    when(roleRightsService.getAllRights()).thenReturn(rights);
    ResponseEntity<OpenLmisResponse> result = controller.getAllRights();
    assertThat((List<Right>) result.getBody().getData().get(RIGHTS), is(rights));
    verify(roleRightsService).getAllRights();
  }

  @Test
  public void shouldSaveRole() throws Exception {
    ResponseEntity<OpenLmisResponse> responseEntity = controller.createRole(role, httpServletRequest);
    verify(roleRightsService).saveRole(role);
    assertThat(role.getModifiedBy(), is(LOGGED_IN_USERID));
    String successMsg = (String) responseEntity.getBody().getData().get(SUCCESS);
    assertThat(successMsg, is("'test role' created successfully"));
  }

  @Test
  public void shouldGiveErrorIfRoleNotSaved() throws Exception {
    doThrow(new RuntimeException("Error message")).when(roleRightsService).saveRole(role);
    ResponseEntity<OpenLmisResponse> responseEntity = controller.createRole(role, httpServletRequest);
    verify(roleRightsService).saveRole(role);
    assertThat(responseEntity.getBody().getErrorMsg(), is("Error message"));
  }

  @Test
  public void shouldGetAllRolesWithRights() throws Exception {
    List<Role> roles = new ArrayList<>();
    when(roleRightsService.getAllRoles()).thenReturn(roles);
    OpenLmisResponse response = controller.getAll().getBody();
    assertThat((List<Role>) response.getData().get(ROLES), is(roles));
    verify(roleRightsService).getAllRoles();
  }

  @Test
  public void shouldGetRoleById() throws Exception {
    Role role = new Role();
    int roleId = 1;
    when(roleRightsService.getRole(roleId)).thenReturn(role);

    OpenLmisResponse response = controller.get(roleId).getBody();

    assertThat((Role) response.getData().get(ROLE), is(role));
    verify(roleRightsService).getRole(roleId);
  }

  @Test
  public void shouldUpdateRoleAndRights() throws Exception {
    Role role = new Role(123, "Role Name", "Desc", null, null, asList(CONFIGURE_RNR));

    OpenLmisResponse response = controller.updateRole(role.getId(), role).getBody();

    assertThat(response.getSuccessMsg(), is("Role updated successfully"));
    verify(roleRightsService).updateRole(role);
  }
}
