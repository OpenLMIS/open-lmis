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
import static org.junit.Assert.assertThat;
import static org.openlmis.core.domain.RightName.MANAGE_DISTRIBUTION;

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
      "VALUES(?,(SELECT id FROM programs WHERE code='VACCINES'), ?)", id, scheduleId);
  }

  private long insertSchedule() throws SQLException {
    return queryExecutor.executeUpdate("INSERT INTO processing_schedules(code, name, description) values(?, ?, ?)",
    "M", "scheduleName", "desc");
  }

  private void createRoleAssignment(long user, String deliveryZoneCode, String planDistributionRole) throws SQLException {
    queryExecutor.executeUpdate("INSERT INTO roles (name) VALUES (?)", planDistributionRole);
    queryExecutor.executeUpdate("INSERT INTO role_rights (roleId, rightName) VALUES ((select id from roles where name=?), ?)",
      planDistributionRole, MANAGE_DISTRIBUTION);
    queryExecutor.executeUpdate("INSERT INTO role_assignments (userId, roleId, deliveryZoneId) " +
      "VALUES (?, (SELECT id FROM roles WHERE name = ?), " +
      "(SELECT id FROM delivery_zones WHERE code=?))",user, planDistributionRole, deliveryZoneCode);
  }

  private long insertUser(String user) throws SQLException {
    return queryExecutor.executeUpdate("INSERT INTO users(username, password, firstname, lastname, email) " +
      "VALUES(?,'password','firstname','lastname', 'email')", user);
  }

  private long insertDeliveryZone(String deliveryZoneCode, String deliveryZoneName) throws SQLException {
    return queryExecutor.executeUpdate("INSERT INTO delivery_zones (code, name) values (?,?)", deliveryZoneCode, deliveryZoneName);
  }
}
