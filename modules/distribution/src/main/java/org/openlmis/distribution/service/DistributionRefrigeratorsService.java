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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openlmis.core.domain.Refrigerator;
import org.openlmis.core.service.RefrigeratorService;
import org.openlmis.distribution.domain.DistributionRefrigerators;
import org.openlmis.distribution.domain.FacilityVisit;
import org.openlmis.distribution.domain.RefrigeratorReading;
import org.openlmis.distribution.repository.DistributionRefrigeratorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistributionRefrigeratorsService {

  @Autowired
  private DistributionRefrigeratorsRepository repository;

  @Autowired
  private FacilityVisitService facilityVisitService;

  @Autowired
  private RefrigeratorService refrigeratorService;

  public void save(Long facilityId, DistributionRefrigerators distributionRefrigerators) {
    List<RefrigeratorReading> readings = distributionRefrigerators.getReadings();
    if (readings.size() == 0) {
      refrigeratorService.disableAllFor(facilityId);
      return;
    }
    FacilityVisit facilityVisit = facilityVisitService.getById(readings.get(0).getFacilityVisitId());
    if (facilityVisit.getSynced()) {
      return;
    }
    refrigeratorService.disableAllFor(facilityId);

    List<Refrigerator> refrigeratorsForFacility = refrigeratorService.getAllBy(facilityId);

    for (RefrigeratorReading reading : readings) {

      final Refrigerator refrigerator = reading.getRefrigerator();
      Refrigerator existingRefrigerator = (Refrigerator) CollectionUtils.find(refrigeratorsForFacility, new Predicate() {
        @Override
        public boolean evaluate(Object o) {
          return ((Refrigerator) o).getSerialNumber().equals(refrigerator.getSerialNumber());
        }
      });

      refrigerator.setEnabled(true);
      refrigerator.setModifiedBy(distributionRefrigerators.getCreatedBy());
      if (existingRefrigerator != null) {
        refrigerator.setId(existingRefrigerator.getId());
        refrigerator.setModifiedBy(distributionRefrigerators.getCreatedBy());
      } else {
        refrigerator.setFacilityId(facilityId);
        refrigerator.setCreatedBy(distributionRefrigerators.getCreatedBy());
      }
      refrigeratorService.save(refrigerator);

      repository.saveReading(reading);
    }
  }

  public DistributionRefrigerators getBy(Long facilityVisitId) {
    return repository.getBy(facilityVisitId);
  }
}
