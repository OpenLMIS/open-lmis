/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.service.FacilityService;
import org.openlmis.core.service.ProgramSupportedService;
import org.openlmis.upload.model.AuditFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProgramSupportedPersistenceHandler extends AbstractModelPersistenceHandler {

  @Autowired
  private ProgramSupportedService service;

  @Autowired
  private FacilityService facilityService;


  @Override
  protected BaseModel getExisting(BaseModel record) {
    return service.getProgramSupported((ProgramSupported) record);
  }

  @Override
  protected void save(BaseModel record) {
    service.uploadSupportedProgram((ProgramSupported) record);
  }

  @Override
  public void postProcess(AuditFields auditFields) {
    List<Facility> facilities = facilityService.getAllByProgramSupportedModifiedDate(auditFields.getCurrentTimestamp());
    for (Facility facility : facilities) {
      service.notifyProgramSupportedUpdated(facility);
    }
  }

  @Override
  public String getMessageKey() {
    return "error.duplicate.program.supported";
  }

}
