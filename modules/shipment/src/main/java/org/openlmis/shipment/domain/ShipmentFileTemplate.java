/*
 * Copyright © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentFileTemplate {

  private ShipmentConfiguration shipmentConfiguration;

  private List<ShipmentFileColumn> shipmentFileColumns;

  public void validateAndSetModifiedBy(Long userId) {
    Set<Integer> positions = new HashSet();
    Integer includedColumnCount = 0;
    List<String> mandatoryColumnNames = asList("productCode", "orderId", "quantityShipped");
    shipmentConfiguration.setModifiedBy(userId);
    for (ShipmentFileColumn shipmentFileColumn : shipmentFileColumns) {
      shipmentFileColumn.validate();
      if (mandatoryColumnNames.contains(shipmentFileColumn.getName()) && !shipmentFileColumn.getInclude()) {
        throw new DataException("shipment.file.mandatory.columns.not.included");
      }
      if (shipmentFileColumn.getInclude()) {
        positions.add(shipmentFileColumn.getPosition());
        includedColumnCount++;
      }
      if (positions.size() != includedColumnCount) {
        throw new DataException("shipment.file.duplicate.position");
      }
      shipmentFileColumn.setModifiedBy(userId);
    }
  }


}
