/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.FacilityFtpDetails;
import org.openlmis.core.service.FacilityFtpDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class FacilityFtpDetailsPersistenceHandler extends AbstractModelPersistenceHandler {

  @Autowired
  private FacilityFtpDetailsService facilityFtpDetailsService;

  @Override
  protected BaseModel getExisting(BaseModel record) {
    FacilityFtpDetails facilityFtpDetails = (FacilityFtpDetails) record;
    return facilityFtpDetailsService.getByFacilityCode(facilityFtpDetails.getFacilityCode());
  }

  @Override
  protected void save(BaseModel record) {
    facilityFtpDetailsService.save((FacilityFtpDetails) record);
  }

  @Override
  public String getMessageKey() {
    return "error.duplicate.facility.code";
  }


}
