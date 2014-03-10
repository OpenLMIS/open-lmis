/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityFtpDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Exposes the services for handling FacilityFtpDetails entity.
 */

@Service
@NoArgsConstructor
public class FacilityFtpDetailsService {

  @Autowired
  FacilityFtpDetailsRepository repository;

  @Autowired
  FacilityService facilityService;

  public void save(FacilityFtpDetails facilityFtpDetails) {
    Facility existingFacility = facilityService.getByCode(facilityFtpDetails.getFacility());
    if (existingFacility == null) {
      throw new DataException("error.facility.code.invalid");
    }
    if (facilityFtpDetails.getId() != null) {
      update(facilityFtpDetails);
      return;
    }

    facilityFtpDetails.setFacility(existingFacility);
    insert(facilityFtpDetails);
  }

  public void update(FacilityFtpDetails facilityFtpDetails) {
    repository.update(facilityFtpDetails);
  }

  public void insert(FacilityFtpDetails facilityFtpDetails) {
    repository.insert(facilityFtpDetails);
  }

  public FacilityFtpDetails getByFacilityCode(Facility facility) {
    return getByFacilityId(facilityService.getByCode(facility));
  }

  public FacilityFtpDetails getByFacilityId(Facility facility) {
    return repository.getByFacilityId(facility);
  }
}
