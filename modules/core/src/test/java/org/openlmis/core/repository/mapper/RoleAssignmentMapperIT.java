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

import org.apache.commons.collections.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.DeliveryZoneBuilder;
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
import static org.openlmis.core.domain.RightName.*;

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
  @Autowired
  private DeliveryZoneMapper deliverZoneMapper;

  private User user;
  private Facility facility;

  @Before
  public void setUp() throws Exception {
    facility = insertFacility();
    user = insertUser(facility);
  }

  @Test
  public void shouldReturnRoleAssignmentsForUserAndRight() {
    Program program1 = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));
    Program program2 = insertProgram(make(a(defaultProgram, with(programCode, "p2"))));

    Role r1 = new Role("r1", "random description");
    roleRightsMapper.insertRole(r1);

    Role r2 = new Role("r2", "random description");
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
    assertRoleAssignment(roleAssignments, new RoleAssignment(user.getId(), r1.getId(), program1.getId(), supervisoryNode));
  }

  private void assertRoleAssignment(List<RoleAssignment> actualRoleAssignments, final RoleAssignment expectedRoleAssignment) {
    assertTrue(exists(actualRoleAssignments, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        RoleAssignment roleAssignment = (RoleAssignment) o;
        return roleAssignment.getProgramId().equals(expectedRoleAssignment.getProgramId()) &&
          roleAssignment.getSupervisoryNode().equals(expectedRoleAssignment.getSupervisoryNode());
      }
    }));
  }

  @Test
  public void shouldNotGetTheSameRoleAssignmentForMultipleRolesWithSameRights() {
    Program program1 = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));

    Role r1 = new Role("r1", "random description");
    roleRightsMapper.insertRole(r1);

    Role r2 = new Role("r2", "random description");
    roleRightsMapper.insertRole(r2);

    roleRightsMapper.createRoleRight(r1, CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(r1, APPROVE_REQUISITION);
    roleRightsMapper.createRoleRight(r2, APPROVE_REQUISITION);

    SupervisoryNode supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    supervisoryNode.setFacility(facility);
    supervisoryNodeMapper.insert(supervisoryNode);

    mapper.insertRoleAssignment(user.getId(), program1.getId(), supervisoryNode.getId(), r1.getId());
    mapper.insertRoleAssignment(user.getId(), program1.getId(), supervisoryNode.getId(), r2.getId());

    List<RoleAssignment> roleAssignments =
      mapper.getRoleAssignmentsWithGivenRightForAUser(APPROVE_REQUISITION, user.getId());

    assertEquals(1, roleAssignments.size());
    assertRoleAssignment(roleAssignments, new RoleAssignment(user.getId(), r1.getId(), program1.getId(), supervisoryNode));
  }

  @Test
  public void shouldGetRoleAssignmentsForMultipleRoles() {
    final Program program1 = insertProgram(make(a(defaultProgram, with(programCode, "p1"))));
    Program program2 = insertProgram(make(a(defaultProgram, with(programCode, "p2"))));

    Role r1 = new Role("r1", "random description");
    roleRightsMapper.insertRole(r1);

    Role r2 = new Role("r2", "random description");
    roleRightsMapper.insertRole(r2);

    roleRightsMapper.createRoleRight(r1, CREATE_REQUISITION);
    roleRightsMapper.createRoleRight(r1, APPROVE_REQUISITION);
    roleRightsMapper.createRoleRight(r2, APPROVE_REQUISITION);

    final SupervisoryNode supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    supervisoryNode.setFacility(facility);
    supervisoryNodeMapper.insert(supervisoryNode);

    mapper.insertRoleAssignment(user.getId(), program1.getId(), supervisoryNode.getId(), r1.getId());
    mapper.insertRoleAssignment(user.getId(), program1.getId(), supervisoryNode.getId(), r2.getId());
    mapper.insertRoleAssignment(user.getId(), program2.getId(), supervisoryNode.getId(), r2.getId());

    List<RoleAssignment> roleAssignments =
      mapper.getRoleAssignmentsWithGivenRightForAUser(APPROVE_REQUISITION, user.getId());

    assertEquals(2, roleAssignments.size());
    assertRoleAssignment(roleAssignments, new RoleAssignment(user.getId(), r1.getId(), program1.getId(), supervisoryNode));
    assertRoleAssignment(roleAssignments, new RoleAssignment(user.getId(), r1.getId(), program2.getId(), supervisoryNode));

  }

  @Test
  public void shouldGetSupervisorRolesForAUser() throws Exception {
    Role r1 = new Role("r1", "random description");
    roleRightsMapper.insertRole(r1);

    Role r2 = new Role("r2", "random description");
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
    Role r1 = new Role("r1", "random description");
    roleRightsMapper.insertRole(r1);

    Role r2 = new Role("r2", "random description");
    roleRightsMapper.insertRole(r2);

    SupervisoryNode supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    supervisoryNode.setFacility(facility);
    supervisoryNodeMapper.insert(supervisoryNode);

    DeliveryZone deliveryZone = make(a(DeliveryZoneBuilder.defaultDeliveryZone));
    deliverZoneMapper.insert(deliveryZone);


    mapper.insert(user.getId(), 1L, null, null, r2.getId());
    mapper.insert(user.getId(), 2L, null, deliveryZone, r1.getId());

    List<RoleAssignment> roleAssignments = mapper.getHomeFacilityRoles(user.getId());

    assertThat(roleAssignments.size(), is(1));
    assertThat(roleAssignments.get(0).getRoleIds().size(), is(1));

  }

  @Test
  public void shouldGetHomeFacilityRolesForAUserOnAGivenProgramWithRights() throws Exception {
    Long programId = 1L;
    Role r1 = new Role("r1", "random description");
    roleRightsMapper.insertRole(r1);
    Role r2 = new Role("r2", "random description");
    roleRightsMapper.insertRole(r2);
    roleRightsMapper.createRoleRight(r2, CREATE_REQUISITION);

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

    final Role adminRole = new Role("r1", "admin role");
    roleRightsMapper.insertRole(adminRole);
    roleRightsMapper.createRoleRight(adminRole, MANAGE_FACILITY);
    Role nonAdminRole = new Role("r2", "non admin role");
    roleRightsMapper.insertRole(nonAdminRole);
    roleRightsMapper.createRoleRight(adminRole, CREATE_REQUISITION);

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

  @Test
  public void shouldInsertRolesForUser() throws Exception {
    Long programId = 1L;

    SupervisoryNode supervisoryNode = make(a(SupervisoryNodeBuilder.defaultSupervisoryNode));
    supervisoryNode.setFacility(facility);
    supervisoryNodeMapper.insert(supervisoryNode);

    DeliveryZone deliveryZone = make(a(DeliveryZoneBuilder.defaultDeliveryZone));
    deliverZoneMapper.insert(deliveryZone);

    Role adminRole = new Role("r1", "admin role");
    roleRightsMapper.insertRole(adminRole);
    roleRightsMapper.createRoleRight(adminRole, MANAGE_FACILITY);
    Role nonAdminRole = new Role("r2", "non admin role");
    roleRightsMapper.insertRole(nonAdminRole);
    roleRightsMapper.createRoleRight(nonAdminRole, CREATE_REQUISITION);

    mapper.insert(user.getId(), programId, supervisoryNode, null, nonAdminRole.getId());
    mapper.insert(user.getId(), null, null, null, adminRole.getId());

    RoleAssignment adminRoleAssignment = mapper.getAdminRole(user.getId());
    List<RoleAssignment> supervisoryRoles = mapper.getSupervisorRoles(user.getId());

    assertThat(adminRoleAssignment.getRoleIds().get(0), is(adminRole.getId()));
    assertThat(supervisoryRoles.get(0).getRoleIds().get(0), is(nonAdminRole.getId()));
  }

  @Test
  public void shouldGetAllocationRolesForUser() throws Exception {
    Long userId = user.getId();

    final Role allocationRole = new Role("r1", "allocation role");
    roleRightsMapper.insertRole(allocationRole);
    roleRightsMapper.createRoleRight(allocationRole, MANAGE_DISTRIBUTION);
    Role adminRole = new Role("r2", "non admin role");
    roleRightsMapper.insertRole(adminRole);
    roleRightsMapper.createRoleRight(allocationRole, CREATE_REQUISITION);
    DeliveryZone deliveryZone = make(a(DeliveryZoneBuilder.defaultDeliveryZone));
    deliverZoneMapper.insert(deliveryZone);

    mapper.insert(userId, 1L, null, deliveryZone, allocationRole.getId());
    mapper.insert(userId, null, null, null, adminRole.getId());

    List<RoleAssignment> allocationRoles = mapper.getAllocationRoles(userId);

    assertThat(allocationRoles.size(), is(1));
    assertThat(allocationRoles.get(0).getRoleIds().size(), is(1));
    assertThat(allocationRoles.get(0).getRoleIds().get(0), is(allocationRole.getId()));
  }

  @Test
  public void shouldGetReportingRolesForUser(){
    Long userId = user.getId();
    Role reportingRole = new Role("r1", "reporting role");
    roleRightsMapper.insertRole(reportingRole);
    roleRightsMapper.createRoleRight(reportingRole, MANAGE_REPORT);

    Role nonReportingRole = new Role("r2", "non reporting role");
    roleRightsMapper.insertRole(nonReportingRole);
    roleRightsMapper.createRoleRight(nonReportingRole, CREATE_REQUISITION);
    mapper.insertRoleAssignment(userId, null, null, reportingRole.getId());
    mapper.insertRoleAssignment(userId, 2L, null, nonReportingRole.getId());

    RoleAssignment role = mapper.getReportingRole(userId);

    assertThat(role.getRoleIds().size(), is(1));
    assertThat(role.getRoleIds().get(0), is(reportingRole.getId()));
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
