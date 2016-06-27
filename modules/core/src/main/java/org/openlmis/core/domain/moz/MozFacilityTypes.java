package org.openlmis.core.domain.moz;

public enum MozFacilityTypes {
    DNM("DNM"),//national
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
}
