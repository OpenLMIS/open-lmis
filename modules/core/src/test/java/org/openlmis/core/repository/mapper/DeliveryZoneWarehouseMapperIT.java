/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.core.repository.mapper;

import com.natpryce.makeiteasy.MakeItEasy;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.openlmis.core.builder.DeliveryZoneBuilder;
import org.openlmis.core.builder.FacilityBuilder;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.domain.DeliveryZoneWarehouse;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.repository.mapper.DeliveryZoneMapper;
import org.openlmis.core.repository.mapper.DeliveryZoneWarehouseMapper;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.db.categories.IntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;

@Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:test-applicationContext-core.xml")
@Transactional
@TransactionConfiguration(defaultRollback = true, transactionManager = "openLmisTransactionManager")
public class DeliveryZoneWarehouseMapperIT {

  @Autowired
  private DeliveryZoneWarehouseMapper mapper;

  @Autowired
  private DeliveryZoneMapper zoneMapper;

  @Autowired
  private FacilityMapper facilityMapper;

  private DeliveryZoneWarehouse deliveryZoneWarehouse;
  private DeliveryZone deliveryZone;
  private Facility warehouse;

  @Before
  public void setUp() throws Exception {
    deliveryZone = make(a(DeliveryZoneBuilder.defaultDeliveryZone));
    zoneMapper.insert(deliveryZone);
    warehouse = make(a(FacilityBuilder.defaultFacility));
    facilityMapper.insert(warehouse);
    deliveryZoneWarehouse = new DeliveryZoneWarehouse(deliveryZone, warehouse);
  }

  @Test
  public void shouldInsertDeliveryZoneWarehouse() throws Exception {
    mapper.insert(deliveryZoneWarehouse);

    DeliveryZoneWarehouse returned = mapper.getByDeliveryZoneCodeAndWarehouseCode(deliveryZone.getCode(), warehouse.getCode());
    Assert.assertThat(returned.getDeliveryZone().getId(), CoreMatchers.is(deliveryZoneWarehouse.getDeliveryZone().getId()));
    Assert.assertThat(returned.getWarehouse().getId(), CoreMatchers.is(deliveryZoneWarehouse.getWarehouse().getId()));
  }

  @Test
  public void shouldUpdateDeliveryZoneWarehouse() throws Exception {
    mapper.insert(deliveryZoneWarehouse);

    Date modifiedDate = new Date();
    deliveryZoneWarehouse.setModifiedDate(modifiedDate);
    mapper.update(deliveryZoneWarehouse);

    DeliveryZoneWarehouse updatedWarehouse = mapper.getByDeliveryZoneCodeAndWarehouseCode(deliveryZone.getCode(), warehouse.getCode());
    Assert.assertThat(updatedWarehouse.getModifiedDate(), CoreMatchers.is(modifiedDate));
  }
}
