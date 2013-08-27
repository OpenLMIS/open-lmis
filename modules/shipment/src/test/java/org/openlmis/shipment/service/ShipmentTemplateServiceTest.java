package org.openlmis.shipment.service;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openlmis.shipment.domain.ShipmentConfiguration;
import org.openlmis.shipment.domain.ShipmentFileColumn;
import org.openlmis.shipment.dto.ShipmentFileTemplateDTO;
import org.openlmis.shipment.repository.ShipmentTemplateRepository;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ShipmentTemplateServiceTest {

  @Mock
  ShipmentTemplateRepository repository;

  @InjectMocks
  ShipmentTemplateService service;


  @Test
  public void shouldUpdateTemplate() {
    List<ShipmentFileColumn> shipmentFileColumns = new ArrayList<ShipmentFileColumn>() {{
      add(new ShipmentFileColumn());
      add(new ShipmentFileColumn());
      add(new ShipmentFileColumn());
    }};
    ShipmentConfiguration shipmentConfiguration = new ShipmentConfiguration();
    ShipmentFileTemplateDTO shipmentFileTemplateDTO = new ShipmentFileTemplateDTO(shipmentConfiguration, shipmentFileColumns);

    service.update(shipmentFileTemplateDTO);
    verify(repository).updateShipmentConfiguration(shipmentConfiguration);
    verify(repository).deleteAllShipmentFileColumns();
    verify(repository, times(3)).insertShipmentFileColumn(any(ShipmentFileColumn.class));
  }

}
