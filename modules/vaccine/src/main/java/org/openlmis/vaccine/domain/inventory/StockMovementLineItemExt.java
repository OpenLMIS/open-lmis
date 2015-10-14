package org.openlmis.vaccine.domain.inventory;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.serializer.DateDeserializer;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockMovementLineItemExt extends BaseModel{

  private Long stockMovementLineItemId;
  private String issueVoucher;
  private String issueDate;
  private Long productId;
  private Long dosesRequested;
  private Long gap;
  private String toFacilityName;
  private Long productCategoryId;
  private Long quantityOnHand;





}
