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

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.core.exception.DataException;

@NoArgsConstructor
public class ShipmentFileColumn extends EDIFileColumn {

  public ShipmentFileColumn(String name, String dataFieldLabel, Boolean include, Boolean mandatory, Integer position, String datePattern) {
    super(name, dataFieldLabel, include, mandatory, position, datePattern);
  }

  public void validate() {
    if (position == null || position == 0) {
      throw new DataException("shipment.file.invalid.position");
    }
  }
}
