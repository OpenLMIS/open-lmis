/*
 *
 *  * This program is part of the OpenLMIS logistics management information system platform software.
 *  * Copyright © 2013 VillageReach
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  *  
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 *
 */

package org.openlmis.distribution.service;

import org.openlmis.distribution.domain.Distribution;
import org.openlmis.distribution.domain.FacilityDistribution;
import org.openlmis.distribution.repository.DistributionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class DistributionService {

  @Autowired
  FacilityVisitService facilityVisitService;

  @Autowired
  FacilityDistributionService facilityDistributionService;

  @Autowired
  DistributionRepository repository;

  public Distribution create(Distribution distribution) {
    Distribution savedDistribution = repository.create(distribution);
    Map<Long, FacilityDistribution> facilityDistributions = facilityDistributionService.getFor(distribution);
    savedDistribution.setFacilityDistributions(facilityDistributions);
    return savedDistribution;
  }

  public Distribution get(Distribution distribution) {
    return repository.get(distribution);
  }

  @Transactional
  //TODO return boolean or status object
  public String sync(FacilityDistribution facilityDistributionData) {
    return facilityVisitService.save(facilityDistributionData.getFacilityVisit());
  }
}
