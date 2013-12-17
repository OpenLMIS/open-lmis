/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.EpiUse;
import org.openlmis.distribution.domain.FacilityDistribution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@NoArgsConstructor
public class FacilityDistributionService {

  @Autowired
  FacilityService facilityService;

  @Autowired
  EpiUseService epiUseService;

  @Autowired
  FacilityVisitService facilityVisitService;

  public Map<Long, FacilityDistribution> createFor(Distribution distribution) {
    Long deliveryZoneId = distribution.getDeliveryZone().getId();
    Long programId = distribution.getProgram().getId();

    Map<Long, FacilityDistribution> facilityDistributions = new HashMap<>();

    List<Facility> facilities = facilityService.getAllForDeliveryZoneAndProgram(deliveryZoneId, programId);
    for (Facility facility : facilities) {
      facilityDistributions.put(facility.getId(), createDistributionData(facility, distribution));
    }

    return facilityDistributions;
  }

  FacilityDistribution createDistributionData(Facility facility, Distribution distribution) {
    EpiUse epiUse = new EpiUse(facility, distribution);
    epiUseService.save(epiUse);
    return new FacilityDistribution(null, epiUse);
  }

  public boolean save(FacilityDistribution facilityDistribution) {
    epiUseService.save(facilityDistribution.getEpiUse());

    return facilityVisitService.save(facilityDistribution.getFacilityVisit());
  }
}
