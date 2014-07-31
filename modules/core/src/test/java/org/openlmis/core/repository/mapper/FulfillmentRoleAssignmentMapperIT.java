/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.UserBuilder;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FulfillmentRoleAssignment;
import org.openlmis.core.domain.Role;
import org.openlmis.core.domain.User;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;
import static org.openlmis.core.domain.RightName.MANAGE_POD;
import static org.openlmis.core.domain.RightName.VIEW_ORDER;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class FulfillmentRoleAssignmentMapperIT {

  @Autowired
  FulfillmentRoleAssignmentMapper fulfillmentRoleAssignmentMapper;

  @Autowired
  QueryExecutor queryExecutor;

  @Autowired
  RoleRightsMapper roleRightsMapper;

  @Autowired
  UserMapper userMapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Test
  public void shouldGetFulfillmentRolesForUser() throws Exception {

    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    User user = make(a(UserBuilder.defaultUser, with(UserBuilder.facilityId, facility.getId())));
    userMapper.insert(user);

    Role role = new Role("r1", "random description");
    roleRightsMapper.insertRole(role);

    queryExecutor.executeUpdate("INSERT INTO fulfillment_role_assignments (userId,facilityId,roleId) values (?,?,?)", user.getId(), facility.getId(), role.getId());

    List<FulfillmentRoleAssignment> expectedFulfillmentRoleAssignments = fulfillmentRoleAssignmentMapper.getFulfillmentRolesForUser(user.getId());

    FulfillmentRoleAssignment fulfillmentRoleAssignment = expectedFulfillmentRoleAssignments.get(0);
    assertThat(fulfillmentRoleAssignment.getUserId(), is(user.getId()));
    assertThat(fulfillmentRoleAssignment.getRoleIds().get(0), is(role.getId()));
    assertThat(fulfillmentRoleAssignment.getFacilityId(), is(facility.getId()));
    assertThat(fulfillmentRoleAssignment.getCreatedBy(), is(user.getCreatedBy()));
    assertThat(fulfillmentRoleAssignment.getModifiedBy(), is(user.getModifiedBy()));
  }


  @Test
  public void shouldDeleteAllFulfillmentRolesForUser() throws Exception {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    User user = make(a(UserBuilder.defaultUser, with(UserBuilder.facilityId, facility.getId())));
    userMapper.insert(user);

    Role role = new Role("r1", "random description");
    roleRightsMapper.insertRole(role);

    queryExecutor.executeUpdate("INSERT INTO fulfillment_role_assignments (userId, facilityId,roleId, " +
      "createdBy, modifiedBy) values (?,?,?, ?, ?)",
      user.getId(), facility.getId(), role.getId(), user.getModifiedBy(), user.getModifiedBy());

    fulfillmentRoleAssignmentMapper.deleteAllFulfillmentRoles(user);

    List<FulfillmentRoleAssignment> expectedFulfillmentRoleAssignments = fulfillmentRoleAssignmentMapper.getFulfillmentRolesForUser(user.getId());

    assertThat(expectedFulfillmentRoleAssignments.size(), is(0));
  }

  @Test
  public void shouldInsertFulfillmentRolesForUser() throws Exception {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    User user = make(a(UserBuilder.defaultUser, with(UserBuilder.facilityId, facility.getId())));
    userMapper.insert(user);

    Role role = new Role("r1", "random description");
    roleRightsMapper.insertRole(role);

    List<Long> roles = asList(role.getId());
    FulfillmentRoleAssignment fulfillmentRoleAssignment = new FulfillmentRoleAssignment(user.getId(), facility.getId(), roles);

    fulfillmentRoleAssignmentMapper.insertFulfillmentRole(user, fulfillmentRoleAssignment.getFacilityId(), fulfillmentRoleAssignment.getRoleIds().get(0));

    List<FulfillmentRoleAssignment> expectedFulfillmentRoleAssignments = fulfillmentRoleAssignmentMapper.getFulfillmentRolesForUser(user.getId());

    assertThat(expectedFulfillmentRoleAssignments.get(0).getUserId(), is(user.getId()));
    assertThat(expectedFulfillmentRoleAssignments.get(0).getRoleIds().get(0), is(role.getId()));
    assertThat(expectedFulfillmentRoleAssignments.get(0).getFacilityId(), is(facility.getId()));
  }

  @Test
  public void shouldGetFulfilmentRolesForUserWithSpecificRight() throws Exception {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    User user = make(a(UserBuilder.defaultUser, with(UserBuilder.facilityId, facility.getId())));
    userMapper.insert(user);

    Role managePODRole = new Role("r1", "random description");
    roleRightsMapper.insertRole(managePODRole);
    roleRightsMapper.createRoleRight(managePODRole, MANAGE_POD);

    Role nonPODRole = new Role("Non POD", "non POD description");
    roleRightsMapper.insertRole(nonPODRole);
    roleRightsMapper.createRoleRight(managePODRole, VIEW_ORDER);

    queryExecutor.executeUpdate("INSERT INTO fulfillment_role_assignments (userId, facilityId,roleId, " +
      "createdBy, modifiedBy) values (?,?,?, ?, ?)",
      user.getId(), facility.getId(), managePODRole.getId(), user.getModifiedBy(), user.getModifiedBy());

    queryExecutor.executeUpdate("INSERT INTO fulfillment_role_assignments (userId, facilityId,roleId, " +
      "createdBy, modifiedBy) values (?,?,?, ?, ?)",
      user.getId(), facility.getId(), nonPODRole.getId(), user.getModifiedBy(), user.getModifiedBy());

    List<FulfillmentRoleAssignment> managePodRoles = fulfillmentRoleAssignmentMapper.getRolesWithRight(user.getId(), MANAGE_POD);

    assertThat(managePodRoles.size(), is(1));
    assertThat(managePodRoles.get(0).getRoleIds(), hasItem(managePODRole.getId()));
    assertThat(managePodRoles.get(0).getFacilityId(), is(facility.getId()));
  }
}
