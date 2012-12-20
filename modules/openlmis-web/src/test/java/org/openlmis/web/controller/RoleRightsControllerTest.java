package org.openlmis.web.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.Role;
import org.openlmis.core.service.RoleRightsService;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER;

@RunWith(MockitoJUnitRunner.class)
public class RoleRightsControllerTest {

    Role role;

    @Mock
    RoleRightsService roleRightsService;

    private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

    RoleRightsController roleRightsController;
    @Before
    public void setUp() throws Exception {
        roleRightsController = new RoleRightsController(roleRightsService);
        MockHttpSession mockHttpSession = new MockHttpSession();
        httpServletRequest.setSession(mockHttpSession);
        mockHttpSession.setAttribute(USER,USER);
        role = new Role("test role","test role description");
    }

    @Test
    public void shouldFetchAllRightsInSystem() throws Exception {
        List<Right> rights = new ArrayList<>();
        when(roleRightsService.getAllRights()).thenReturn(rights);
        List<Right> result = roleRightsController.getAllRights();
        assertThat(result,is(rights));
        verify(roleRightsService).getAllRights();
    }

    @Test
    public void shouldSaveRole() throws Exception {
        ResponseEntity<ModelMap> responseEntity = roleRightsController.saveRole(role, httpServletRequest);
        verify(roleRightsService).saveRole(role);
        assertThat(role.getModifiedBy(), is(USER) );
        ModelMap expectedModelMap = new ModelMap();
        expectedModelMap.put("success", "'test role' created successfully");
        assertThat(responseEntity.getBody(), is(expectedModelMap));
    }

    @Test
    public void shouldGiveErrorIfRoleNotSaved() throws Exception {
        doThrow(new RuntimeException("Error message")).when(roleRightsService).saveRole(role);
        ResponseEntity<ModelMap> responseEntity = roleRightsController.saveRole(role, httpServletRequest);
        verify(roleRightsService).saveRole(role);
        ModelMap expectedModelMap = new ModelMap();
        expectedModelMap.put("error", "Error message");
        assertThat(responseEntity.getBody(), is(expectedModelMap));
    }
}
