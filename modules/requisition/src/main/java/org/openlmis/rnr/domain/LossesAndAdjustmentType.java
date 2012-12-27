package org.openlmis.rnr.domain;

public enum LossesAndAdjustmentType {

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

    LossesAndAdjustmentType(String description) {
        this.description = description;
    }
}
