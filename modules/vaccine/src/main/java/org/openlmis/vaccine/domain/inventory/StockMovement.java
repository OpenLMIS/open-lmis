package org.openlmis.vaccine.domain.inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openlmis.core.domain.BaseModel;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockMovement extends BaseModel {

    private Long fromFacilityId;
    private Long toFacilityId;
    private StockMovementType type;

    private Date initiatedDate;
    private Date shippedDate;
    private Date expectedDate;
    private Date receivedDate;
}
