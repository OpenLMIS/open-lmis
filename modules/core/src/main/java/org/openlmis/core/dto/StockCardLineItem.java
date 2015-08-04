package org.openlmis.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.StockCardLineItemType;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class StockCardLineItem extends BaseModel {

  StockCardLineItemType type;

  Long quantity;

  String referenceNumber;

  String adjustmentReason;

  String notes;
}
