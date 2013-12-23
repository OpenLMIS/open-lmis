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

import org.openlmis.core.service.RefrigeratorService;
import org.openlmis.distribution.domain.DistributionRefrigerators;
import org.openlmis.distribution.domain.RefrigeratorReading;
import org.openlmis.distribution.repository.DistributionRefrigeratorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistributionRefrigeratorsService {

  @Autowired
  private DistributionRefrigeratorsRepository repository;

  @Autowired
  private RefrigeratorService refrigeratorService;

  public void save(DistributionRefrigerators distributionRefrigerators) {
    repository.save(distributionRefrigerators);

    for (RefrigeratorReading reading : distributionRefrigerators.getReadings()) {

      reading.setDistributionRefrigeratorsId(distributionRefrigerators.getId());
      repository.saveReading(reading);

      refrigeratorService.update(reading.getRefrigerator());
    }
  }
}
