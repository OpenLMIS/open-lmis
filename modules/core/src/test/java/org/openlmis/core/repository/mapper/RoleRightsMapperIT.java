package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.Right;
import org.openlmis.core.domain.Role;
import org.openlmis.core.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.domain.Right.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class RoleRightsMapperIT {

  @Autowired
  UserMapper userMapper;
  @Autowired
  ProgramMapper programMapper;
  @Autowired
  ProgramSupportedMapper programSupportedMapper;
  @Autowired
  RoleRightsMapper roleRightsMapper;
  @Autowired
  RoleAssignmentMapper roleAssignmentMapper;

  @Test
  public void shouldSetupRightsForAdminRole() {
    List<Right> adminRights = roleRightsMapper.getAllRightsForUser("Admin123");
    assertEquals(CONFIGURE_RNR, adminRights.get(0));
    assertEquals(MANAGE_FACILITY, adminRights.get(1));
    assertEquals(MANAGE_ROLE, adminRights.get(2));
    assertEquals(UPLOADS, adminRights.get(3));
  }

  @Test
  public void shouldGetAllRightsForAUser() throws Exception {
    User user = insertUser();

    List<Right> allRightsForUser = roleRightsMapper.getAllRightsForUser(user.getUserName());
    assertThat(allRightsForUser.size(), is(0));

    Program program = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));
    Role role = insertRole();

    insertRoleAssignments(program, user, role);

    roleRightsMapper.createRoleRight(role.getId(), CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(role.getId(), CONFIGURE_RNR);

    allRightsForUser = roleRightsMapper.getAllRightsForUser(user.getUserName());
    assertThat(allRightsForUser.size(), is(2));
  }

  @Test
  public void shouldGetRoleAndRights() throws Exception {
    Role role = new Role(111, "role name", "description", 123, null, null);
    roleRightsMapper.insertRole(role);

    roleRightsMapper.createRoleRight(role.getId(), CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(role.getId(), MANAGE_FACILITY);

    Role resultRole = roleRightsMapper.getRole(role.getId());

    assertThat(resultRole.getId(), is(not(111)));
    assertThat(resultRole.getId(), is(notNullValue()));
    assertThat(resultRole.getName(), is(role.getName()));
    assertThat(resultRole.getDescription(), is(role.getDescription()));
    assertThat(resultRole.getModifiedBy(), is(role.getModifiedBy()));
    assertTrue(resultRole.getRights().contains(CREATE_REQUISITION));
    assertTrue(resultRole.getRights().contains(MANAGE_FACILITY));
  }

  @Test(expected = DuplicateKeyException.class)
  public void shouldThrowDuplicateKeyExceptionIfDuplicateRoleName() throws Exception {
    String duplicateRoleName = "role name";
    Role role = new Role(duplicateRoleName, "");
    Role role2 = new Role(duplicateRoleName, "any other description");
    roleRightsMapper.insertRole(role);
    roleRightsMapper.insertRole(role2);
  }

  @Test
  public void shouldReturnAllRolesInSystem() throws Exception {
    Role role = new Role("role name", "");
    roleRightsMapper.insertRole(role);
    roleRightsMapper.createRoleRight(role.getId(), CONFIGURE_RNR);
    roleRightsMapper.createRoleRight(role.getId(), CREATE_REQUISITION);

    List<Role> roles = roleRightsMapper.getAllRoles();

    assertThat(roles.get(0).getName(), is("Admin"));
    Role fetchedRole = roles.get(1);
    assertThat(fetchedRole.getName(), is("role name"));
    assertTrue(fetchedRole.getRights().contains(CONFIGURE_RNR));
    assertTrue(fetchedRole.getRights().contains(CREATE_REQUISITION));
  }

  private Role insertRole() {
    Role r1 = new Role("r1", "random description");
    roleRightsMapper.insertRole(r1);
    return r1;
  }

  private Program insertProgram(Program program) {
    programMapper.insert(program);
    return program;
  }

  private Role insertRoleAssignments(Program program, User user, Role role) {
    roleAssignmentMapper.createRoleAssignment(user, role, program, null);
    return role;
  }

  private User insertUser() {
    User user = new User("random123123", "pwd");
    userMapper.insert(user);
    return user;
  }


}
