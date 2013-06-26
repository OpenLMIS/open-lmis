/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
