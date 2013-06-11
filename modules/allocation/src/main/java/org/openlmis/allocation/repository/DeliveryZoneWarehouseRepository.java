package org.openlmis.allocation.repository;

import org.openlmis.allocation.domain.DeliveryZoneWarehouse;
import org.openlmis.allocation.repository.mapper.DeliveryZoneWarehouseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DeliveryZoneWarehouseRepository {

  @Autowired
  private DeliveryZoneWarehouseMapper mapper;

  public void insert(DeliveryZoneWarehouse warehouse) {
    mapper.insert(warehouse);
  }

  public void update(DeliveryZoneWarehouse warehouse) {
    mapper.update(warehouse);
  }

  public DeliveryZoneWarehouse getByDeliveryZoneCodeAndWarehouseCode(String deliveryZoneCode, String warehouseCode) {
    return mapper.getByDeliveryZoneCodeAndWarehouseCode(deliveryZoneCode, warehouseCode);
  }
}
