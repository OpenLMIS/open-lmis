/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.matchers.JUnitMatchers;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.DeliveryZoneBuilder;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.Program;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;
import static org.openlmis.core.domain.Right.MANAGE_DISTRIBUTION;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class DeliveryZoneMapperIT {

  @Autowired
  DeliveryZoneMapper mapper;

  @Autowired
  QueryExecutor queryExecutor;

  @Test
  public void shouldInsertDZ() throws Exception {
    DeliveryZone zone = make(a(DeliveryZoneBuilder.defaultDeliveryZone));

    mapper.insert(zone);

    DeliveryZone returnedZone = mapper.getById(zone.getId());

    assertThat(returnedZone.getCreatedDate(), CoreMatchers.is(CoreMatchers.notNullValue()));
    returnedZone.setCreatedDate(null);
    assertThat(returnedZone, CoreMatchers.is(zone));
  }

  @Test
  public void shouldUpdateDZ() throws Exception {
    DeliveryZone zone = make(a(DeliveryZoneBuilder.defaultDeliveryZone));

    mapper.insert(zone);

    zone.setCode("CleanCode");

    mapper.update(zone);

    DeliveryZone returnedZone = mapper.getById(zone.getId());
    assertThat(returnedZone.getCreatedDate(), CoreMatchers.is(CoreMatchers.notNullValue()));
    returnedZone.setCreatedDate(null);

    assertThat(returnedZone, CoreMatchers.is(zone));
  }

  @Test
  public void shouldGetDZByCode() throws Exception {
    DeliveryZone zone = make(a(DeliveryZoneBuilder.defaultDeliveryZone));

    mapper.insert(zone);

    DeliveryZone returnedZone = mapper.getByCode(zone.getCode());

    assertThat(returnedZone.getCreatedDate(), CoreMatchers.is(CoreMatchers.notNullValue()));
    returnedZone.setCreatedDate(null);
    assertThat(returnedZone, CoreMatchers.is(zone));
  }

  @Test
  public void shouldNotGetDeliveryZonesForAUserWithRight() throws Exception {
    String deliveryZoneCode = "DZ1";
    String deliveryZoneName = "Delivery Zone First";
    String planDistributionRole = "FieldCoordinator";
    String user = "user";
    long userId = insertUser(user);
    insertDeliveryZone(deliveryZoneCode, deliveryZoneName);
    createRoleAssignment(userId, deliveryZoneCode, planDistributionRole);


    List<DeliveryZone> returnedZones = mapper.getByUserForRight(userId, MANAGE_DISTRIBUTION);


    assertThat(returnedZones.size(), CoreMatchers.is(1));
    returnedZones.get(0).setCreatedDate(null);

    DeliveryZone zone = new DeliveryZone();
    zone.setCode(deliveryZoneCode);
    zone.setName(deliveryZoneName);

    assertThat(returnedZones, JUnitMatchers.hasItem(zone));
  }

  @Test
  public void shouldNotGetDeliveryZonesForAUserIfNoneIsMapped() throws Exception {
    String deliveryZoneCode = "DZ1";
    String deliveryZoneName = "Delivery Zone First";
    String planDistributionRole = "FieldCoordinator";
    String user = "user";
    long userId = insertUser(user);
    insertDeliveryZone(deliveryZoneCode, deliveryZoneName);
    createRoleAssignment(userId, deliveryZoneCode, planDistributionRole);


    List<DeliveryZone> returnedZones = mapper.getByUserForRight(1l, MANAGE_DISTRIBUTION);


    assertThat(returnedZones.size(), CoreMatchers.is(0));
  }

  @Test
  public void shouldGetProgramsForDeliveryZone() throws Exception {
    long deliveryZoneId = insertDeliveryZone("deliveryZoneCode", "DZ name");
    long scheduleId = insertSchedule();
    insertDeliveryZoneProgramSchedule(deliveryZoneId, scheduleId);

    List<Program> programs = mapper.getPrograms(deliveryZoneId);

    assertThat(programs.size(), CoreMatchers.is(1));
    assertThat(programs.get(0).getId(), CoreMatchers.is(getProgramId()));
  }

  private Long getProgramId() throws SQLException {
    try (ResultSet resultSet = queryExecutor.execute("SELECT id FROM programs WHERE code = 'VACCINES'")) {
      resultSet.next();
      return resultSet.getLong(1);
    }
  }

  private void insertDeliveryZoneProgramSchedule(long id, long scheduleId) throws SQLException {
    queryExecutor.executeUpdate("INSERT INTO delivery_zone_program_schedules(deliveryZoneId, programId, scheduleId ) " +
      "VALUES(?,(SELECT id FROM programs WHERE code='VACCINES'), ?)", asList(id, scheduleId));
  }

  private long insertSchedule() throws SQLException {
    return queryExecutor.executeUpdate("INSERT INTO processing_schedules(code, name, description) values(?, ?, ?)",
      asList("M", "scheduleName", "desc"));
  }

  private void createRoleAssignment(long user, String deliveryZoneCode, String planDistributionRole) throws SQLException {
    queryExecutor.executeUpdate("INSERT INTO roles (name, type) VALUES (?, 'ALLOCATION')", asList(planDistributionRole));
    queryExecutor.executeUpdate("INSERT INTO role_rights (roleId, rightName) VALUES ((select id from roles where name=?), ?)",
      asList(planDistributionRole, MANAGE_DISTRIBUTION.toString()));
    queryExecutor.executeUpdate("INSERT INTO role_assignments (userId, roleId, deliveryZoneId) " +
      "VALUES (?, (SELECT id FROM roles WHERE name = ?), " +
      "(SELECT id FROM delivery_zones WHERE code=?))", asList(user, planDistributionRole, deliveryZoneCode));
  }

  private long insertUser(String user) throws SQLException {
    return queryExecutor.executeUpdate("INSERT INTO users(username, password, firstname, lastname, email) " +
      "VALUES(?,'password','firstname','lastname', 'email')", asList(user));
  }

  private long insertDeliveryZone(String deliveryZoneCode, String deliveryZoneName) throws SQLException {
    return queryExecutor.executeUpdate("INSERT INTO delivery_zones (code, name) values (?,?)", asList(deliveryZoneCode, deliveryZoneName));
  }
}
