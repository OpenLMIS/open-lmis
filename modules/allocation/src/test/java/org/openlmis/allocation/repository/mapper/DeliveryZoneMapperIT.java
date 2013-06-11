/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.allocation.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.allocation.domain.DeliveryZone;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.openlmis.allocation.builder.DeliveryZoneBuilder.defaultDeliveryZone;
import static org.openlmis.core.domain.Right.PLAN_DISTRIBUTION;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-allocation.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class DeliveryZoneMapperIT {

  @Autowired
  DeliveryZoneMapper mapper;

  @Autowired
  QueryExecutor queryExecutor;

  @Test
  public void shouldInsertDZ() throws Exception {
    DeliveryZone zone = make(a(defaultDeliveryZone));

    mapper.insert(zone);

    DeliveryZone returnedZone = mapper.getById(zone.getId());

    assertThat(returnedZone.getCreatedDate(), is(notNullValue()));
    returnedZone.setCreatedDate(null);
    assertThat(returnedZone, is(zone));
  }

  @Test
  public void shouldUpdateDZ() throws Exception {
    DeliveryZone zone = make(a(defaultDeliveryZone));

    mapper.insert(zone);

    zone.setCode("CleanCode");

    mapper.update(zone);

    DeliveryZone returnedZone = mapper.getById(zone.getId());
    assertThat(returnedZone.getCreatedDate(), is(notNullValue()));
    returnedZone.setCreatedDate(null);

    assertThat(returnedZone, is(zone));
  }

  @Test
  public void shouldGetDZByCode() throws Exception {
    DeliveryZone zone = make(a(defaultDeliveryZone));

    mapper.insert(zone);

    DeliveryZone returnedZone = mapper.getByCode(zone.getCode());

    assertThat(returnedZone.getCreatedDate(), is(notNullValue()));
    returnedZone.setCreatedDate(null);
    assertThat(returnedZone, is(zone));
  }

  @Test
  public void shouldGetAllDeliveryZonesForAUserWithRight() throws Exception {
    String deliveryZoneCode = "DZ1";
    String deliveryZoneName = "Delivery Zone First";
    String planDistributionRole = "FieldCoordinator";
    String user = "Admin123";
    insertDeliveryZone(deliveryZoneCode, deliveryZoneName);
    createRoleAssignment(user, deliveryZoneCode, planDistributionRole);


    List<DeliveryZone> returnedZones = mapper.getByUserForRight(1l, PLAN_DISTRIBUTION);


    assertThat(returnedZones.size(), is(1));
    returnedZones.get(0).setCreatedDate(null);

    DeliveryZone zone = new DeliveryZone();
    zone.setCode(deliveryZoneCode);
    zone.setName(deliveryZoneName);

    assertThat(returnedZones, hasItem(zone));
  }

  private void createRoleAssignment(String user, String deliveryZoneCode, String planDistributionRole) throws SQLException {
    queryExecutor.executeUpdate("INSERT INTO roles (name, adminRole) VALUES (?, 'f');", asList(planDistributionRole));
    queryExecutor.executeUpdate("INSERT INTO role_rights (roleId, rightName) VALUES ((select id from roles where name=?), ?);",
      asList(planDistributionRole, PLAN_DISTRIBUTION.toString()));
    queryExecutor.executeUpdate("INSERT INTO role_assignments (userId, roleId, deliveryZoneId) VALUES ((SELECT id FROM USERS " +
      "WHERE username=?), (SELECT id FROM roles WHERE name = ?), " +
      "(SELECT id FROM delivery_zones WHERE code=?));", asList(user, planDistributionRole, deliveryZoneCode));
  }

  private void insertDeliveryZone(String deliveryZoneCode, String deliveryZoneName) throws SQLException {
    queryExecutor.executeUpdate("INSERT INTO delivery_zones (code, name) values (?,?)", asList(deliveryZoneCode, deliveryZoneName));
  }
}
