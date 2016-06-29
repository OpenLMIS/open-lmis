package org.openlmis.core.domain.moz;

public enum MozFacilityTypes {
    Central("Central"),//national
    DPM("DPM"),//province
    DDM("DDM"),//district
    CSRUR_I("CSRUR-I"),//facility
    CSRUR_II("CSRUR-II"),;//facility

    private String code;

    MozFacilityTypes(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }

    public static MozFacilityTypes getEnum(String value) {
        for (MozFacilityTypes v : values()) {
            if (v.code.equalsIgnoreCase(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException();
    }
}
