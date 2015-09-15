package org.openlmis.vaccine.domain.inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openlmis.core.domain.BaseModel;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockCardEntry extends BaseModel {

    Long stockCardId;

    Long lotOnHandId;

    StockCardEntryType type;

    Long quantity;

    String referenceNumber;

    String adjustmentReason;

    String notes;
}