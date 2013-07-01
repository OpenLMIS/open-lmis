/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.apache.commons.collections.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.SupervisoryNodeBuilder;
import org.openlmis.core.domain.*;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static junit.framework.Assert.assertEquals;
import static org.apache.commons.collections.CollectionUtils.exists;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.builder.ProgramBuilder.defaultProgram;
import static org.openlmis.core.builder.ProgramBuilder.programCode;
import static org.openlmis.core.builder.UserBuilder.defaultUser;
import static org.openlmis.core.builder.UserBuilder.facilityId;
import static org.openlmis.core.domain.Right.CONFIGURE_RNR;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;
import static org.openlmis.core.domain.RoleType.ADMIN;
import static org.openlmis.core.domain.RoleType.REQUISITION;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class RoleAssignmentMapperIT {

  @Autowired
  UserMapper userMapper;
  @Autowired
  ProgramMapper programMapper;
  @Autowired
  ProgramSupportedMapper programSupportedMapper;
  @Autowired
  RoleRightsMapper roleRightsMapper;
  @Autowired
  RoleAssignmentMapper mapper;
  @Autowired
  FacilityMapper facilityMapper;
  @Autowired
  SupervisoryNodeMapper supervisoryNodeMapper;

  private User user;
  private Facility facility;

  @Before
  public void setUp() throws Exception {
    facility = insertFacility();
    user = insertUser(facility);
  }

  @Test
  public void shouldReturnProgramAvailableForAFacilityForAUserWithGivenRights() throws Exception {
    Program program1 = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));
    Program program2 = insertProgram(make(a(defaultProgram, with(programCode, "p2"))));

    Role r1 = new Role("r1", REQUISITION, "random description");
    roleRightsMapper.insertRole(r1);

    Role r2 = new Role("r2", REQUISITION, "random description");
    roleRightsMapper.insertRole(r2);

    roleRightsMapper.createRoleRight(r1, CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(r1, CONFIGURE_RNR);
    roleRightsMapper.createRoleRight(r2, CONFIGURE_RNR);

    SupervisoryNode supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    supervisoryNode.setFacility(facility);
    supervisoryNodeMapper.insert(supervisoryNode);

    mapper.insertRoleAssignment(user.getId(), program1.getId(), supervisoryNode.getId(), r1.getId());
    mapper.insertRoleAssignment(user.getId(), program1.getId(), null, r2.getId());
    mapper.insertRoleAssignment(user.getId(), program2.getId(), null, r2.getId());

    List<RoleAssignment> roleAssignments =
      mapper.getRoleAssignmentsWithGivenRightForAUser(CREATE_REQUISITION, user.getId());

    assertEquals(1, roleAssignments.size());
    RoleAssignment expectedRoleAssignment = new RoleAssignment(user.getId(), r1.getId(), program1.getId(), supervisoryNode);
    assertThat(roleAssignments.get(0), is(expectedRoleAssignment));
  }

  @Test
  public void shouldGetSupervisorRolesForAUser() throws Exception {
    Role r1 = new Role("r1", REQUISITION, "random description");
    roleRightsMapper.insertRole(r1);

    Role r2 = new Role("r2", REQUISITION, "random description");
    roleRightsMapper.insertRole(r2);

    SupervisoryNode supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    supervisoryNode.setFacility(facility);
    supervisoryNodeMapper.insert(supervisoryNode);


    mapper.insertRoleAssignment(user.getId(), 1L, supervisoryNode.getId(), r1.getId());
    mapper.insertRoleAssignment(user.getId(), 1L, supervisoryNode.getId(), r2.getId());
    mapper.insertRoleAssignment(user.getId(), 1L, null, r1.getId());

    List<RoleAssignment> roleAssignments = mapper.getSupervisorRoles(user.getId());

    assertThat(roleAssignments.size(), is(1));
    assertThat(roleAssignments.get(0).getRoleIds().size(), is(2));

  }

  @Test
  public void shouldGetHomeFacilityRolesForAUser() throws Exception {
    Role r1 = new Role("r1", REQUISITION, "random description");
    roleRightsMapper.insertRole(r1);

    Role r2 = new Role("r2", REQUISITION, "random description");
    roleRightsMapper.insertRole(r2);

    SupervisoryNode supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    supervisoryNode.setFacility(facility);
    supervisoryNodeMapper.insert(supervisoryNode);


    mapper.insertRoleAssignment(user.getId(), 1L, null, r2.getId());
    mapper.insertRoleAssignment(user.getId(), 1L, null, r1.getId());

    List<RoleAssignment> roleAssignments = mapper.getHomeFacilityRoles(user.getId());

    assertThat(roleAssignments.size(), is(1));
    assertThat(roleAssignments.get(0).getRoleIds().size(), is(2));

  }

  @Test
  public void shouldGetHomeFacilityRolesForAUserOnAGivenProgramWithRights() throws Exception {
    Long programId = 1L;
    Role r1 = new Role("r1", REQUISITION, "random description");
    roleRightsMapper.insertRole(r1);
    Role r2 = new Role("r2", REQUISITION, "random description");
    roleRightsMapper.insertRole(r2);
    roleRightsMapper.createRoleRight(r2, Right.CREATE_REQUISITION);

    SupervisoryNode supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    supervisoryNode.setFacility(facility);
    supervisoryNodeMapper.insert(supervisoryNode);


    mapper.insertRoleAssignment(user.getId(), programId, null, r2.getId());
    mapper.insertRoleAssignment(user.getId(), programId, null, r1.getId());
    mapper.insertRoleAssignment(user.getId(), programId, supervisoryNode.getId(), r2.getId());

    List<RoleAssignment> roleAssignments = mapper.getHomeFacilityRolesForUserOnGivenProgramWithRights(user.getId(), programId, "{CREATE_REQUISITION}");

    assertThat(roleAssignments.size(), is(1));
    assertThat(roleAssignments.get(0).getRoleIds().size(), is(1));
  }

  @Test
  public void shouldDeleteRoleAssignmentsForAUser() throws Exception {
    Long userId = user.getId();
    mapper.insertRoleAssignment(userId, 2L, null, 1L);

    mapper.deleteAllRoleAssignmentsForUser(userId);

    assertThat(mapper.getHomeFacilityRoles(userId).size(), is(0));
    assertThat(mapper.getSupervisorRoles(userId).size(), is(0));
  }

  @Test
  public void shouldGetAdminRolesForUser() throws Exception {
    Long userId = user.getId();

    final Role adminRole = new Role("r1", ADMIN, "admin role");
    roleRightsMapper.insertRole(adminRole);
    Role nonAdminRole = new Role("r2", REQUISITION, "non admin role");
    roleRightsMapper.insertRole(nonAdminRole);

    mapper.insertRoleAssignment(userId, null, null, adminRole.getId());
    mapper.insertRoleAssignment(userId, 2L, null, nonAdminRole.getId());

    RoleAssignment adminRoles = mapper.getAdminRole(userId);

    assertThat(adminRoles.getRoleIds().size(), is(1));

    assertTrue(exists(adminRoles.getRoleIds(), new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        Long roleId = (Long) o;
        return roleId.equals(adminRole.getId());
      }
    }));
  }

  private Program insertProgram(Program program) {
    programMapper.insert(program);
    return program;
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
