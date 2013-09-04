/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.FacilityFtpDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
