/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.joda.time.LocalDate;
import org.openlmis.core.domain.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.natpryce.makeiteasy.Property.newProperty;

public class FacilityBuilder {

  public static final Property<Facility, Long> facilityId = newProperty();
  public static final Property<Facility, String> code = newProperty();
  public static final Property<Facility, String> name = newProperty();
  public static final Property<Facility, String> type = newProperty();
  public static final Property<Facility, Long> geographicZoneId = newProperty();
  public static final Property<Facility, String> geographicZoneCode = newProperty();
  public static final Property<Facility, Boolean> sdp = newProperty();
  public static final Property<Facility, Boolean> active = newProperty();
  public static final Property<Facility, Boolean> virtualFacility = newProperty();
  public static final Property<Facility, Date> goLiveDate = newProperty();
  public static final Property<Facility, String> operatedByCode = newProperty();
  public static final Property<Facility, GeographicZone> geographicZone = newProperty();
  public static final Property<Facility, Long> typeId = newProperty();
  public static final Property<Facility, Long> operatedById = newProperty();
  public static final Property<Facility, Long> parentFacilityId = newProperty();
  public static final Property<Facility, Boolean> enabled = newProperty();
  public static final Property<Facility, List<ProgramSupported>> programSupportedList = newProperty();
  public static final Property<Facility, Date> modifiedDate = newProperty();

  public static final String FACILITY_CODE = "F10010";
  public static final String FACILITY_TYPE_CODE = "warehouse";
  public static final Long FACILITY_TYPE_ID = 1L;
  public static final Long PARENT_FACILITY_ID = null;
  public static final Long GEOGRAPHIC_ZONE_ID = 3L;
  public static final Long DEFAULT_FACILITY_ID = null;

  public static final String GEOGRAPHIC_ZONE_CODE = "GEOGRAPHIC_ZONE_CODE";
  public static final List EMPTY_LIST = new ArrayList<>();
  public static final Instantiator<Facility> defaultFacility = new Instantiator<Facility>() {

    @Override
    public Facility instantiate(PropertyLookup<Facility> lookup) {
      Facility facility = new Facility();
      facility.setId(lookup.valueOf(facilityId, DEFAULT_FACILITY_ID));
      facility.setCode(lookup.valueOf(code, FACILITY_CODE));
      FacilityType facilityType = new FacilityType();
      facilityType.setCode(lookup.valueOf(type, FACILITY_TYPE_CODE));
      facilityType.setName("Warehouse");
      facilityType.setId(lookup.valueOf(typeId, FACILITY_TYPE_ID));
      facilityType.setNominalMaxMonth(100);
      facilityType.setNominalEop(50.5);
      facility.setFacilityType(facilityType);
      facility.setName(lookup.valueOf(name, "Apollo Hospital"));

      //TODO refactor zone
      GeographicZone zone = new GeographicZone();
      zone.setLevel(new GeographicLevel(1L, "levelCode", "levelName", 4));
      zone.setId(lookup.valueOf(geographicZoneId, GEOGRAPHIC_ZONE_ID));
      zone.setCode(lookup.valueOf(geographicZoneCode, GEOGRAPHIC_ZONE_CODE));
      zone.setName("Arusha");
      GeographicZone parentGeographicZone = new GeographicZone();
      parentGeographicZone.setLevel(new GeographicLevel(2L, "parentLevelCode", "parentLevelName", 3));
      parentGeographicZone.setId(lookup.valueOf(geographicZoneId, GEOGRAPHIC_ZONE_ID));
      parentGeographicZone.setCode(lookup.valueOf(geographicZoneCode, GEOGRAPHIC_ZONE_CODE));
      parentGeographicZone.setName("Zambia");
      zone.setParent(parentGeographicZone);

      facility.setGeographicZone(lookup.valueOf(geographicZone, zone));
      facility.setVirtualFacility(lookup.valueOf(virtualFacility, false));

      facility.setSdp(lookup.valueOf(sdp, true));
      facility.setActive(lookup.valueOf(active, true));
      facility.setEnabled(lookup.valueOf(enabled, true));
      FacilityOperator operatedBy = new FacilityOperator();
      operatedBy.setCode(lookup.valueOf(operatedByCode, "MoH"));
      operatedBy.setId(lookup.valueOf(operatedById, 1L));
      operatedBy.setText("MOH");
      facility.setOperatedBy(operatedBy);
      facility.setParentFacilityId(lookup.valueOf(parentFacilityId, PARENT_FACILITY_ID));
      facility.setGoLiveDate(lookup.valueOf(goLiveDate, new LocalDate(2013, 10, 10).toDate()));
      facility.setSupportedPrograms(lookup.valueOf(programSupportedList, EMPTY_LIST));
      facility.setModifiedBy(1L);
      facility.setModifiedDate(lookup.valueOf(modifiedDate, new Date()));
      return facility;
    }
  };
}
