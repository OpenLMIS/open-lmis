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
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openlmis.allocation.builder.DeliveryZoneBuilder.defaultDeliveryZone;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-allocation.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class DeliveryZoneMapperIT {

  @Autowired
  DeliveryZoneMapper mapper;

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
}
