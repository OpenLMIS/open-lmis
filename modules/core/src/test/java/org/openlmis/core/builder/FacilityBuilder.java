package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.core.domain.Facility;

import static com.natpryce.makeiteasy.Property.newProperty;

public class FacilityBuilder {

    public static final Property<Facility, String> code = newProperty();
    public static final Property<Facility, String> name = newProperty();
    public static final Property<Facility, Integer> type = newProperty();
    public static final Property<Facility, Integer> geographicZone = newProperty();

    public static final Instantiator<Facility> defaultFacility = new Instantiator<Facility>() {
        @Override
        public Facility instantiate(PropertyLookup<Facility> lookup) {
            Facility facility  = new Facility();
            facility.setCode(lookup.valueOf(code, "F10010"));
            facility.setType(lookup.valueOf(type, 1));
            facility.setName(lookup.valueOf(name, "Apollo Hospital"));
            facility.setGeographicZone(lookup.valueOf(geographicZone, 2));
            return facility;
        }
    };
}
