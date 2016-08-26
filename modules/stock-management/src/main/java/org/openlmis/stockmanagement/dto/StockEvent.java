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
import lombok.NoArgsConstructor;
import org.apache.commons.collections.map.DefaultedMap;
import org.apache.commons.lang3.StringUtils;
import org.openlmis.core.domain.StockAdjustmentReason;
import org.openlmis.core.hash.Encoder;
import org.openlmis.core.serializer.DateDeserializer;
import org.openlmis.core.serializer.DateTimeDeserializer;
import org.openlmis.stockmanagement.domain.Lot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode()
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockEvent {

    private StockEventType type;
    private Long facilityId;
    private String productCode;

    @JsonDeserialize(using = DateDeserializer.class)
    private Date occurred;

    @JsonDeserialize(using = DateTimeDeserializer.class)
    private Date createdTime;

    private Long quantity;
    private Long lotId;
    private Lot lot;
    private String reasonName;
    private String referenceNumber;

    private Long requestedQuantity;

    private List<LotEvent> lotEventList;

    private Map<String, String> customProps;

    public long getQuantity() {
        return Math.abs(quantity);
    }

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
     *
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

    public String getSyncUpHash() {
        Map decoratedProps = DefaultedMap.decorate(customProps == null ? new HashMap() : customProps, "");
        String eventContentString = this.facilityId.toString() + this.type + this.productCode +
                this.occurred + this.createdTime + this.quantity +
                this.reasonName + this.referenceNumber + decoratedProps.get("SOH");
        return Encoder.hash(eventContentString);
    }

    private boolean hasFacility() {
        return null != facilityId;
    }
}
