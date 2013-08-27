package org.openlmis.shipment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.openlmis.shipment.domain.ShipmentConfiguration;
import org.openlmis.shipment.domain.ShipmentFileColumn;

import java.util.List;

@Data
@AllArgsConstructor
public class ShipmentFileTemplateDTO {

  private ShipmentConfiguration shipmentConfiguration;

  private List<ShipmentFileColumn> shipmentFileColumns;
}
