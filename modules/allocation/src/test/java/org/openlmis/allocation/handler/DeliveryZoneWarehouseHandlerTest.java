package org.openlmis.allocation.handler;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.allocation.domain.DeliveryZoneWarehouse;
import org.openlmis.allocation.service.DeliveryZoneWarehouseService;
import org.openlmis.db.categories.UnitTests;

import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DeliveryZoneWarehouseHandlerTest {

  @InjectMocks
  DeliveryZoneWarehouseHandler handler;

  @Mock
  DeliveryZoneWarehouseService service;

  @Test
  public void shouldSaveDeliveryZoneWarehouse() throws Exception {
    DeliveryZoneWarehouse member = new DeliveryZoneWarehouse();
    handler.save(member);
    verify(service).save(member);
  }
}
