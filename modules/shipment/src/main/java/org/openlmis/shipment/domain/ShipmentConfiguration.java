package org.openlmis.shipment.domain;

import lombok.Data;

@Data
public class ShipmentConfiguration {

  private boolean headerInFile;
  private String packedDatePattern;
  private String shippedDatePattern;

}
