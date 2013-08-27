package org.openlmis.shipment.repository;

import org.openlmis.shipment.domain.ShipmentConfiguration;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.openlmis.shipment.repository.mapper.ShipmentConfigurationMapper;
import org.openlmis.shipment.repository.mapper.ShipmentFileColumnMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ShipmentTemplateRepository {

  @Autowired
  private ShipmentFileColumnMapper shipmentFileColumnMapper;

  @Autowired
  ShipmentConfigurationMapper shipmentConfigurationMapper;

  public List<ShipmentFileColumn> getAllShipmentFileColumns() {
    return shipmentFileColumnMapper.getAll();
  }

  public ShipmentConfiguration getShipmentConfiguration() {
    return shipmentConfigurationMapper.get();
  }

  public void updateShipmentConfiguration(ShipmentConfiguration shipmentConfiguration) {
    shipmentConfigurationMapper.update(shipmentConfiguration);
  }

  public void deleteAllShipmentFileColumns() {
    shipmentFileColumnMapper.deleteAll();
  }

  public void insertShipmentFileColumn(ShipmentFileColumn shipmentFileColumn) {
    shipmentFileColumnMapper.insert(shipmentFileColumn);
  }
}
