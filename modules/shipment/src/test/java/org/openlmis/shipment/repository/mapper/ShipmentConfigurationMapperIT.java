/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.repository.mapper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.shipment.domain.ShipmentConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-applicationContext-shipment.xml")
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
@Transactional
public class ShipmentConfigurationMapperIT {


  @Autowired
  ShipmentConfigurationMapper mapper;

  @Test
  public void shouldGetShipmentConfiguration() {
    ShipmentConfiguration shipmentConfiguration = mapper.get();
    assertThat(shipmentConfiguration.isHeaderInFile(), is(false));
  }

  @Test
  public void shouldUpdateShipmentConfiguration() {
    ShipmentConfiguration configuration = mapper.get();

    Date originalModifiedDate = configuration.getModifiedDate();

    configuration.setHeaderInFile(true);
    configuration.setModifiedBy(1L);

    mapper.update(configuration);

    configuration = mapper.get();
    assertThat(configuration.isHeaderInFile(), is(true));
    assertThat(configuration.getModifiedBy(), is(1L));
    assertThat(configuration.getModifiedDate(), is(not(originalModifiedDate)));
  }
}



