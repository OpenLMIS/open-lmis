/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.builder.SupervisoryNodeBuilder.code;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
import static org.openlmis.core.builder.UserBuilder.facilityId;
import static org.openlmis.core.domain.Right.*;
import static org.openlmis.core.domain.RoleType.ADMIN;
import static org.openlmis.core.domain.RoleType.REQUISITION;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
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
  @Autowired
  private FacilityMapper facilityMapper;
  @Autowired
  private SupervisoryNodeMapper supervisoryNodeMapper;

  @Test
  public void shouldSetupRightsForAdminRole() {
    Set<Right> adminRights = roleRightsMapper.getAllRightsForUserByUserName("Admin123");
    assertEquals(11, adminRights.size());
    Assert.assertTrue(adminRights.containsAll(asList(CONFIGURE_RNR, MANAGE_FACILITY, MANAGE_ROLE, MANAGE_PROGRAM_PRODUCT, MANAGE_SCHEDULE, UPLOADS, MANAGE_REPORT, VIEW_REPORT, MANAGE_REGIMEN_TEMPLATE, MANAGE_EDI)));
  }

  @Test
  public void shouldGetAllRightsForAUserByUserName() throws Exception {
    Facility facility = insertFacility();
    User user = insertUser(facility);

    Set<Right> allRightsForUser = roleRightsMapper.getAllRightsForUserByUserName(user.getUserName());
    assertThat(allRightsForUser.size(), is(0));

    Program program = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));
    Role role = insertRole("r1", "random description");

    insertRoleAssignments(program, user, role);

    roleRightsMapper.createRoleRight(role, CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(role, CONFIGURE_RNR);

    allRightsForUser = roleRightsMapper.getAllRightsForUserByUserName(user.getUserName());
    assertThat(allRightsForUser.size(), is(2));
  }

  @Test
  public void shouldGetAllRightsForAUserByUserId() throws Exception {
    Facility facility = insertFacility();
    User user = insertUser(facility);

    Set<Right> allRightsForUser = roleRightsMapper.getAllRightsForUserById(user.getId());
    assertThat(allRightsForUser.size(), is(0));

    Program program = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));
    Role role = insertRole("r1", "random description");

    insertRoleAssignments(program, user, role);

    roleRightsMapper.createRoleRight(role, CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(role, CONFIGURE_RNR);

    allRightsForUser = roleRightsMapper.getAllRightsForUserById(user.getId());
    assertThat(allRightsForUser.size(), is(2));
  }

  @Test
  public void shouldGetRoleAndRights() throws Exception {
    Role role = new Role("role name", REQUISITION, "description", null);
    roleRightsMapper.insertRole(role);

    roleRightsMapper.createRoleRight(role, CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(role, MANAGE_FACILITY);

    Role resultRole = roleRightsMapper.getRole(role.getId());

    assertThat(resultRole.getId(), is(not(111L)));
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
    Role role = new Role(duplicateRoleName, REQUISITION, "");
    Role role2 = new Role(duplicateRoleName, REQUISITION, "any other description");
    roleRightsMapper.insertRole(role);
    roleRightsMapper.insertRole(role2);
  }

  @Test
  public void shouldReturnAllRolesInSystem() throws Exception {
    Role role = new Role("role name", REQUISITION, "");
    roleRightsMapper.insertRole(role);
    roleRightsMapper.createRoleRight(role, CONFIGURE_RNR);
    roleRightsMapper.createRoleRight(role, CREATE_REQUISITION);

    List<Role> roles = new ArrayList();
    roles.addAll(roleRightsMapper.getAllRoles());

    assertThat(roles.get(0).getName(), is("Admin"));
    Role fetchedRole = roles.get(1);
    assertThat(fetchedRole.getName(), is("role name"));
    assertTrue(fetchedRole.getRights().contains(CONFIGURE_RNR));
    assertTrue(fetchedRole.getRights().contains(CREATE_REQUISITION));
  }

  @Test
  public void shouldUpdateRole() {
    Role role = new Role("Right Name", REQUISITION, "Right Desc", null);
    roleRightsMapper.insertRole(role);

    role.setName("Right2");
    role.setRights(new HashSet<>(asList(CREATE_REQUISITION)));
    role.setDescription("Right Description Changed");
    role.setModifiedBy(222L);
    role.setType(ADMIN);

    roleRightsMapper.updateRole(role);

    Role updatedRole = roleRightsMapper.getRole(role.getId());
    assertThat(updatedRole.getName(), is("Right2"));
    assertThat(updatedRole.getDescription(), is("Right Description Changed"));
    assertThat(updatedRole.getModifiedBy(), is(222L));
    assertThat(updatedRole.getType(), is(ADMIN));
  }

  @Test
  public void shouldDeleteRights() throws Exception {
    Role role = new Role("Right Name", REQUISITION, "Right Desc", null);
    roleRightsMapper.insertRole(role);
    roleRightsMapper.createRoleRight(role, CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(role, UPLOADS);

    assertThat(roleRightsMapper.deleteAllRightsForRole(role.getId()), is(2));
  }

  @Test
  public void shouldGetRightsForAUserOnSupervisoryNodeAndProgram() throws Exception {
    Facility facility = insertFacility();
    User user = insertUser(facility);
    Program program = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));
    SupervisoryNode supervisoryNode1 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN1")));
    supervisoryNode1.setFacility(facility);
    supervisoryNodeMapper.insert(supervisoryNode1);

    SupervisoryNode supervisoryNode2 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, with(code, "SN2")));
    supervisoryNode2.setFacility(facility);
    supervisoryNodeMapper.insert(supervisoryNode2);


    Role role1 = insertRole("r1", "random description");
    roleRightsMapper.createRoleRight(role1, CREATE_REQUISITION);
    Role role2 = insertRole("r2", "random description");
    roleRightsMapper.createRoleRight(role2, CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(role2, AUTHORIZE_REQUISITION);

    roleAssignmentMapper.insertRoleAssignment(user.getId(), program.getId(), supervisoryNode1.getId(), role1.getId());
    roleAssignmentMapper.insertRoleAssignment(user.getId(), program.getId(), supervisoryNode2.getId(), role2.getId());

    List<Right> result = roleRightsMapper.getRightsForUserOnSupervisoryNodeAndProgram(user.getId(), "{" + supervisoryNode1.getId() + ", " + supervisoryNode2.getId() + "}", program);
    assertThat(result.size(), is(2));
    assertTrue(result.contains(CREATE_REQUISITION));
    assertTrue(result.contains(AUTHORIZE_REQUISITION));
  }

  @Test
  public void shouldGetRightsForAUserOnHomeFacilityAndProgram() throws Exception {
    Facility facility = insertFacility();
    User user = insertUser(facility);
    Program program = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));

    Role role1 = insertRole("r1", "random description");
    roleRightsMapper.createRoleRight(role1, CREATE_REQUISITION);
    Role role2 = insertRole("r2", "random description");
    roleRightsMapper.createRoleRight(role2, CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(role2, AUTHORIZE_REQUISITION);

    roleAssignmentMapper.insertRoleAssignment(user.getId(), program.getId(), null, role1.getId());
    roleAssignmentMapper.insertRoleAssignment(user.getId(), program.getId(), null, role2.getId());

    List<Right> result = roleRightsMapper.getRightsForUserOnHomeFacilityAndProgram(user.getId(), program);
    assertThat(result.size(), is(2));
    assertTrue(result.contains(CREATE_REQUISITION));
    assertTrue(result.contains(AUTHORIZE_REQUISITION));
  }

  @Test
  public void shouldInsertRole() throws Exception {
    Role r1 = new Role("rolename", REQUISITION, "description");
    roleRightsMapper.insertRole(r1);

    assertThat(roleRightsMapper.getRole(r1.getId()).getName(), is("rolename"));
  }

  private Role insertRole(String name, String description) {
    Role r1 = new Role(name, REQUISITION, description);
    roleRightsMapper.insertRole(r1);
    return r1;
  }

  private Program insertProgram(Program program) {
    programMapper.insert(program);
    return program;
  }

  private Role insertRoleAssignments(Program program, User user, Role role) {
    roleAssignmentMapper.insertRoleAssignment(user.getId(), program.getId(), null, role.getId());
    return role;
  }

  private User insertUser(Facility facility) {
    User user = make(a(defaultUser, with(facilityId, facility.getId())));
    userMapper.insert(user);
    return user;
  }

  private Facility insertFacility() {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
    return facility;
  }
}
