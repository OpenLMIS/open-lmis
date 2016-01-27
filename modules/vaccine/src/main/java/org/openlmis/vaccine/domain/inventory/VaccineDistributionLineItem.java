package org.openlmis.vaccine.domain.inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openlmis.core.domain.BaseModel;

import java.util.List;

/**
 * Created by chrispinus on 10/22/15.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VaccineDistributionLineItem extends BaseModel {

    List<VaccineDistributionLineItemLot> lots;
    private Long distributionId;
    private Long productId;
    private Long quantity;
    private Integer vvmStatus;
}

