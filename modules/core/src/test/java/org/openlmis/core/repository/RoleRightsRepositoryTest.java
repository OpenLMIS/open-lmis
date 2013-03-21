package org.openlmis.core.repository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.Role;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.helper.CommaSeparator;
import org.openlmis.core.repository.mapper.RoleRightsMapper;
import org.springframework.dao.DuplicateKeyException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.lang.Boolean.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.openlmis.core.domain.Right.*;

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
    role.setId(1);
    roleRightsRepository.createRole(role);

    verify(roleRightsMapper).insertRole(role);
    verify(roleRightsMapper).createRoleRight(1, CONFIGURE_RNR);
    verify(roleRightsMapper).createRoleRight(1, CREATE_REQUISITION);
  }

  @Test
  public void shouldSaveRoleWithMappingsAndTheirDependentMappings() throws Exception {
    role.setRights(new HashSet<>(asList(CONFIGURE_RNR, CREATE_REQUISITION)));
    role.setId(1);
    roleRightsRepository.createRole(role);

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

    roleRightsRepository.createRole(role);
  }

  @Test
  public void shouldNotUpdateToDuplicateRoleName() {
    Role role = new Role("Name", FALSE, "Desc");
    role.setId(123);
    doThrow(DuplicateKeyException.class).when(roleRightsMapper).updateRole(role);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Duplicate Role found");

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
    int roleId = 1;
    when(roleRightsMapper.getRole(roleId)).thenReturn(role);

    Role fetchedRole = roleRightsRepository.getRole(roleId);

    assertThat(fetchedRole, is(role));
    verify(roleRightsMapper).getRole(roleId);
  }

  @Test
  public void shouldUpdateRole() {
    role.setRights(new HashSet<>(asList(CONFIGURE_RNR)));
    role.setId(100);
    roleRightsRepository.updateRole(role);
    verify(roleRightsMapper).updateRole(role);
    verify(roleRightsMapper).deleteAllRightsForRole(100);
    verify(roleRightsMapper).createRoleRight(100, CONFIGURE_RNR);
  }

  @Test
  public void shouldUpdateRoleAlongWithDependentRights() {
    role.setRights(new HashSet<>(asList(CREATE_REQUISITION)));
    role.setId(100);

    roleRightsRepository.updateRole(role);

    verify(roleRightsMapper).updateRole(role);
    verify(roleRightsMapper).deleteAllRightsForRole(100);
    verify(roleRightsMapper).createRoleRight(100, CREATE_REQUISITION);
    verify(roleRightsMapper).createRoleRight(100, VIEW_REQUISITION);
  }

  @Test
  public void shouldGetRightsForAUserOnSupervisoryNodeAndProgram() throws Exception {
    Integer userId = 1;
    List<SupervisoryNode> supervisoryNodes = asList(new SupervisoryNode(2), new SupervisoryNode(3));
    Program program = new Program(3);
    List<Right> expected = null;
    when(commaSeparator.commaSeparateIds(supervisoryNodes)).thenReturn("{2, 3}");
    when(roleRightsMapper.getRightsForUserOnSupervisoryNodeAndProgram(userId, "{2, 3}", program)).thenReturn(expected);
    List<Right> result = roleRightsRepository.getRightsForUserOnSupervisoryNodeAndProgram(userId, supervisoryNodes, program);
    verify(roleRightsMapper).getRightsForUserOnSupervisoryNodeAndProgram(userId, "{2, 3}", program);
    assertThat(result, is(expected));
  }


  @Test
  public void shouldGetRightsForAUserOnHomeFacilityAndProgram() throws Exception {
    Integer userId = 1;
    Program program = new Program(3);
    List<Right> expected = null;
    when(roleRightsMapper.getRightsForUserOnHomeFacilityAndProgram(userId, program)).thenReturn(expected);
    List<Right> result = roleRightsRepository.getRightsForUserOnHomeFacilityAndProgram(userId, program);
    verify(roleRightsMapper).getRightsForUserOnHomeFacilityAndProgram(userId, program);
    assertThat(result, is(expected));
  }
}
