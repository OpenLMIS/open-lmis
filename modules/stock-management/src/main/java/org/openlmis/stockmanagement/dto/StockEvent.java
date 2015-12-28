/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.stockmanagement.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.openlmis.core.domain.StockAdjustmentReason;
import org.openlmis.core.serializer.DateDeserializer;
import org.openlmis.stockmanagement.domain.Lot;

import java.util.Date;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
@JsonIgnoreProperties(ignoreUnknown=true)
public class StockEvent {

  private StockEventType type;
  private Long facilityId;
  private String productCode;

  @JsonDeserialize(using= DateDeserializer.class)
  private Date occurred;
  private Long quantity;
  private Long lotId;
  private Lot lot;
  private String reasonName;
  private String referenceNumber;

  private Map<String, String> customProps;

  public StockEvent() {
    facilityId = null;
    productCode = null;
    occurred = null;
    quantity = null;
    lotId = null;
    reasonName = null;
    referenceNumber = null;
  }

  public long getQuantity() {return Math.abs(quantity);}

  public long getPositiveOrNegativeQuantity(StockAdjustmentReason reason) {
    long q = Math.abs(quantity);
    if (null != reason) {
      q = reason.getAdditive() ? q : q * -1;
    } else if (StockEventType.ISSUE == type) {
      q = q * -1;
    }
    return q;
  }

  /**
   * True if this is a valid event.
   * @return true if valid, false otherwise
   */
  public boolean isValid() {
    return isValidAdjustment() || isValidIssue() || isValidReceipt();
  }

  private boolean isValidProductAndQuantity() {
    return (null != productCode && null != quantity);
  }

  public boolean isValidAdjustment() {
    return isValidProductAndQuantity() &&
            StockEventType.ADJUSTMENT == type &&
            !StringUtils.isBlank(reasonName);
  }

  public boolean isValidIssue() {
    // Need to know what facility it is going to
    return isValidProductAndQuantity() &&
            StockEventType.ISSUE == type &&
            hasFacility();
  }

  public boolean isValidReceipt() {
    // Need to know what facility it is coming from
    return isValidProductAndQuantity() &&
            StockEventType.RECEIPT == type &&
            hasFacility();
  }

  public boolean hasLot() {
    //TODO
    return true;
  }

  private boolean hasFacility() {
    return null != facilityId;
  }
}
