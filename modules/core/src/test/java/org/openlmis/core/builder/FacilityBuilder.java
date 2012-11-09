package org.openlmis.core.builder;

import org.openlmis.core.domain.Facility;

public class FacilityBuilder {

    Facility facility = new Facility();

    public FacilityBuilder withDefaults() {
        facility.setCode("F10010");
        facility.setName("hiv");
        facility.setType(1);
        facility.setGeographicZone(2);
        return this;
    }

    public FacilityBuilder withCode(String code) {
        facility.setCode(code);
        return this;
    }

    public FacilityBuilder withName(String name) {
        facility.setName(name);
        return this;
    }

    public FacilityBuilder withType(int type) {
        facility.setType(type);
        return this;
    }

    public FacilityBuilder withGZone(int gZone) {
        facility.setGeographicZone(gZone);
        return this;
    }

    public Facility build() {
        return facility;
    }
}
