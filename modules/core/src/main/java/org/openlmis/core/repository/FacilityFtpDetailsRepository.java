/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.openlmis.core.repository.mapper.FacilityFtpDetailsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class FacilityFtpDetailsRepository {

  @Autowired
  FacilityFtpDetailsMapper mapper;

  public void update(FacilityFtpDetails facilityFtpDetails) {
    mapper.update(facilityFtpDetails);
  }

  public void insert(FacilityFtpDetails facilityFtpDetails) {
    mapper.insert(facilityFtpDetails);
  }

  public FacilityFtpDetails getByFacilityId(Facility facility) {
    return mapper.getByFacilityId(facility);
  }

}
