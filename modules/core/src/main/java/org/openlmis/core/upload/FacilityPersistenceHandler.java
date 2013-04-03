/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("facilityPersistenceHandler")
@NoArgsConstructor
public class FacilityPersistenceHandler extends AbstractModelPersistenceHandler {

  public static final String DUPLICATE_FACILITY_CODE = "Duplicate Facility Code";
  private FacilityService facilityService;

  @Autowired
  public FacilityPersistenceHandler(FacilityService facilityService) {
    super(DUPLICATE_FACILITY_CODE);
    this.facilityService = facilityService;
  }

  @Override
  protected BaseModel getExisting(BaseModel record) {
    return facilityService.getByCode((Facility) record);
  }

  @Override
  protected void save(BaseModel record) {
    facilityService.save((Facility) record);
  }

}
