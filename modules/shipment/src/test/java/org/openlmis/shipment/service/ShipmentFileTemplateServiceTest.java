package org.openlmis.shipment.service;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.shipment.domain.ShipmentConfiguration;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.openlmis.shipment.domain.ShipmentFileTemplate;
import org.openlmis.shipment.repository.ShipmentTemplateRepository;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@Category(UnitTests.class)
@PrepareForTest(ShipmentFileTemplateService.class)
@RunWith(PowerMockRunner.class)
public class ShipmentFileTemplateServiceTest {

  @Mock
  ShipmentTemplateRepository repository;

  @InjectMocks
  ShipmentFileTemplateService service;


  @Test
  public void shouldUpdateTemplate() {
    List<ShipmentFileColumn> shipmentFileColumns = new ArrayList<ShipmentFileColumn>() {{
      add(new ShipmentFileColumn());
      add(new ShipmentFileColumn());
      add(new ShipmentFileColumn());
    }};
    ShipmentConfiguration shipmentConfiguration = new ShipmentConfiguration();
    ShipmentFileTemplate shipmentFileTemplate = new ShipmentFileTemplate(shipmentConfiguration, shipmentFileColumns);

    service.update(shipmentFileTemplate);
    verify(repository).updateShipmentConfiguration(shipmentConfiguration);
    verify(repository).deleteAllShipmentFileColumns();
    verify(repository, times(3)).insertShipmentFileColumn(any(ShipmentFileColumn.class));
  }

  @Test
  public void shouldGetShipmentTemplate() throws Exception {
    ShipmentConfiguration shipmentConfiguration = new ShipmentConfiguration();
    List<ShipmentFileColumn> shipmentFileColumns = new ArrayList<>();
    when(repository.getShipmentConfiguration()).thenReturn(shipmentConfiguration);
    when(repository.getAllShipmentFileColumns()).thenReturn(shipmentFileColumns);

    ShipmentFileTemplate shipmentFileTemplate = new ShipmentFileTemplate();
    whenNew(ShipmentFileTemplate.class).withArguments(shipmentConfiguration, shipmentFileColumns).thenReturn(shipmentFileTemplate);

    ShipmentFileTemplate shipmentFileTemplateDTO = service.get();

    assertThat(shipmentFileTemplateDTO, is(shipmentFileTemplate));
    verify(repository).getShipmentConfiguration();
    verify(repository).getAllShipmentFileColumns();
  }
}
