/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.rnr.dto;


import lombok.Data;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openlmis.rnr.domain.RnrLineItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

import static java.lang.reflect.Modifier.isStatic;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@Data
@JsonSerialize(include = NON_EMPTY)
public class RnrLineItemDTO {

  String productCode;
  Integer beginningBalance;
  Integer quantityReceived;
  Integer quantityDispensed;
  Integer totalLossesAndAdjustments;
  Integer stockInHand;
  Integer newPatientCount;
  Integer stockOutDays;
  Integer quantityRequested;
  String reasonForRequestedQuantity;
  Integer calculatedOrderQuantity;
  Integer quantityApproved;
  String remarks;

  private static Logger logger = LoggerFactory.getLogger(RnrLineItemDTO.class);

  public RnrLineItemDTO(RnrLineItem lineItem) {
    for (Field field : this.getClass().getDeclaredFields()) {
      if (isStatic(field.getModifiers())) continue;

      try {
        Field lineItemField = RnrLineItem.class.getDeclaredField(field.getName());
        lineItemField.setAccessible(true);
        field.set(this, lineItemField.get(lineItem));
      } catch (Exception e) {
        logger.error("Failed to set RnrLineItemDTO field for name: " + field.getName());
      }
    }
  }

}
