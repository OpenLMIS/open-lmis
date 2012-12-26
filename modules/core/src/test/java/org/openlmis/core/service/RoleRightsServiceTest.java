package org.openlmis.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.Role;
import org.openlmis.core.repository.RoleAssignmentRepository;
import org.openlmis.core.repository.RoleRightsRepository;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.domain.Right.CONFIGURE_RNR;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

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

    @Before
    public void setUp() throws Exception {
        role = new Role("role name", "role description");
        roleRightsService = new RoleRightsService(roleRightsRepository, roleAssignmentRepository);
    }


    @Test
    public void shouldGetAllRightsInAlphabeticalOrder() throws Exception {
        List<Right> allRights = new RoleRightsService().getAllRights();
        assertThat(allRights.get(0), is(CONFIGURE_RNR));
    }

    @Test
    public void shouldSaveRole() throws Exception {
        role.setRights(asList(CREATE_REQUISITION));
        roleRightsService.saveRole(role);
        verify(roleRightsRepository).saveRole(role);
    }

    @Test
    public void shouldNotSaveRoleWithoutAnyRights() throws Exception {
        Role role = mock(Role.class);
        doThrow(new RuntimeException("error-message")).when(role).validate();
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("error-message");
        roleRightsService.saveRole(role);
        verify(roleRightsRepository, never()).saveRole(role);
    }

    @Test
    public void shouldReturnAllRoles() throws Exception {
        List<Role> allRoles = new ArrayList<>();
        when(roleRightsRepository.getAllRoles()).thenReturn(allRoles);

        assertThat(roleRightsService.getAllRoles(), is(allRoles));

        verify(roleRightsRepository).getAllRoles();
    }
}
