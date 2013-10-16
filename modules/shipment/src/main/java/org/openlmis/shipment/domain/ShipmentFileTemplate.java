/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.EDIConfiguration;
import org.openlmis.core.domain.EDIFileTemplate;
import org.openlmis.core.exception.DataException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

@Data
@NoArgsConstructor
public class ShipmentFileTemplate extends EDIFileTemplate<ShipmentFileColumn> {

  public ShipmentFileTemplate(EDIConfiguration config, List<ShipmentFileColumn> columns) {
    this.columns = columns;
    this.configuration = config;
  }

  @Override
  public void setColumns(List<ShipmentFileColumn> columns) {
    super.setColumns(columns);
  }

  public void validateAndSetModifiedBy(Long userId) {
    Set<Integer> positions = new HashSet();
    Integer includedColumnCount = 0;
    List<String> mandatoryColumnNames = asList("productCode", "orderId", "quantityShipped");
    configuration.setModifiedBy(userId);
    for (ShipmentFileColumn shipmentFileColumn : columns) {
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
