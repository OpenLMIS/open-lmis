/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.model;

import org.openlmis.core.domain.FacilityOperator;
import org.openlmis.core.domain.FacilityType;
import org.openlmis.core.domain.GeographicZone;
import org.openlmis.core.domain.Program;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

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
