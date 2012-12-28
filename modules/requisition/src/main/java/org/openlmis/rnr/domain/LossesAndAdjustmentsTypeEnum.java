package org.openlmis.rnr.domain;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(using = LossesAndAdjustmentsTypeEnumSerializer.class)
@JsonDeserialize(using = LossesAndAdjustmentsTypeEnumDeSerializer.class)
public enum LossesAndAdjustmentsTypeEnum {

    TRANSFER_IN("Transfer In"),
    TRANSFER_OUT("Transfer Out"),
    DAMAGED("Damaged"),
    LOST("Lost"),
    STOLEN("Stolen"),
    EXPIRED("Expired"),
    PASSED_OPEN_VIAL_TIME_LIMIT("Passed Open-Vial Time Limit"),
    COLD_CHAIN_FAILURE("Cold Chain Failure"),
    CLINIC_RETURN("Clinic Return");

    private String description;

    LossesAndAdjustmentsTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
