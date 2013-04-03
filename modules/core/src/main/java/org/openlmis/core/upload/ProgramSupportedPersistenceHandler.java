/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("programSupportedPersistenceHandler")
public class ProgramSupportedPersistenceHandler extends AbstractModelPersistenceHandler {

  public static final String FACILITY_ALREADY_MAPPED_TO_PROGRAM = "Facility has already been mapped to the program ";
  private FacilityService facilityService;

  @Autowired
  public ProgramSupportedPersistenceHandler(FacilityService facilityService) {
    super(FACILITY_ALREADY_MAPPED_TO_PROGRAM);
    this.facilityService = facilityService;
  }

  @Override
  protected BaseModel getExisting(BaseModel record) {
    return facilityService.getProgramSupported((ProgramSupported) record);
  }

  @Override
  protected void save(BaseModel record) {
    facilityService.uploadSupportedProgram((ProgramSupported) record);
  }

}
