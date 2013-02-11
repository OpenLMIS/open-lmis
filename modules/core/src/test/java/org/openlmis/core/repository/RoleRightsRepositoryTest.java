package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Role;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.RoleRightsMapper;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.domain.Right.CONFIGURE_RNR;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;
import static org.openlmis.core.domain.Right.VIEW_REQUISITION;

@RunWith(MockitoJUnitRunner.class)
public class RoleRightsRepositoryTest {

  Role role;
  @Mock
  RoleRightsMapper roleRightsMapper;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    role = new Role("role name", "role description");

  }

  @Test
  public void shouldSaveRoleWithMappings() throws Exception {
    role.setRights(new HashSet<>(asList(CONFIGURE_RNR, CREATE_REQUISITION)));
    role.setId(1);
    new RoleRightsRepository(roleRightsMapper).createRole(role);

    verify(roleRightsMapper).insertRole(role);
    verify(roleRightsMapper).createRoleRight(1, CONFIGURE_RNR);
    verify(roleRightsMapper).createRoleRight(1, CREATE_REQUISITION);
  }

  @Test
  public void shouldSaveRoleWithMappingsAndTheirDependentMappings() throws Exception {
    role.setRights(new HashSet<>(asList(CONFIGURE_RNR, CREATE_REQUISITION)));
    role.setId(1);
    new RoleRightsRepository(roleRightsMapper).createRole(role);

    verify(roleRightsMapper).insertRole(role);
    verify(roleRightsMapper).createRoleRight(1, CONFIGURE_RNR);
    verify(roleRightsMapper).createRoleRight(1, CREATE_REQUISITION);
    verify(roleRightsMapper, times(1)).createRoleRight(1, VIEW_REQUISITION);
  }

  @Test
  public void shouldNotSaveDuplicateRole() throws Exception {
    doThrow(DuplicateKeyException.class).when(roleRightsMapper).insertRole(role);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Duplicate Role found");

    new RoleRightsRepository(roleRightsMapper).createRole(role);
  }

  @Test
  public void shouldNotUpdateToDuplicateRoleName() {
    Role role = new Role("Name", "Desc");
    role.setId(123);
    doThrow(DuplicateKeyException.class).when(roleRightsMapper).updateRole(role);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Duplicate Role found");

    new RoleRightsRepository(roleRightsMapper).updateRole(role);
  }

  @Test
  public void shouldGetAllRolesInTheSystem() throws Exception {
    List<Role> roles = new ArrayList<>();
    when(roleRightsMapper.getAllRoles()).thenReturn(roles);
    List<Role> allRoles = new RoleRightsRepository(roleRightsMapper).getAllRoles();
    assertThat(allRoles, is(roles));
    verify(roleRightsMapper).getAllRoles();
  }

  @Test
  public void shouldGetRoleById() throws Exception {
    Role role = new Role();
    int roleId = 1;
    when(roleRightsMapper.getRole(roleId)).thenReturn(role);

    Role fetchedRole = new RoleRightsRepository(roleRightsMapper).getRole(roleId);

    assertThat(fetchedRole, is(role));
    verify(roleRightsMapper).getRole(roleId);
  }

  @Test
  public void shouldUpdateRole() {
    role.setRights(new HashSet<>(asList(CONFIGURE_RNR)));
    role.setId(100);
    new RoleRightsRepository(roleRightsMapper).updateRole(role);
    verify(roleRightsMapper).updateRole(role);
    verify(roleRightsMapper).deleteAllRightsForRole(100);
    verify(roleRightsMapper).createRoleRight(100, CONFIGURE_RNR);
  }
}
