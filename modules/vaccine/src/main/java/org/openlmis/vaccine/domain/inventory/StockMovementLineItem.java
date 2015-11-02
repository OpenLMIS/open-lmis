package org.openlmis.vaccine.domain.inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openlmis.core.domain.BaseModel;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockMovementLineItem extends BaseModel{

    private Long stockMovementId;
    private Long lotId;
    private  Long quantity;
    private String notes;


}
