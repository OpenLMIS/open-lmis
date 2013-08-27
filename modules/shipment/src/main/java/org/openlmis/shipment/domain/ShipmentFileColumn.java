package org.openlmis.shipment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentFileColumn {

  private String dataFieldLabel;
  private int position;
  private Boolean includedInShipmentFile;
  private Boolean mandatory;

}
