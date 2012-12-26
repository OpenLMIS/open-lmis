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
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;

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
    List<Right> result = controller.getAllRights();
    assertThat(result, is(rights));
    verify(roleRightsService).getAllRights();
  }

  @Test
  public void shouldSaveRole() throws Exception {
    ResponseEntity<ModelMap> responseEntity = controller.saveRole(role, httpServletRequest);
    verify(roleRightsService).saveRole(role);
    assertThat(role.getModifiedBy(), is(LOGGED_IN_USERID));
    ModelMap expectedModelMap = new ModelMap();
    expectedModelMap.put("success", "'test role' created successfully");
    assertThat(responseEntity.getBody(), is(expectedModelMap));
  }

  @Test
  public void shouldGiveErrorIfRoleNotSaved() throws Exception {
    doThrow(new RuntimeException("Error message")).when(roleRightsService).saveRole(role);
    ResponseEntity<ModelMap> responseEntity = controller.saveRole(role, httpServletRequest);
    verify(roleRightsService).saveRole(role);
    ModelMap expectedModelMap = new ModelMap();
    expectedModelMap.put("error", "Error message");
    assertThat(responseEntity.getBody(), is(expectedModelMap));
  }

  @Test
  public void shouldGetAllRolesWithRights() throws Exception {
    List<Role> roles = new ArrayList<>();
    when(roleRightsService.getAllRoles()).thenReturn(roles);
    OpenLmisResponse response = controller.getAll();
    assertThat((List<Role>) response.getResponseData(), is(roles));
    verify(roleRightsService).getAllRoles();
  }

  @Test
  public void shouldGetRoleById() throws Exception {
    Role role = new Role();
    int roleId = 1;
    when(roleRightsService.getRole(roleId)).thenReturn(role);

    OpenLmisResponse response = controller.get(roleId);

    assertThat((Role) response.getResponseData(), is(role));
    verify(roleRightsService).getRole(roleId);
  }
}
