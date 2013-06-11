package org.openlmis.allocation.repository;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.allocation.domain.DeliveryZoneWarehouse;
import org.openlmis.allocation.repository.mapper.DeliveryZoneWarehouseMapper;
import org.openlmis.db.categories.UnitTests;

import static org.mockito.Mockito.verify;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class DeliveryZoneWarehouseRepositoryTest {

  @Mock
  private DeliveryZoneWarehouseMapper mapper;

  @InjectMocks
  private DeliveryZoneWarehouseRepository repository;

  @Test
  public void shouldInsertDeliveryZoneWarehouse() throws Exception {
    DeliveryZoneWarehouse warehouse = new DeliveryZoneWarehouse();
    repository.insert(warehouse);
    verify(mapper).insert(warehouse);
  }

  @Test
  public void shouldUpdateDeliveryZoneWarehouse() throws Exception {
    DeliveryZoneWarehouse warehouse = new DeliveryZoneWarehouse();
    repository.update(warehouse);
    verify(mapper).update(warehouse);
  }
}
