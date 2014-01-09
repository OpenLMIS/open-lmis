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
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.Refrigerator;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.RefrigeratorService;
import org.openlmis.distribution.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections.CollectionUtils.collect;
import static org.apache.commons.collections.CollectionUtils.select;

@Service
@NoArgsConstructor
public class FacilityDistributionService {

  @Autowired
  private FacilityService facilityService;

  @Autowired
  private RefrigeratorService refrigeratorService;

  @Autowired
  private EpiUseService epiUseService;

  @Autowired
  private FacilityVisitService facilityVisitService;

  @Autowired
  private DistributionRefrigeratorsService distributionRefrigeratorsService;

  @Autowired
  private EpiInventoryService epiInventoryService;

  @Autowired
  private VaccinationCoverageService vaccinationCoverageService;

  public Map<Long, FacilityDistribution> createFor(Distribution distribution) {
    Long deliveryZoneId = distribution.getDeliveryZone().getId();
    Long programId = distribution.getProgram().getId();

    Map<Long, FacilityDistribution> facilityDistributions = new HashMap<>();

    List<Facility> facilities = facilityService.getAllForDeliveryZoneAndProgram(deliveryZoneId, programId);
    List<Refrigerator> distributionRefrigerators = refrigeratorService.getRefrigeratorsForADeliveryZoneAndProgram(deliveryZoneId, programId);

    for (Facility facility : facilities) {
      facilityDistributions.put(facility.getId(), createDistributionData(facility, distribution, distributionRefrigerators));
    }

    return facilityDistributions;
  }

  FacilityDistribution createDistributionData(final Facility facility, Distribution distribution, List<Refrigerator> refrigerators) {
    List<RefrigeratorReading> refrigeratorReadings = (List) collect(select(refrigerators, new Predicate() {
      @Override
      public boolean evaluate(Object o) {
        return ((Refrigerator) o).getFacilityId().equals(facility.getId());
      }
    }), new Transformer() {
      @Override
      public Object transform(Object o) {
        return new RefrigeratorReading((Refrigerator) o);
      }
    });

    FacilityVisit facilityVisit = new FacilityVisit(distribution.getId(), facility.getId(), distribution.getCreatedBy());
    facilityVisitService.save(facilityVisit);
    FacilityDistribution facilityDistribution = new FacilityDistribution(facilityVisit, facility, distribution, refrigeratorReadings);
    epiUseService.save(facilityDistribution.getEpiUse());
    epiInventoryService.save(facilityDistribution.getEpiInventory());
    return facilityDistribution;
  }

  public boolean save(FacilityDistribution facilityDistribution) {
    epiUseService.save(facilityDistribution.getEpiUse());
    distributionRefrigeratorsService.save(facilityDistribution.getFacilityVisit().getFacilityId(), facilityDistribution.getRefrigerators());
    vaccinationCoverageService.save(facilityDistribution.getCoverage());
    return facilityVisitService.save(facilityDistribution.getFacilityVisit());
  }

  public Map<Long, FacilityDistribution> get(Distribution distribution) {
    Long deliveryZoneId = distribution.getDeliveryZone().getId();
    Long programId = distribution.getProgram().getId();

    Map<Long, FacilityDistribution> facilityDistributions = new HashMap<>();

    List<Facility> facilities = facilityService.getAllForDeliveryZoneAndProgram(deliveryZoneId, programId);
    for (Facility facility : facilities) {
      facilityDistributions.put(facility.getId(), getDistributionData(facility, distribution));
    }

    return facilityDistributions;

  }

  private FacilityDistribution getDistributionData(Facility facility, Distribution distribution) {
    FacilityVisit facilityVisit = facilityVisitService.getBy(facility.getId(), distribution.getId());
    EpiUse epiUse = epiUseService.getBy(facilityVisit.getId());
    DistributionRefrigerators refrigerators = distributionRefrigeratorsService.getBy(facilityVisit.getId());
    EpiInventory epiInventory = epiInventoryService.getBy(facilityVisit.getId());
    VaccinationCoverage coverage = vaccinationCoverageService.getBy(facilityVisit.getId());

    return new FacilityDistribution(facilityVisit, epiUse, refrigerators, epiInventory, coverage);
  }
}
