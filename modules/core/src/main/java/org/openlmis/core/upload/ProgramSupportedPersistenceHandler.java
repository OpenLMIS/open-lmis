/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.domain.User;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.FacilityService;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;
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
  protected Importable getExisting(Importable importable) {
    return facilityService.getProgramSupported((ProgramSupported) importable);
  }

  @Override
  protected void save(Importable existingRecord, Importable currentRecord, AuditFields auditFields) {
    ProgramSupported programSupported = (ProgramSupported) currentRecord;
    programSupported.setModifiedBy(auditFields.getUser());
    programSupported.setModifiedDate(auditFields.getCurrentTimestamp());
    if(existingRecord != null) programSupported.setId(((ProgramSupported) existingRecord).getId());
    facilityService.uploadSupportedProgram(programSupported);
  }

}
