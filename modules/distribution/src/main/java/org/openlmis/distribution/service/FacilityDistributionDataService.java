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
import org.openlmis.core.domain.FacilityProgramProduct;
import org.openlmis.core.domain.ProductGroup;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.service.FacilityService;
import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.EpiUse;
import org.openlmis.distribution.domain.EpiUseLineItem;
import org.openlmis.distribution.domain.FacilityDistribution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@NoArgsConstructor
public class FacilityDistributionDataService {

  @Autowired
  FacilityService facilityService;

  @Autowired
  EpiUseService epiUseService;

  public Map<Long, FacilityDistribution> getFor(Distribution distribution) {
    Long deliveryZoneId = distribution.getDeliveryZone().getId();
    Long programId = distribution.getProgram().getId();

    Map<Long, FacilityDistribution> facilityDistributions = new HashMap<>();

    List<Facility> facilities = facilityService.getAllForDeliveryZoneAndProgram(deliveryZoneId, programId);
    for (Facility facility : facilities) {
      facilityDistributions.put(facility.getId(), createDistributionData(facility, distribution));
    }
    return facilityDistributions;
  }

  public FacilityDistribution createDistributionData(Facility facility, Distribution distribution) {

    return new FacilityDistribution(null, createEpiUse(facility, distribution));
  }

  private EpiUse createEpiUse(Facility facility, Distribution distribution) {
    Set<ProductGroup> productGroupSet = new HashSet<>();
    List<EpiUseLineItem> epiUseLineItems = new ArrayList<>();

    EpiUse epiUse = new EpiUse(distribution.getId(), facility.getId(), epiUseLineItems);

    if (facility.getSupportedPrograms().size() != 0) {
      ProgramSupported programSupported = facility.getSupportedPrograms().get(0);
      for (FacilityProgramProduct facilityProgramProduct : programSupported.getProgramProducts()) {
        if (facilityProgramProduct.isActive() && facilityProgramProduct.getProduct().getActive()) {
          ProductGroup productGroup = facilityProgramProduct.getProduct().getProductGroup();
          if (productGroup != null && productGroupSet.add(productGroup)) {
            epiUseLineItems.add(new EpiUseLineItem(productGroup));
          }
        }
      }
      epiUseService.saveLineItems(epiUse);
    }
    return epiUse;
  }
}
