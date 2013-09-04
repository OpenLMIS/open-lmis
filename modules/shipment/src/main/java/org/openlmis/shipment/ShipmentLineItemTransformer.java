/*
 * CShipment © 2013 VillageReach. All Rights Reserved. This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.shipment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.shipment.domain.ShipmentLineItem;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentLineItemTransformer {

  private String rnrId;
  private String productCode;
  private String quantityShipped;
  private String cost;
  private String packedDate;
  private String shippedDate;

  public ShipmentLineItem transform(String packedDateFormat, String shippedDateFormat) {
    checkMandatory();

    ShipmentLineItem lineItem = new ShipmentLineItem();

    try {
      setMandatoryFields(lineItem);
      setOptionalFields(lineItem, packedDateFormat, shippedDateFormat);
    } catch (Exception e) {
      throw new DataException("wrong.data.type");
    }

    return lineItem;
  }

  private void setOptionalFields(ShipmentLineItem lineItem, String packedDateFormat, String shippedDateFormat) throws ParseException {
    if (!isBlank(cost)) {
      lineItem.setCost(new BigDecimal(cost.trim()));
    }

    if (!isBlank(packedDate)) {
      lineItem.setPackedDate(new SimpleDateFormat(packedDateFormat).parse(packedDate.trim()));
    }

    if (!isBlank(shippedDate)) {
      lineItem.setShippedDate(new SimpleDateFormat(shippedDateFormat).parse(shippedDate.trim()));
    }

  }

  private void setMandatoryFields(ShipmentLineItem lineItem) {
    lineItem.setProductCode(productCode.trim());
    lineItem.setRnrId(Long.valueOf(rnrId.trim()));
    lineItem.setQuantityShipped(Integer.valueOf(quantityShipped.trim()));

  }

  private void checkMandatory() {
    if (isBlank(productCode) || isBlank(rnrId) || isBlank(quantityShipped)) {
      throw new DataException("mandatory.field.missing");
    }
  }
}
