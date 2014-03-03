/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.openlmis.core.domain.EDIFileColumn;
import org.openlmis.core.exception.DataException;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 *  DTO for ShipmentLineItem. It represents each record of Budget File received.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentLineItemDTO {

  private static Logger logger = Logger.getLogger(ShipmentLineItemDTO.class);
  private String orderId;
  private String productCode;
  private String quantityShipped;
  private String cost;
  private String packedDate;
  private String shippedDate;

  public void checkMandatoryFields() {
    if (isBlank(this.productCode) ||
      isBlank(this.orderId) ||
      isBlank(this.quantityShipped)) {

      throw new DataException("error.mandatory.fields.missing");
    }
  }

  public static ShipmentLineItemDTO populate(List<String> fieldsInOneRow,
                                             Collection<EDIFileColumn> shipmentFileColumns) {
    ShipmentLineItemDTO dto = new ShipmentLineItemDTO();

    for (EDIFileColumn shipmentFileColumn : shipmentFileColumns) {
      Integer position = shipmentFileColumn.getPosition();
      String name = shipmentFileColumn.getName();
      try {
        Field field = ShipmentLineItemDTO.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(dto, fieldsInOneRow.get(position - 1));
      } catch (Exception e) {
        logger.error("Unable to set field '" + name +
          "' in ShipmentLinetItemDTO, check mapping between DTO and ShipmentFileColumn", e);
      }
    }

    return dto;
  }
}
