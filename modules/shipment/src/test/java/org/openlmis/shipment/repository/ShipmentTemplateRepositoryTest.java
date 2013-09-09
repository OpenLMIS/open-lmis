package org.openlmis.shipment.repository;


import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.shipment.domain.ShipmentConfiguration;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.openlmis.shipment.repository.mapper.ShipmentConfigurationMapper;
import org.openlmis.shipment.repository.mapper.ShipmentFileColumnMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class ShipmentTemplateRepositoryTest {

  @Mock
  ShipmentFileColumnMapper shipmentFileColumnMapper;

  @Mock
  ShipmentConfigurationMapper shipmentConfigurationMapper;

  @InjectMocks
  ShipmentTemplateRepository repository;

  @Test
  public void shouldGetAllShipmentFileColumns() {
    List<ShipmentFileColumn> expectedShipmentColumns = new ArrayList<>();
    when(shipmentFileColumnMapper.getAll()).thenReturn(expectedShipmentColumns);
    List<ShipmentFileColumn> shipmentFileColumns = repository.getAllShipmentFileColumns();
    assertThat(shipmentFileColumns, is(expectedShipmentColumns));
    verify(shipmentFileColumnMapper).getAll();
  }

  @Test
  public void shouldGetShipmentConfiguration() {
    ShipmentConfiguration expectedShipmentConfiguration = new ShipmentConfiguration();
    when(shipmentConfigurationMapper.get()).thenReturn(expectedShipmentConfiguration);

    ShipmentConfiguration shipmentConfiguration = repository.getShipmentConfiguration();

    assertThat(shipmentConfiguration, is(expectedShipmentConfiguration));
    verify(shipmentConfigurationMapper).get();
  }

  @Test
  public void shouldUpdateShipmentConfiguration() {
    ShipmentConfiguration shipmentConfiguration = new ShipmentConfiguration();

    repository.updateShipmentConfiguration(shipmentConfiguration);

    verify(shipmentConfigurationMapper).update(shipmentConfiguration);
  }


  @Test
  public void shouldDeleteAllShipmentFileColumns() {

    repository.deleteAllShipmentFileColumns();

    verify(shipmentFileColumnMapper).deleteAll();
    assertThat(repository.getAllShipmentFileColumns().size(), is(0));
  }

  @Test
  public void shouldInsertShipmentFileColumn() {
    ShipmentFileColumn shipmentFileColumn = new ShipmentFileColumn();

    repository.insertShipmentFileColumn(shipmentFileColumn);

    verify(shipmentFileColumnMapper).insert(shipmentFileColumn);
  }
}
