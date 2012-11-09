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
            return new Facility(lookup.valueOf(code, "F10010"),
                    lookup.valueOf(name, "hiv"),
                    lookup.valueOf(type, 1),
                    lookup.valueOf(geographicZone, 2));
        }
    };
}
