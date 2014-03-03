/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.shipment;

import org.openlmis.core.exception.DataException;
import org.openlmis.core.transformer.LineItemTransformer;
import org.openlmis.order.dto.ShipmentLineItemDTO;
import org.openlmis.shipment.domain.ShipmentLineItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * ShipmentLineItemTransformer transforms ShipmentLineItemDTO to ShipmentLineItem.
 */

@Component
public class ShipmentLineItemTransformer extends LineItemTransformer {

  public ShipmentLineItem transform(ShipmentLineItemDTO shipmentLineItemDTO,
                                    String packedDateFormat,
                                    String shippedDateFormat,
                                    Date creationDate) throws DataException {

    shipmentLineItemDTO.checkMandatoryFields();

    ShipmentLineItem lineItem = new ShipmentLineItem();

    try {
      setMandatoryFields(lineItem, shipmentLineItemDTO);
      setOptionalFields(lineItem, shipmentLineItemDTO, packedDateFormat, shippedDateFormat, creationDate);
    } catch (DataException e) {
      throw e;
    } catch (Exception e) {
      throw new DataException("wrong.data.type");
    }

    return lineItem;
  }

  private void setOptionalFields(ShipmentLineItem lineItem,
                                 ShipmentLineItemDTO dto,
                                 String packedDateFormat,
                                 String shippedDateFormat,
                                 Date creationDate) throws ParseException {

    if (!isBlank(dto.getCost())) {
      BigDecimal cost = new BigDecimal(dto.getCost().trim());
      if (cost.compareTo(new BigDecimal(0)) == -1) {
        throw new DataException("error.cost.negative");
      }
      lineItem.setCost(cost);
    }

    Date packedDate = (!isBlank(dto.getPackedDate())) ? parseDate(packedDateFormat,
      dto.getPackedDate().trim()) : creationDate;
    lineItem.setPackedDate(packedDate);

    if (!isBlank(dto.getShippedDate())) {
      lineItem.setShippedDate(parseDate(shippedDateFormat, dto.getShippedDate()));
    }
  }

  private void setMandatoryFields(ShipmentLineItem lineItem, ShipmentLineItemDTO shipmentLineItemDTO) {
    lineItem.setProductCode(shipmentLineItemDTO.getProductCode().trim());
    lineItem.setOrderId(Long.valueOf(shipmentLineItemDTO.getOrderId().trim()));
    String quantityShipped = shipmentLineItemDTO.getQuantityShipped().trim();
    if (quantityShipped.length() > 8) {
      throw new DataException("invalid.quantity.shipped");
    }
    lineItem.setQuantityShipped(Integer.valueOf(quantityShipped));
  }
}
