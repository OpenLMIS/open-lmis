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
public class FacilityDistributionService {

  @Autowired
  FacilityService facilityService;

  @Autowired
  EpiUseService epiUseService;

  @Autowired
  FacilityVisitService facilityVisitService;

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

  public boolean save(FacilityDistribution facilityDistribution) {

    epiUseService.save(facilityDistribution.getEpiUse());

    return facilityVisitService.save(facilityDistribution.getFacilityVisit());
  }

  private EpiUse createEpiUse(Facility facility, Distribution distribution) {
    List<EpiUseLineItem> epiUseLineItems = new ArrayList<>();
    EpiUse epiUse = new EpiUse(distribution.getId(), facility.getId(), epiUseLineItems);

    if (facility.getSupportedPrograms().size() != 0) {
      List<FacilityProgramProduct> programProducts = facility.getSupportedPrograms().get(0).getProgramProducts();
      populateEpiUseLineItems(programProducts, epiUseLineItems);
    }

    epiUse.setLineItems(epiUseLineItems);
    epiUseService.save(epiUse);

    return epiUse;
  }

  private List<EpiUseLineItem> populateEpiUseLineItems(List<FacilityProgramProduct> programProducts, List<EpiUseLineItem> epiUseLineItems) {
    Set<ProductGroup> productGroupSet = new HashSet<>();

    for (FacilityProgramProduct facilityProgramProduct : programProducts) {
      ProductGroup productGroup = facilityProgramProduct.getActiveProductGroup();
      if (productGroup != null && productGroupSet.add(productGroup)) {
        epiUseLineItems.add(new EpiUseLineItem(productGroup));
      }
    }

    return epiUseLineItems;
  }
}
