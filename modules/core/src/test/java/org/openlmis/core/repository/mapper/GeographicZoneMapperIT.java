/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.domain.GeographicLevel;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-core.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class GeographicZoneMapperIT {

  @Autowired
  private GeographicZoneMapper mapper;

  @Test
  public void shouldSaveGeographicZone() throws Exception {
    GeographicZone geographicZone = new GeographicZone(null, "code", "name", new GeographicLevel(2L,"state", "State", 2), null);
    geographicZone.setCatchmentPopulation(10000L);
    geographicZone.setLongitude(333.9874);
    geographicZone.setLatitude(-256.7249);
    Date date = new Date();
    geographicZone.setModifiedDate(date);

    mapper.insert(geographicZone);

    GeographicZone returnedZone = mapper.getGeographicZoneByCode("code");

    assertThat(returnedZone, is(geographicZone));
  }

  @Test
  public void shouldGetGeographicLevelByCode() throws Exception {
    String code = "state";
    GeographicLevel geographicLevel = mapper.getGeographicLevelByCode(code);
    assertThat(geographicLevel.getName(), is("State"));
    assertThat(geographicLevel.getId(), is(2L));
  }

  @Test
  public void shouldGetNullIfZoneNotPresent() throws Exception {
    GeographicZone nullZone = mapper.getGeographicZoneByCode("some random code");

    assertThat(nullZone, is(nullValue()));
  }

  @Test
  public void shouldGetAllGeographicZonesOfLowestLevelExceptRootGeographicZone() throws Exception {
    List<GeographicZone> allGeographicZones = mapper.getAllGeographicZones();
    assertThat(allGeographicZones.size(), is(10));
    GeographicZone geographicZone = allGeographicZones.get(0);

    assertThat(geographicZone.getCode(), is("Ngorongoro"));
    assertThat(geographicZone.getName(), is("Ngorongoro"));
    assertThat(geographicZone.getLevel().getName(), is("City"));
    assertThat(geographicZone.getLevel().getLevelNumber(), is(4));
  }

  @Test
  public void shouldGetGeographicZoneWithParent() throws Exception {
    GeographicZone parent = new GeographicZone(null, "Dodoma", "Dodoma", new GeographicLevel(null, "district", "District",null), null);
    GeographicZone expectedZone = new GeographicZone(5L, "Ngorongoro", "Ngorongoro", new GeographicLevel(null, "city", "City", null), parent);

    GeographicZone zone = mapper.getGeographicZoneById(5);

    assertThat(zone, is(expectedZone));
  }

  @Test
  public void shouldUpdateGeographicZone() throws Exception {
    GeographicZone geographicZone = new GeographicZone(null, "code", "name", new GeographicLevel(2L,"state", "State", 2), null);
    geographicZone.setLongitude(123.9878);

    mapper.insert(geographicZone);

    geographicZone.setName("new name");
    geographicZone.setLevel(new GeographicLevel(1L,"country", "Country", 1));
    geographicZone.setLongitude(-111.9877);

    mapper.update(geographicZone);

    GeographicZone returnedZone = mapper.getGeographicZoneByCode("code");
    returnedZone.setModifiedDate(null);

    assertThat(returnedZone, is(geographicZone));
  }
}