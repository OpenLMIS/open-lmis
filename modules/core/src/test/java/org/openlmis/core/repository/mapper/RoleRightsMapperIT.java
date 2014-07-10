/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.mapper;

import com.natpryce.makeiteasy.MakeItEasy;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.builder.UserBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Iterables.any;
import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.builder.SupervisoryNodeBuilder.code;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
import static org.openlmis.core.builder.UserBuilder.facilityId;
import static org.openlmis.core.domain.RightName.*;
import static org.openlmis.core.domain.RightType.ADMIN;
import static org.openlmis.core.domain.RightType.REQUISITION;
import static org.openlmis.core.utils.RightUtil.*;

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
  private FulfillmentRoleAssignmentMapper fulfillmentRoleAssignmentMapper;

  @Autowired
  RoleAssignmentMapper roleAssignmentMapper;

  @Autowired
  private FacilityMapper facilityMapper;

  @Autowired
  private SupervisoryNodeMapper supervisoryNodeMapper;

  @Autowired
  private QueryExecutor queryExecutor;

  @Test
  public void shouldGetAllRightsForAUserByUserId() throws Exception {
    Facility facility = insertFacility();
    User user = insertUser(facility);

    List<Right> allRightsForUser = roleRightsMapper.getAllRightsForUserById(user.getId());
    assertThat(allRightsForUser.size(), is(0));

    Program program = insertProgram(make(a(defaultProgram, MakeItEasy.with(programCode, "p1"))));
    Role role = insertRole("r1", "random description");
    Role role2 = insertRole("r2", "fulfillment role");

    insertRoleAssignments(program, user, role);
    insertFulfillmentRoleAssignment(user, facility, role2);

    roleRightsMapper.createRoleRight(role, CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(role, CONFIGURE_RNR);
    roleRightsMapper.createRoleRight(role2, VIEW_ORDER);

    allRightsForUser = roleRightsMapper.getAllRightsForUserById(user.getId());

    assertThat(allRightsForUser.size(), is(3));
    assertTrue(any(allRightsForUser, with(CREATE_REQUISITION)));
    assertTrue(any(allRightsForUser, withType(REQUISITION)));
    assertTrue(any(allRightsForUser, with(CONFIGURE_RNR)));
    assertTrue(any(allRightsForUser, with(VIEW_ORDER)));
  }

  @Test
  public void shouldGetRoleAndRights() throws Exception {
    Role role = new Role("role name", "description", null);
    roleRightsMapper.insertRole(role);

    roleRightsMapper.createRoleRight(role, CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(role, MANAGE_FACILITY);

    Role resultRole = roleRightsMapper.getRole(role.getId());

    assertThat(resultRole.getId(), is(role.getId()));
    assertThat(resultRole.getName(), is(role.getName()));
    assertThat(resultRole.getDescription(), is(role.getDescription()));
    assertTrue(any(resultRole.getRights(), with(CREATE_REQUISITION)));
    assertTrue(any(resultRole.getRights(), with(MANAGE_FACILITY)));
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
    roleRightsMapper.createRoleRight(role, CONFIGURE_RNR);
    roleRightsMapper.createRoleRight(role, CREATE_REQUISITION);

    List<Role> roles = roleRightsMapper.getAllRoles();

    assertThat(roles.get(0).getName(), is("Admin"));
    Role fetchedRole = roles.get(1);
    assertThat(fetchedRole.getName(), is("role name"));
    assertTrue(any(fetchedRole.getRights(), with(CREATE_REQUISITION)));
    assertTrue(any(fetchedRole.getRights(), withDisplayNameKey("right.create.requisition")));
    assertTrue(any(fetchedRole.getRights(), with(CONFIGURE_RNR)));
    assertTrue(any(fetchedRole.getRights(), withDisplayNameKey("right.configure.rnr")));
    assertTrue(any(fetchedRole.getRights(), withType(ADMIN)));
  }

  @Test
  public void shouldUpdateRole() {
    Role role = new Role("Right Name", "Right Desc", null);
    roleRightsMapper.insertRole(role);

    role.setName("Right2");
    Right right = new Right(CREATE_REQUISITION, ADMIN);
    role.setRights(new ArrayList<>(asList(right)));
    role.setDescription("Right Description Changed");
    role.setModifiedBy(222L);

    roleRightsMapper.updateRole(role);

    Role updatedRole = roleRightsMapper.getRole(role.getId());
    assertThat(updatedRole.getName(), is("Right2"));
    assertThat(updatedRole.getDescription(), is("Right Description Changed"));
    assertThat(updatedRole.getModifiedBy(), is(222L));
  }

  @Test
  public void shouldDeleteRights() throws Exception {
    Role role = new Role("Right Name", "Right Desc", null);
    roleRightsMapper.insertRole(role);
    roleRightsMapper.createRoleRight(role, CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(role, UPLOADS);

    assertThat(roleRightsMapper.deleteAllRightsForRole(role.getId()), is(2));
  }

  @Test
  public void shouldGetRightsForAUserOnSupervisoryNodeAndProgram() throws Exception {
    Facility facility = insertFacility();
    User user = insertUser(facility);
    Program program = insertProgram(make(a(defaultProgram, MakeItEasy.with(programCode, "p1"))));
    SupervisoryNode supervisoryNode1 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, MakeItEasy.with(code, "SN1")));
    supervisoryNode1.setFacility(facility);
    supervisoryNodeMapper.insert(supervisoryNode1);

    SupervisoryNode supervisoryNode2 = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode, MakeItEasy.with(code, "SN2")));
    supervisoryNode2.setFacility(facility);
    supervisoryNodeMapper.insert(supervisoryNode2);

    Role role1 = insertRole("r1", "random description");
    roleRightsMapper.createRoleRight(role1, CREATE_REQUISITION);
    Role role2 = insertRole("r2", "random description");
    roleRightsMapper.createRoleRight(role2, CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(role2, AUTHORIZE_REQUISITION);

    roleAssignmentMapper.insertRoleAssignment(user.getId(), program.getId(), supervisoryNode1.getId(), role1.getId());
    roleAssignmentMapper.insertRoleAssignment(user.getId(), program.getId(), supervisoryNode2.getId(), role2.getId());

    List<Right> results = roleRightsMapper.getRightsForUserOnSupervisoryNodeAndProgram(user.getId(), "{" + supervisoryNode1.getId() + ", " + supervisoryNode2.getId() + "}", program);
    assertThat(results.size(), is(2));
    assertTrue(any(results, with(CREATE_REQUISITION)));
    assertTrue(any(results, with(AUTHORIZE_REQUISITION)));
  }

  @Test
  public void shouldGetRightsForAUserOnHomeFacilityAndProgram() throws Exception {
    Facility facility = insertFacility();
    User user = insertUser(facility);
    Program program = insertProgram(make(a(defaultProgram, MakeItEasy.with(programCode, "p1"))));

    Role role1 = insertRole("r1", "random description");
    roleRightsMapper.createRoleRight(role1, CREATE_REQUISITION);
    Role role2 = insertRole("r2", "random description");
    roleRightsMapper.createRoleRight(role2, CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(role2, AUTHORIZE_REQUISITION);

    roleAssignmentMapper.insertRoleAssignment(user.getId(), program.getId(), null, role1.getId());
    roleAssignmentMapper.insertRoleAssignment(user.getId(), program.getId(), null, role2.getId());

    List<Right> results = roleRightsMapper.getRightsForUserOnHomeFacilityAndProgram(user.getId(), program);

    assertThat(results.size(), is(2));
    assertTrue(any(results, with(CREATE_REQUISITION)));
    assertTrue(any(results, with(AUTHORIZE_REQUISITION)));
  }

  @Test
  public void shouldInsertRole() throws Exception {
    Role r1 = new Role("rolename", "description");
    roleRightsMapper.insertRole(r1);

    assertThat(roleRightsMapper.getRole(r1.getId()).getName(), is("rolename"));
  }

  @Test
  public void shouldGetRightTypeForRoleId() throws Exception {
    Role role = new Role("rolename", "description");
    roleRightsMapper.insertRole(role);
    roleRightsMapper.createRoleRight(role, CREATE_REQUISITION);

    RightType rightTypeForRoleId = roleRightsMapper.getRightTypeForRoleId(role.getId());

    ResultSet resultSet = queryExecutor.execute("SELECT * FROM rights WHERE name =?", CREATE_REQUISITION);
    resultSet.next();

    assertThat(rightTypeForRoleId.toString(), is(resultSet.getString("rightType")));
  }

  @Test
  public void shouldGetRightsForUserAndWarehouse() {
    Role role = new Role("WareHouserole", "Warehouse Role");
    roleRightsMapper.insertRole(role);
    roleRightsMapper.createRoleRight(role, MANAGE_POD);
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
    User user = make(a(UserBuilder.defaultUser, MakeItEasy.with(UserBuilder.facilityId, facility.getId())));
    userMapper.insert(user);
    fulfillmentRoleAssignmentMapper.insertFulfillmentRole(user, facility.getId(), role.getId());

    List<Right> rights = roleRightsMapper.getRightsForUserAndWarehouse(user.getId(), facility.getId());

    assertThat(rights.size(), is(1));
    assertTrue(any(rights, with(MANAGE_POD)));
  }

  private Role insertRole(String name, String description) {
    Role r1 = new Role(name, description);
    roleRightsMapper.insertRole(r1);
    return r1;
  }

  private void insertFulfillmentRoleAssignment(User user, Facility facility, Role role) {
    fulfillmentRoleAssignmentMapper.insertFulfillmentRole(user, facility.getId(), role.getId());
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
    User user = make(a(defaultUser, MakeItEasy.with(facilityId, facility.getId())));
    userMapper.insert(user);
    return user;
  }

  private Facility insertFacility() {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);
    return facility;
  }
}