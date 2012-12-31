package org.openlmis.web.model;

import org.openlmis.rnr.domain.LossesAndAdjustmentsType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

public class RnrReferenceData {

    public static final String LOSSES_AND_ADJUSTMENTS_TYPES = "lossAdjustmentTypes";
    MultiValueMap referenceData = new LinkedMultiValueMap<>();


    public RnrReferenceData addLossesAndAdjustmentsTypes(List<LossesAndAdjustmentsType> lossesAndAdjustmentsTypes) {
        referenceData.put(LOSSES_AND_ADJUSTMENTS_TYPES, lossesAndAdjustmentsTypes);
        return this;
    }


    public MultiValueMap get() {
        return referenceData;
    }
}
