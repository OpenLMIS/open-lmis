/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.web.model;

import org.openlmis.core.domain.FacilityOperator;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.Program;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * This class represents the reference data related to facility.
 */

public class FacilityReferenceData {

  public static final String FACILITY_TYPES = "facilityTypes";
  public static final String FACILITY_OPERATORS = "facilityOperators";
  public static final String GEOGRAPHIC_ZONES = "geographicZones";
  public static final String PROGRAMS = "programs";
  MultiValueMap referenceData = new LinkedMultiValueMap<>();


  public FacilityReferenceData addFacilityTypes(List<FacilityType> facilityTypes) {
    referenceData.put(FACILITY_TYPES, facilityTypes);
    return this;
  }

  public FacilityReferenceData addFacilityOperators(List<FacilityOperator> allOperators) {
    referenceData.put(FACILITY_OPERATORS, allOperators);
    return this;
  }

  public FacilityReferenceData addGeographicZones(List<GeographicZone> allZones) {
    referenceData.put(GEOGRAPHIC_ZONES, allZones);
    return this;
  }

  public MultiValueMap get() {
    return referenceData;
  }

  public FacilityReferenceData addPrograms(List<Program> programs) {
    referenceData.put(PROGRAMS, programs);
    return this;
  }
}
