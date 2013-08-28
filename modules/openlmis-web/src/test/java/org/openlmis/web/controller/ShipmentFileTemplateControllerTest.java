/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.shipment.domain.ShipmentFileTemplate;
import org.openlmis.shipment.service.ShipmentFileTemplateService;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ShipmentFileTemplateControllerTest {


  @Mock
  ShipmentFileTemplateService shipmentFileTemplateService;

  @InjectMocks
  ShipmentFileTemplateController controller;

  @Test
  public void shouldGetShipmentFileTemplate() {

    ShipmentFileTemplate expectedShipmentFileTemplate = new ShipmentFileTemplate();
    when(shipmentFileTemplateService.get()).thenReturn(expectedShipmentFileTemplate);

    ResponseEntity<OpenLmisResponse> response = controller.get();

    assertThat((ShipmentFileTemplate) response.getBody().getData().get("shipment_template"), is(expectedShipmentFileTemplate));
    verify(shipmentFileTemplateService).get();
  }

  @Test
  public void shouldUpdateShipmentFileTemplate() {
    ShipmentFileTemplate shipmentFileTemplate = new ShipmentFileTemplate();

    controller.update(shipmentFileTemplate);

    verify(shipmentFileTemplateService).update(shipmentFileTemplate);
  }
}
