package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.joda.time.LocalDate;
import org.openlmis.core.domain.Facility;

import java.util.Date;

import static com.natpryce.makeiteasy.Property.newProperty;

public class FacilityBuilder {

    public static final Property<Facility, Integer> id = newProperty();
    public static final Property<Facility, String> code = newProperty();
    public static final Property<Facility, String> name = newProperty();
    public static final Property<Facility, String> type = newProperty();
    public static final Property<Facility, Integer> geographicZone = newProperty();
    public static final Property<Facility, Boolean> sdp = newProperty();
    public static final Property<Facility, Boolean> active = newProperty();
    public static final Property<Facility, Date> goLiveDate = newProperty();
    public static final Property<Facility,String> operatedBy = newProperty();

    public static final String FACILITY_CODE = "F10010";
    public static final int FACILITY_ID = 1;
    public static final String FACILITY_TYPE = "warehouse";

    public static final Instantiator<Facility> defaultFacility = new Instantiator<Facility>() {

        @Override
        public Facility instantiate(PropertyLookup<Facility> lookup) {
            Facility facility = new Facility();
            facility.setId(lookup.valueOf(id, FACILITY_ID));
            facility.setCode(lookup.valueOf(code, FACILITY_CODE));
            facility.setFacilityTypeCode(lookup.valueOf(type, FACILITY_TYPE));
            facility.setName(lookup.valueOf(name, "Apollo Hospital"));
            facility.setGeographicZone(lookup.valueOf(geographicZone, 2));
            facility.setSdp(lookup.valueOf(sdp, true));
            facility.setActive(lookup.valueOf(active, true));
            facility.setOperatedBy(lookup.valueOf(operatedBy, "MoH"));
            facility.setGoLiveDate(lookup.valueOf(goLiveDate, new LocalDate(2013, 10, 10).toDate()));
            facility.setModifiedBy("user");
            return facility;
        }
    };
}
