/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
  public static Property<Facility, Long> typeId = newProperty();
  private static Property<Facility, Long> operatedById = newProperty();
  public static final Property<Facility, Boolean> dataReportable = newProperty();
  public static final Property<Facility, List<ProgramSupported>> programSupportedList = newProperty();
  public static final Property<Facility, Date> modifiedDate = newProperty();

  public static final String FACILITY_CODE = "F10010";
  public static final String FACILITY_TYPE_CODE = "warehouse";
  public static final Long FACILITY_TYPE_ID = 1L;
  public static final Long GEOGRAPHIC_ZONE_ID = 3L;

  public static final String GEOGRAPHIC_ZONE_CODE = "GEOGRAPHIC_ZONE_CODE";
  public static final List EMPTY_LIST = new ArrayList<>();
  public static final Instantiator<Facility> defaultFacility = new Instantiator<Facility>() {

    @Override
    public Facility instantiate(PropertyLookup<Facility> lookup) {
      Facility facility = new Facility();
      facility.setCode(lookup.valueOf(code, FACILITY_CODE));
      FacilityType facilityType = new FacilityType();
      facilityType.setCode(lookup.valueOf(type, FACILITY_TYPE_CODE));
      facilityType.setName("Central Warehouse");
      facilityType.setId(lookup.valueOf(typeId, FACILITY_TYPE_ID));
      facilityType.setNominalMaxMonth(100);
      facilityType.setNominalEop(50.5);
      facility.setFacilityType(facilityType);
      facility.setName(lookup.valueOf(name, "Apollo Hospital"));

      GeographicZone geographicZone = new GeographicZone();
      geographicZone.setLevel(new GeographicLevel(1L, "levelCode", "levelName", 4));
      geographicZone.setId(lookup.valueOf(geographicZoneId, GEOGRAPHIC_ZONE_ID));
      geographicZone.setCode(lookup.valueOf(geographicZoneCode, GEOGRAPHIC_ZONE_CODE));
      geographicZone.setName("Lusaka");
      GeographicZone parentGeographicZone = new GeographicZone();
      parentGeographicZone.setLevel(new GeographicLevel(2L, "parentLevelCode", "parentLevelName", 3));
      parentGeographicZone.setId(lookup.valueOf(geographicZoneId, GEOGRAPHIC_ZONE_ID));
      parentGeographicZone.setCode(lookup.valueOf(geographicZoneCode, GEOGRAPHIC_ZONE_CODE));
      parentGeographicZone.setName("Zambia");
      geographicZone.setParent(parentGeographicZone);
      facility.setGeographicZone(lookup.valueOf(FacilityBuilder.geographicZone, geographicZone));
      facility.setVirtualFacility(lookup.valueOf(virtualFacility, false));

      facility.setSdp(lookup.valueOf(sdp, true));
      facility.setActive(lookup.valueOf(active, true));
      facility.setDataReportable(lookup.valueOf(dataReportable, true));
      FacilityOperator operatedBy = new FacilityOperator();
      operatedBy.setCode(lookup.valueOf(operatedByCode, "MoH"));
      operatedBy.setId(lookup.valueOf(operatedById, 1L));
      operatedBy.setText("MOH");
      facility.setOperatedBy(operatedBy);
      facility.setGoLiveDate(lookup.valueOf(goLiveDate, new LocalDate(2013, 10, 10).toDate()));
      facility.setSupportedPrograms(lookup.valueOf(programSupportedList, EMPTY_LIST));
      facility.setModifiedBy(1L);
      facility.setModifiedDate(lookup.valueOf(modifiedDate, new Date()));
      return facility;
    }
  };
}
