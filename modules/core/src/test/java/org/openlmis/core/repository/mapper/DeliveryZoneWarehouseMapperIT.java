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
