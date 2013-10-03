/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.upload;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.DeliveryZone;
import org.openlmis.core.service.DeliveryZoneService;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.upload.DeliveryZoneHandler;
import org.openlmis.db.categories.UnitTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DeliveryZoneHandlerTest {

  @Mock
  DeliveryZoneService service;

  @InjectMocks
  DeliveryZoneHandler handler;

  @Test
  public void shouldSaveDeliveryZone() throws Exception {
    DeliveryZone deliveryZone = new DeliveryZone();

    handler.save(deliveryZone);

    verify(service).save(deliveryZone);
  }

  @Test
  public void shouldGetDeliveryZoneByCode() throws Exception {
    DeliveryZone expectedZone = new DeliveryZone();
    expectedZone.setCode("code");

    DeliveryZone existingZone = new DeliveryZone();
    existingZone.setCode("existing code");

    when(service.getByCode(existingZone.getCode())).thenReturn(expectedZone);

    BaseModel returnedZone = handler.getExisting(existingZone);

    assertThat((DeliveryZone) returnedZone, is(expectedZone));
  }
}
