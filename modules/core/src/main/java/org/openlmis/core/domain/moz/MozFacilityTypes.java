package org.openlmis.core.domain.moz;

public enum MozFacilityTypes {
    DPM("DPM"),
    DDM("DDM"),
    CSRUR_I("CSRUR-I"),
    CSRUR_II("CSRUR-II"),;

    private String code;

    MozFacilityTypes(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}
