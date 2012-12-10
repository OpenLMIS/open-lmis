package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.joda.time.LocalDate;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityOperator;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.domain.GeographicZone;

import java.util.Date;

import static com.natpryce.makeiteasy.Property.newProperty;

public class FacilityBuilder {

    public static final Property<Facility, String> code = newProperty();
    public static final Property<Facility, String> name = newProperty();
    public static final Property<Facility, String> type = newProperty();
    public static final Property<Facility, Long> geographicZoneId = newProperty();
    public static final Property<Facility, Boolean> sdp = newProperty();
    public static final Property<Facility, Boolean> active = newProperty();
    public static final Property<Facility, Date> goLiveDate = newProperty();
    public static final Property<Facility,String> operatedByCode = newProperty();
    public static final Property<Facility,GeographicZone> geographicZone= newProperty();

    public static final String FACILITY_CODE = "F10010";
    public static final String FACILITY_TYPE_CODE = "warehouse";

    public static final Instantiator<Facility> defaultFacility = new Instantiator<Facility>() {

        @Override
        public Facility instantiate(PropertyLookup<Facility> lookup) {
            Facility facility = new Facility();
            facility.setCode(lookup.valueOf(code, FACILITY_CODE));
            FacilityType facilityType = new FacilityType();
            facilityType.setCode(lookup.valueOf(type, FACILITY_TYPE_CODE));
            facility.setFacilityType(facilityType);
            facility.setName(lookup.valueOf(name, "Apollo Hospital"));
            GeographicZone geographicZoneValue = new GeographicZone();
            geographicZoneValue.setId(lookup.valueOf(geographicZoneId, 2L));
            facility.setGeographicZone(lookup.valueOf(geographicZone, geographicZoneValue));
            facility.setSdp(lookup.valueOf(sdp, true));
            facility.setActive(lookup.valueOf(active, true));
            FacilityOperator operatedBy = new FacilityOperator();
            operatedBy.setCode(lookup.valueOf(operatedByCode, "MoH"));
            facility.setOperatedBy(operatedBy);
            facility.setGoLiveDate(lookup.valueOf(goLiveDate, new LocalDate(2013, 10, 10).toDate()));
            facility.setModifiedBy("user");
            return facility;
        }
    };
}
