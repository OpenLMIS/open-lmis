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
import org.openlmis.core.domain.Role;
import org.openlmis.core.domain.ShipmentRoleAssignment;
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
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.builder.FacilityBuilder.defaultFacility;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class ShipmentRoleAssignmentMapperIT {

  @Autowired
  ShipmentRoleAssignmentMapper shipmentRoleAssignmentMapper;

  @Autowired
  QueryExecutor queryExecutor;

  @Autowired
  RoleRightsMapper roleRightsMapper;

  @Autowired
  UserMapper userMapper;

  @Autowired
  FacilityMapper facilityMapper;

  @Test
  public void shouldGetShipmentRolesForUser() throws Exception {

    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    User user = make(a(UserBuilder.defaultUser, with(UserBuilder.facilityId, facility.getId())));
    userMapper.insert(user);

    Role role = new Role("r1", "random description");
    roleRightsMapper.insertRole(role);

    queryExecutor.executeUpdate("INSERT INTO fulfillment_role_assignments (userId,facilityId,roleId) values (?,?,?)", asList(user.getId(), facility.getId(), role.getId()));

    List<ShipmentRoleAssignment> expectedShipmentRoleAssignments = shipmentRoleAssignmentMapper.getShipmentRolesForUser(user.getId());

    assertThat(expectedShipmentRoleAssignments.get(0).getUserId(), is(user.getId()));
    assertThat(expectedShipmentRoleAssignments.get(0).getRoleIds().get(0), is(role.getId()));
    assertThat(expectedShipmentRoleAssignments.get(0).getFacilityId(), is(facility.getId()));
  }

  @Test
  public void shouldDeleteAllShipmentRolesForUser() throws Exception {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    User user = make(a(UserBuilder.defaultUser, with(UserBuilder.facilityId, facility.getId())));
    userMapper.insert(user);

    Role role = new Role("r1", "random description");
    roleRightsMapper.insertRole(role);

    queryExecutor.executeUpdate("INSERT INTO fulfillment_role_assignments (userId,facilityId,roleId) values (?,?,?)", asList(user.getId(), facility.getId(), role.getId()));

    shipmentRoleAssignmentMapper.deleteAllShipmentRoles(user);

    List<ShipmentRoleAssignment> expectedShipmentRoleAssignments = shipmentRoleAssignmentMapper.getShipmentRolesForUser(user.getId());

    assertThat(expectedShipmentRoleAssignments.size(), is(0));
  }

  @Test
  public void shouldInsertShipmentRolesForUser() throws Exception {
    Facility facility = make(a(defaultFacility));
    facilityMapper.insert(facility);

    User user = make(a(UserBuilder.defaultUser, with(UserBuilder.facilityId, facility.getId())));
    userMapper.insert(user);

    Role role = new Role("r1", "random description");
    roleRightsMapper.insertRole(role);

    List<Long> roles = asList(role.getId());
    ShipmentRoleAssignment shipmentRoleAssignment = new ShipmentRoleAssignment(user.getId(), facility.getId(), roles);

    shipmentRoleAssignmentMapper.insertShipmentRole(user.getId(), shipmentRoleAssignment.getFacilityId(), shipmentRoleAssignment.getRoleIds().get(0));

    List<ShipmentRoleAssignment> expectedShipmentRoleAssignments = shipmentRoleAssignmentMapper.getShipmentRolesForUser(user.getId());

    assertThat(expectedShipmentRoleAssignments.get(0).getUserId(), is(user.getId()));
    assertThat(expectedShipmentRoleAssignments.get(0).getRoleIds().get(0), is(role.getId()));
    assertThat(expectedShipmentRoleAssignments.get(0).getFacilityId(), is(facility.getId()));
  }
}
